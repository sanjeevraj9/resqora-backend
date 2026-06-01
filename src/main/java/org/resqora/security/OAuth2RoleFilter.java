package org.resqora.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class OAuth2RoleFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String role = request.getParameter("role");

        if (role != null && request.getRequestURI().contains("/oauth2/authorization")) {
            request.getSession().setAttribute("oauth2Role", role.toUpperCase());
        }

        filterChain.doFilter(request, response);
    }
}