package com.bank.skbankofindia.servlet;

import com.bank.skbankofindia.util.JsonResponse;
import com.bank.skbankofindia.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/api/v1/protected/*")
public class JwtAuthFilter implements Filter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(resp, "Missing or invalid Authorization header. Bearer token required.");
            return;
        }

        String token = authHeader.substring(7).trim();

        if (!JwtUtil.isTokenValid(token)) {
            sendUnauthorized(resp, "Invalid or expired JWT token. Please log in again.");
            return;
        }

        try {
            Long userId = JwtUtil.extractUserId(token);
            String username = JwtUtil.extractUsername(token);
            String role = JwtUtil.extractRole(token);

            req.setAttribute("authenticatedUserId", userId);
            req.setAttribute("authenticatedUsername", username);
            req.setAttribute("authenticatedRole", role);

            chain.doFilter(request, response);

        } catch (Exception e) {
            sendUnauthorized(resp, "Token processing failed: " + e.getMessage());
        }
    }

    private void sendUnauthorized(HttpServletResponse resp, String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        JsonResponse<Void> errorResp = JsonResponse.error(message, "UNAUTHORIZED");
        resp.getWriter().write(objectMapper.writeValueAsString(errorResp));
    }

    @Override
    public void destroy() {}
}
