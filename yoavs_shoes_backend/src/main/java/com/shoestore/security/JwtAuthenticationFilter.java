package com.shoestore.security;

import com.shoestore.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 *
 * This filter intercepts incoming requests and validates JWT tokens.
 * If a valid token is found, it sets the authentication in the SecurityContext.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        log.debug("JwtAuthenticationFilter processing request: {} {}", request.getMethod(), request.getRequestURI());

        try {
            // Extract JWT token from request
            String jwt = getJwtFromRequest(request);
            log.debug("Extracted JWT token: {}", jwt != null ? "present" : "absent");

            // Validate token and set authentication
            if (StringUtils.hasText(jwt)) {
                log.debug("Attempting to validate JWT token");

                if (jwtUtil.validateToken(jwt)) {
                    String username = jwtUtil.getUsernameFromToken(jwt);
                    log.debug("Token is valid for user: {}", username);

                    // Check if authentication is already set
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        log.debug("Loading user details for: {}", username);

                        // Load user details
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        log.debug("User details loaded: {}", userDetails.getUsername());

                        // Create authentication token
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // Set authentication in SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        log.info("Successfully set authentication for user: {}", username);
                    } else {
                        log.debug("Authentication already set in SecurityContext");
                    }
                } else {
                    log.warn("JWT token validation failed");
                }
            } else {
                log.debug("No JWT token found in request");
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
            // Clear any existing authentication
            SecurityContextHolder.clearContext();
        }

        log.debug("Continuing filter chain for request: {} {}", request.getMethod(), request.getRequestURI());
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.debug("Authorization header: {}", bearerToken != null ? "Bearer ***" : "not present");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            log.debug("Extracted token length: {}", token.length());
            return token;
        }

        return null;
    }

    /**
     * Skip JWT processing for certain paths
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();

        boolean shouldSkip = path.equals("/api/auth/register") || path.equals("/api/auth/login") ||
                path.startsWith("/api/public/") ||
                path.startsWith("/api/docs/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/") ||
                path.equals("/swagger-ui.html") ||
                path.startsWith("/actuator/health") ||
                path.startsWith("/uploads/") ||
                path.startsWith("/images/") ||
                path.equals("/error");

        if (shouldSkip) {
            log.debug("Skipping JWT filter for path: {}", path);
        } else {
            log.debug("Processing JWT filter for path: {}", path);
        }

        return shouldSkip;
    }
}