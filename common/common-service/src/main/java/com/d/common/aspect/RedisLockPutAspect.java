package com.d.common.aspect;

import com.d.annotation.RedisLockPut;
import com.d.exception.CheckedException;
import com.d.util.AspectUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * redis锁
 */
@Aspect
@Component
@Slf4j
public class RedisLockPutAspect {
    private final StringRedisTemplate srt;
    /**
     * 释放锁lua脚本
     */
    private static final String RELEASE_LOCK_LUA_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
    private ThreadLocal<String> lockValue = new ThreadLocal<>();
    private ConcurrentHashMap<String, Integer> lockCount = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("redis锁初始化完成");
    }

    @Autowired
    public RedisLockPutAspect(StringRedisTemplate srt) {
        this.srt = srt;
    }

    @Pointcut(value = "@annotation(redisLock)", argNames = "redisLock")
    public void pointcut(RedisLockPut redisLock) {
    }

    @Around(value = "pointcut(redisLock)", argNames = "pjp,redisLock")
    public Object around(ProceedingJoinPoint pjp, RedisLockPut redisLock) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        if (redisLock.condition().isEmpty() || AspectUtil.spel(pjp, redisLock.condition(), boolean.class)) {
            String key = redisLock.value().isEmpty() ? (method.getDeclaringClass().getName() + "." + method.getName()) : AspectUtil.spel(pjp, redisLock.value(), String.class);
            log.debug("lock method is ( {}.{} ) key is {}.", method.getDeclaringClass().getName(), method.getName(), key);
            if (tryLock(key, redisLock.timelock(), redisLock.reentrant())) {
                try {
                    return pjp.proceed();
                } finally {
                    if (redisLock.timelock() == 0L) {
                        unLock(key, redisLock.reentrant());
                    }
                }
            } else if (redisLock.timeout() > 0) {
                if (redisLock.spinLock() && trySpinLock(key, redisLock.timelock(), redisLock.timeout())) {
                    try {
                        return pjp.proceed();
                    } finally {
                        if (redisLock.timelock() == 0L) {
                            unLock(key, redisLock.reentrant());
                        }
                    }
                } else if (!redisLock.spinLock() && trySleepRetryLock(key, redisLock.timelock(), redisLock.timeout())) {
                    try {
                        return pjp.proceed();
                    } finally {
                        if (redisLock.timelock() == 0L) {
                            unLock(key, redisLock.reentrant());
                        }
                    }
                }
            }
        } else {
            return pjp.proceed();
        }
        if (redisLock.throwable()) {
            if (redisLock.message().isEmpty()) {
                throw new CheckedException("服务器繁忙，请稍后再试L。");
            } else {
                throw new CheckedException(redisLock.message().contains("#") ? AspectUtil.spel(pjp, redisLock.message(), String.class) : redisLock.message());
            }
        } else {
            return null;
        }
    }

    /**
     * 尝试获取锁,不可重入
     *
     * @param key             锁的key
     * @param lockMillisecond 锁的有效期：毫秒
     * @return 是否获取锁
     */
    private boolean tryLock(String key, long lockMillisecond, boolean reentrant) {
        String value = lockValue.get();
        if (StringUtils.isEmpty(lockValue)) {
            value = UUID.randomUUID().toString().replaceAll("-", "");
            lockValue.set(value);
        }
        Boolean setIfAbsent = srt.opsForValue().setIfAbsent(key, value, lockMillisecond, TimeUnit.MILLISECONDS);
        if (setIfAbsent == null) {
            return false;
        } else if (setIfAbsent) {
            return true;
        } else if (!reentrant) {
            return false;
        }

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(RELEASE_LOCK_LUA_SCRIPT, Long.class);
        Long result = srt.execute(redisScript, Collections.singletonList(key), lockValue);
        if (result == null) {
            return false;
        } else if (result > 0L) {
            Integer integer = lockCount.computeIfAbsent(key, k -> 0);
            lockCount.put(key, integer);
        }
        return result > 0L;
    }

    /**
     * 解除锁
     *
     * @param key 锁的key
     */
    private void unLock(String key, boolean reentrant) {
        if (reentrant) {
            Integer integer = lockCount.get(key);
            if (integer != null) {
                if (integer < 2) {
                    lockCount.remove(key);
                } else {
                    integer--;
                    lockCount.put(key, integer);
                }
            }
        }
        srt.delete(key);
    }

    /**
     * 自旋获取锁
     *
     * @param key             锁的key
     * @param lockMillisecond 锁的有效期：毫秒
     * @param timeout         超时时间：毫秒
     * @return 是否获取锁
     */
    private boolean trySpinLock(String key, long lockMillisecond, long timeout) {
        long start = System.currentTimeMillis();
        while (!tryLock(key, lockMillisecond, false)) {
            if ((System.currentTimeMillis() - start) > timeout) {
                return false;
            }
            try {
                Thread.sleep(10L);
            } catch (InterruptedException ignore) {
            }
        }
        return true;
    }

    /**
     * 如果锁失效时间在超时时间内，睡眠当前线程再尝试获取锁
     *
     * @param key             锁的key
     * @param lockMillisecond 锁的有效期：毫秒
     * @param timeout         超时时间：毫秒
     * @return 是否获取锁
     */
    private boolean trySleepRetryLock(String key, long lockMillisecond, long timeout) {
        boolean tryLock = tryLock(key, lockMillisecond, false);
        if (!tryLock) {
            //剩余生存时间
            Long ttl = srt.getExpire(key);
            if (ttl == null || ttl <= 0L) {
                tryLock = tryLock(key, lockMillisecond, false);
            } else if (ttl <= timeout) {
                try {
                    Thread.sleep(ttl);
                    tryLock = tryLock(key, lockMillisecond, false);
                } catch (InterruptedException e) {
                    return false;
                }
            }
        }
        return tryLock;
    }
}
