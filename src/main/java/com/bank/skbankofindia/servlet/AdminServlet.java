package com.bank.skbankofindia.servlet;

import com.bank.skbankofindia.service.AdminService;
import com.bank.skbankofindia.util.JsonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/v1/protected/admin/*")
public class AdminServlet extends HttpServlet {

    private final AdminService adminService = new AdminService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String role = (String) req.getAttribute("authenticatedRole");
        if (!"ROLE_ADMIN".equals(role) && !"ROLE_EMPLOYEE".equals(role)) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin or Employee privileges required.");
            return;
        }

        try {
            Map<String, Object> analytics = adminService.getDashboardAnalytics();
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Admin metrics fetched", analytics)));
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch analytics: " + e.getMessage());
        }
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.error(message)));
    }
}
