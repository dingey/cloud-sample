package com.d.common.handler;

import brave.Span;
import brave.Tracer;
import com.d.base.Result;
import com.d.exception.CheckedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @Resource
    ApplicationContext applicationContext;
    @Resource
    ObjectMapper objectMapper;

    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public Object handle(Throwable e) {
        if (!(e instanceof CheckedException)) {
            log.error(e.getMessage(), e);
            if (applicationContext.getBean(Tracer.class) != null) {
                Span span = applicationContext.getBean(Tracer.class).currentSpan();
                List<String> traceElements = new ArrayList<>();
                traceElements.add(e.getClass().getName() + ": " + e.getCause().getMessage());
                Stream.of(e.getStackTrace()).limit(6).forEach(stackTraceElement -> traceElements.add("    " + stackTraceElement.toString()));
                traceElements.add("Caused by: " + e.getCause().getClass().getName() + ": " + e.getCause().getMessage());
                try {
                    span.tag("stack-trace", objectMapper.writeValueAsString(traceElements));
                } catch (JsonProcessingException ex) {
                    log.error(ex.getMessage(), ex);
                }
                span.tag("error", e.getMessage());
            }
        }
        return Result.error(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(CheckedException.class)
    public Object handle(CheckedException e) {
        return Result.fail(e.getCode(), e.getMsg());
    }
}