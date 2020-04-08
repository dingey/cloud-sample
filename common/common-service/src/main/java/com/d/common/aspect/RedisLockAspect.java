package com.d.common.aspect;

import com.d.annotation.RedisLock;
import com.d.exception.CheckedException;
import com.d.util.AspectUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
@Aspect
@Order(1)
public class RedisLockAspect {
    private final RedisConnectionFactory connectionFactory;
    private RedisLockRegistry registry;

    @Autowired
    public RedisLockAspect(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @PostConstruct
    public void init() {
        registry = new RedisLockRegistry(connectionFactory, "lock_key");
        log.info("自定义redis锁初始化完毕。。。");
    }

    @Pointcut(value = "@annotation(redisLock)", argNames = "redisLock")
    public void pointcut(RedisLock redisLock) {
    }

    @Around(value = "pointcut(redisLock)", argNames = "pjp,redisLock")
    public Object around(ProceedingJoinPoint pjp, RedisLock redisLock) throws Throwable {
        if (condition(pjp, redisLock)) {
            log.debug("do lock.");
            String key = spelKey(pjp, redisLock);
            Lock lock = registry.obtain(key);
            if (lock.tryLock(redisLock.timeout(), TimeUnit.MILLISECONDS)) {
                try {
                    return pjp.proceed();
                } finally {
                    lock.unlock();
                }
            } else {
                if (redisLock.throwable()) {
                    if (redisLock.message().isEmpty()) {
                        throw new CheckedException("服务器繁忙，请稍后再试L。");
                    } else {
                        throw new CheckedException(AspectUtil.spel(pjp, redisLock.message(), String.class));
                    }
                }
                return null;
            }
        } else {
            return pjp.proceed();
        }
    }

    private boolean condition(ProceedingJoinPoint pjp, RedisLock redisLock) {
        return redisLock.condition().isEmpty() || AspectUtil.spel(pjp, redisLock.condition(), Boolean.class);
    }

    private String spelKey(ProceedingJoinPoint pjp, RedisLock redisLock) {
        if (redisLock.value().isEmpty()) {
            Method method = ((MethodSignature) pjp.getSignature()).getMethod();
            return String.format("%s:%s", method.getDeclaringClass().getName().replaceAll(".", ":"), method.getName());
        } else {
            return AspectUtil.spel(pjp, redisLock.value(), String.class);
        }
    }
}
