package com.example.stocks.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ClientIdFilter extends OncePerRequestFilter {

    private static final String VALID_CLIENT_ID = "abc123";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (!path.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientId = request.getHeader("X-Client-Id");

        // Missing or blank header
        if (clientId == null || clientId.isBlank()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/plain");
            response.getWriter().write("Missing X-Client-Id header");
            response.getWriter().flush();
            return;
        }

        // Invalid header value
        if (!VALID_CLIENT_ID.equals(clientId)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/plain");
            response.getWriter().write("Invalid X-Client-Id");
            response.getWriter().flush();   // <-- required
            return;
        }

        // Valid → continue
        filterChain.doFilter(request, response);
    }
}