package com.cheikh.commun.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Around("@annotation(auditable)")
    @SneakyThrows
    public Object logMethod(ProceedingJoinPoint joinPoint, Auditable auditable) {
        String methodName = joinPoint.getSignature().getName();
        Map<String, Object> parameters = obtainParameters(joinPoint);
        JSONObject globalRequest = new JSONObject();
        JSONObject headers = new JSONObject();

        // Récupérer les headers
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            try {
                if (HttpHeaders.AUTHORIZATION.equalsIgnoreCase(headerName)) {
                    String token = request.getHeader(headerName);
                    if (token != null && token.startsWith("Bearer ")) {
                        JSONObject decoded = decodeJWTToken(token.split(" ")[1]);
                        decoded.remove("email"); // retire infos sensibles
                        headers.put("user", decoded.optString("sub"));
                        headers.put("roles", decoded.optJSONArray("roles"));
                    }
                } else {
                    headers.put(headerName, request.getHeader(headerName));
                }
            } catch (JSONException e) {
                throw new RuntimeException("Erreur JSON dans les headers", e);
            }
        });

        globalRequest.put("headers", headers);
        globalRequest.put("body", parameters);
        globalRequest.put("timestamp", Instant.now());

        log.info("{} request: {}", auditable.value(), globalRequest);

        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            log.error("{} failed with exception: {}", methodName, e.getMessage(), e);
            throw e;
        }
    }

    private Map<String, Object> obtainParameters(ProceedingJoinPoint joinPoint) {
        Map<String, Object> parameters = new HashMap<>();
        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        Object[] values = joinPoint.getArgs();
        for (int i = 0; i < paramNames.length && i < values.length; i++) {
            parameters.put(paramNames[i], values[i]);
        }
        return parameters;
    }

    public static JSONObject decodeJWTToken(String token) {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] parts = token.split("\\.");
        if (parts.length < 2) return new JSONObject();
        return new JSONObject(new String(decoder.decode(parts[1])));
    }
}

