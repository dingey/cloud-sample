package com.d.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.objenesis.instantiator.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

@Slf4j
@SuppressWarnings("unchecked")
public abstract class BaseFallback {
    public <T> Result<T> fallback(Object... args) {
        error(args);
        return new Result(ResultCode.SERVER_FALLBACK.getCode(), "服务熔断", empty());
    }

    private StackTraceElement getTrace() {
        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        return traces[4];
    }

    private void error(Object... args) {
        StackTraceElement trace = getTrace();
        String methodName = trace.getMethodName();
        Class<Object> existingClass = ClassUtils.getExistingClass(this.getClass().getClassLoader(), trace.getClassName());
        log.error("服务被熔断，类名：{} 方法：{} 参数：{}", existingClass.getSimpleName(), methodName, args);
    }

    private Object empty() {
        StackTraceElement trace = getTrace();
        String methodName = trace.getMethodName();
        Class<Object> existingClass = ClassUtils.getExistingClass(this.getClass().getClassLoader(), trace.getClassName());
        Object o = null;
        for (Method m : existingClass.getDeclaredMethods()) {
            if (m.getName().equals(methodName)) {
                ParameterizedType parameterizedType = (ParameterizedType) m.getGenericReturnType();
                Type type = parameterizedType.getActualTypeArguments()[0];
                if (type instanceof ParameterizedType) {
                    ParameterizedType subType = (ParameterizedType) type;
                    if (subType.getRawType() == List.class) {
                        o = Collections.emptyList();
                    } else if (subType.getRawType() == Pager.class) {
                        o = Pager.empty();
                    }
                } else if (type instanceof List) {
                    o = Collections.emptyList();
                } else {
                    o = ClassUtils.newInstance((Class) type);
                }
                break;
            }
        }
        return o;
    }
}