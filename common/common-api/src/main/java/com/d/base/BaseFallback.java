package com.d.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.objenesis.instantiator.util.ClassUtils;
import org.springframework.util.StringUtils;

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

    public StackTraceElement getTrace() {
        return getTrace(5);
    }

    public StackTraceElement getErrorTrace() {
        return getTrace(6);
    }

    public StackTraceElement getTrace(int index) {
        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        return traces[index];
    }

    public void error(Object... args) {
        error( null, args);
    }

    public void error(Throwable cause, Object... args) {
        StackTraceElement trace = getErrorTrace();
        String methodName = trace.getMethodName();
        Class<Object> existingClass = ClassUtils.getExistingClass(this.getClass().getClassLoader(), trace.getClassName());
        log.error("服务被熔断，类名：{} 方法：{} 参数：{} 原因：{}", getName(existingClass), methodName, args, cause == null ? "" : cause.getMessage());
    }

    private String getName(Class<?> clazz) {
        if (StringUtils.isEmpty(clazz.getSimpleName())) {
            return clazz.getName();
        } else {
            return clazz.getSimpleName();
        }
    }

    public Object empty() {
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