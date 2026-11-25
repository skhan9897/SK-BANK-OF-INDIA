package com.bank.skbankofindia.servlet;

import com.bank.skbankofindia.service.NotificationService;
import com.bank.skbankofindia.util.JsonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/v1/protected/notifications")
public class NotificationServlet extends HttpServlet {

    private final NotificationService notificationService = new NotificationService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Long userId = (Long) req.getAttribute("authenticatedUserId");
        if (userId == null) {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        try {
            List<Map<String, Object>> notifications = notificationService.getUserNotifications(userId, 20);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("User notifications fetched", notifications)));
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch notifications: " + e.getMessage());
        }
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.error(message)));
    }
}
