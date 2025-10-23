package com.srsoft.modapimanager.security.filters;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
@Component
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {
    private final LoadingCache<String, AtomicInteger> requestCountsPerIp;
    
    @Value("${security.ratelimit.max-requests:50}")  
    private int maxRequestsPerMinute;

    public RateLimitFilter() {
        super();
        requestCountsPerIp = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build(new CacheLoader<String, AtomicInteger>() {
                    @Override
                    public AtomicInteger load(String key) {
                        return new AtomicInteger(0);
                    }
                });
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String clientIp = getClientIdentifier(request);
              
        
                try {
            AtomicInteger counter = requestCountsPerIp.get(clientIp);
            int currentCount = counter.incrementAndGet();
            
            log.info("IP: {}, Request count: {}, Max allowed: {}", 
                    clientIp, currentCount, maxRequestsPerMinute);

            if (currentCount > maxRequestsPerMinute) {
                log.warn("Rate limit exceeded - IP: {}, Count: {}", clientIp, currentCount);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                String jsonResponse = String.format(
                    "{\"error\": \"Rate limit exceeded\", \"currentCount\": %d, \"maxAllowed\": %d}", 
                    currentCount, maxRequestsPerMinute
                );
                response.getWriter().write(jsonResponse);
                return;
            }
            
            log.debug("Request allowed - IP: {}, Count: {}", clientIp, currentCount);
            filterChain.doFilter(request, response);
            
        } catch (ExecutionException e) {
            log.error("Error processing rate limit for IP: {}", clientIp, e);
            throw new RuntimeException("Error in rate limit logic", e);
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            String[] ips = xForwardedFor.split(",");
            return ips[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isEmpty()) {
            return realIp;
        }
        return request.getRemoteAddr();
    }

    
    private String getClientIdentifier(HttpServletRequest request) {
        String ip = getClientIP(request);
        String userAgent = request.getHeader("User-Agent");
        return ip + "-" + userAgent;  // Combina IP e User-Agent
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // Escludiamo alcuni path se necessario
        boolean shouldSkip = path.startsWith("/public/") || 
                           path.startsWith("/auth/") ||
                           path.contains("swagger") ||
                           path.contains("api-docs");
        
        if (shouldSkip) {
            log.debug("Skipping rate limit for path: {}", path);
        }
        
        return shouldSkip;
    }
}