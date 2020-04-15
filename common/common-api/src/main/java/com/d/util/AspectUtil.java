package com.d.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class AspectUtil {
    private final static ThreadLocal<Map<String, Object>> argsMap = new InheritableThreadLocal<>();

    public static <T> T spel(ProceedingJoinPoint pjp, String expressionString, Class<T> desiredResultType) {
        String[] parameterNames = ((MethodSignature) pjp.getSignature()).getParameterNames();
        Object[] args = pjp.getArgs();
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        if (args.length > 0 && parameterNames != null) {
            for (int i = 0; i < args.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }
        if (argsMap.get() != null) {
            for (Map.Entry<String, Object> entry : argsMap.get().entrySet()) {
                context.setVariable(entry.getKey(), entry.getValue());
            }
        }
        return parser.parseExpression(expressionString).getValue(context, desiredResultType);
    }

    public static void setLocalArgs(String key, Object value) {
        Map<String, Object> map = argsMap.get();
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(key, value);
        argsMap.set(map);
    }

    public static void cleanLocalArgs() {
        argsMap.remove();
    }
}
