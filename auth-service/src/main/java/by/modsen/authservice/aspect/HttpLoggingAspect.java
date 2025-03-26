package by.modsen.authservice.aspect;

import by.modsen.authservice.constants.ApplicationConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class HttpLoggingAspect {

    private final ObjectMapper jsonMapper;

    @Around("@within(org.springframework.web.bind.annotation.RestController) && execution(* *(..))")
    public Object logRequestAndResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest httpRequest = getCurrentHttpRequest();
        String requestBody = extractRequestBody(joinPoint);
        logRequestDetails(httpRequest, requestBody);
        return logResponseDetails(joinPoint, httpRequest);
    }

    @Around("@within(org.springframework.web.bind.annotation.RestControllerAdvice) && execution(* *(..))")
    public Object logRequestAndResponseForExceptionHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest httpRequest = getCurrentHttpRequest();
        return logResponseDetails(joinPoint, httpRequest);
    }

    private HttpServletRequest getCurrentHttpRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder
            .getRequestAttributes()))
            .getRequest();
    }

    private void logRequestDetails(HttpServletRequest request, String requestBody) {
        log.info(ApplicationConstants.REQUEST_LOGGING_MESSAGE,
            request.getMethod(),
            request.getRequestURI(),
            requestBody);
    }

    private Object logResponseDetails(ProceedingJoinPoint joinPoint, HttpServletRequest request) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object response = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;
        String responseBody = serializeToJson(response);
        log.info(ApplicationConstants.RESPONSE_LOGGING_MESSAGE,
            request.getMethod(),
            request.getRequestURI(),
            responseBody,
            duration);
        return response;
    }

    private String extractRequestBody(ProceedingJoinPoint joinPoint) {
        return Arrays.stream(joinPoint.getArgs())
            .map(this::serializeToJson)
            .collect(Collectors.joining(", "));
    }

    private String serializeToJson(Object object) {
        if (object == null) {
            return "";
        }
        try {
            return jsonMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error(ApplicationConstants.ERROR_SERIALIZING_JSON_MESSAGE, e);
            return ApplicationConstants.ERROR_SERIALIZING_JSON_MESSAGE;
        }
    }

}