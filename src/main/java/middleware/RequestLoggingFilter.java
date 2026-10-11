package com.example.ksl.wsd2025.middleware;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        System.out.println(
                "[REQUEST] " +
                        request.getMethod() + " " +
                        request.getRequestURI()
        );

        filterChain.doFilter(request, response);

        long endTime = System.currentTimeMillis();

        System.out.println(
                "[RESPONSE] " +
                        response.getStatus() +
                        " / " +
                        (endTime - startTime) + "ms"
        );
    }
}