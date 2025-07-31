package com.shoestore.config;

import com.shoestore.security.service.EntityCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter to clean up the entity cache after each request
 * This prevents memory leaks and ensures cache doesn't persist between requests
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class EntityCacheCleanupFilter extends OncePerRequestFilter {

    private final EntityCache entityCache;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                  @NonNull HttpServletResponse response, 
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } finally {
            // Always clean up the cache after request processing
            entityCache.cleanup();
        }
    }
}
