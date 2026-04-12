package com.edutrack.certificateService.monitoring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Order(1)
@ConditionalOnProperty(name = "app.monitoring.aop.enabled", havingValue = "true", matchIfMissing = true)
public class ApplicationMonitoringAspect {

    private static final Logger log = LoggerFactory.getLogger(ApplicationMonitoringAspect.class);
    private static final int ARGUMENT_TYPE_LOG_LIMIT = 5;

    @Around("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Controller *) || within(@org.springframework.stereotype.Service *)")
    public Object monitorBeanExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint);
    }

    private Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long startTime = System.nanoTime();

        log.info("Started {} with {}", methodName, summarizeArguments(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            log.info("Completed {} in {} ms with returnType={}",
                    methodName,
                    toMillis(startTime),
                    result == null ? "null" : result.getClass().getSimpleName());
            return result;
        } catch (Throwable ex) {
            log.error("Failed {} in {} ms with {}: {}",
                    methodName,
                    toMillis(startTime),
                    ex.getClass().getSimpleName(),
                    ex.getMessage());
            throw ex;
        }
    }

    private String summarizeArguments(Object[] args) {
        if (args == null || args.length == 0) {
            return "argCount=0";
        }

        String argumentTypes = Arrays.stream(args)
                .limit(ARGUMENT_TYPE_LOG_LIMIT)
                .map(this::safeTypeName)
                .collect(Collectors.joining(", "));

        String suffix = args.length > ARGUMENT_TYPE_LOG_LIMIT ? ", ..." : "";
        return "argCount=" + args.length + ", argTypes=[" + argumentTypes + suffix + "]";
    }

    private String safeTypeName(Object arg) {
        return arg == null ? "null" : arg.getClass().getSimpleName();
    }

    private long toMillis(long startTime) {
        return (System.nanoTime() - startTime) / 1_000_000;
    }
}
