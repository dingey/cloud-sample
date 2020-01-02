package com.d.common.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.util.Objects;

@Aspect
@Component
@Slf4j
public class LogAspect {
    @Resource
    private ObjectMapper objectMapper;

    @Around("(@annotation(org.springframework.web.bind.annotation.GetMapping)||" +
            "@annotation(org.springframework.web.bind.annotation.PostMapping)||" +
            "@annotation(org.springframework.web.bind.annotation.PutMapping)||" +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)||" +
            "@annotation(org.springframework.web.bind.annotation.RequestMapping)) " +
            "&& @target(org.springframework.web.bind.annotation.RestController)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = getHttpServletRequest();
        try {
            Object proceed = point.proceed();
            log.info("请求路径：{} 参数：{} 响应：{}", request.getRequestURI(), getParam(point), toJson(proceed));
            return proceed;
        } catch (Throwable throwable) {
            log.error("请求路径：{} 参数：{} 异常信息：{}", request.getRequestURI(), getParam(point), throwable.getMessage());
            throw throwable;
        }
    }

    private static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    private Object getParam(ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Annotation[][] parameterAnnotations = signature.getMethod().getParameterAnnotations();
        for (int i = 0; i < args.length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            boolean isRequestBody = false;
            if (annotations != null && annotations.length > 0) {
                for (Annotation an : annotations) {
                    if (an instanceof RequestBody) {
                        isRequestBody = true;
                        break;
                    }
                }
                if (isRequestBody) {
                    args[i] = toJson(args[i]);
                }
            }
        }
        return args;
//        if (getHttpServletRequest().getContentType() != null && getHttpServletRequest().getContentType().contains("json")) {
//            return toJson(pjp.getArgs());
//        } else {
//            return pjp.getArgs();
//        }
    }

    private String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
