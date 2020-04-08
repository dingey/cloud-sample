package com.d.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@SuppressWarnings("unused")
public class AspectUtil {
    public static <T> T spel(ProceedingJoinPoint pjp, String expressionString, Class<T> desiredResultType) {
        String[] parameterNames = ((MethodSignature) pjp.getSignature()).getParameterNames();
        Object[] args = pjp.getArgs();
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }
        return parser.parseExpression(expressionString).getValue(context, desiredResultType);
    }

}
