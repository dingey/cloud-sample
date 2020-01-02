package com.d.common.aspect;

import brave.Span;
import brave.Tracer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Aspect
@Component
@ConditionalOnProperty(value = "spring.sleuth.enabled")
public class ZipkinTracerAspect {
    @Resource
    ObjectMapper objectMapper;
    @Resource
    Tracer tracer;

    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Span span = tracer.currentSpan();
        span.tag("param", objectMapper.writeValueAsString(pjp.getArgs()));
        Object result = null;
        try {
            result = pjp.proceed();
        } finally {
            span.tag("result", objectMapper.writeValueAsString(result));
        }
        return result;
    }
}