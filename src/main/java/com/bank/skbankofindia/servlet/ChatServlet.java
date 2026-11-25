package com.bank.skbankofindia.servlet;

import com.bank.skbankofindia.service.ChatService;
import com.bank.skbankofindia.util.JsonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@WebServlet("/api/v1/protected/chat/*")
public class ChatServlet extends HttpServlet {

    private final ChatService chatService = new ChatService();
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

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        try {
            if ("/search".equals(pathInfo)) {
                String query = req.getParameter("q");
                List<Map<String, Object>> customers = chatService.searchCustomers(query != null ? query : "");
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Search results fetched", customers)));
            } else if ("/messages".equals(pathInfo)) {
                long partnerUserId = Long.parseLong(req.getParameter("partnerUserId"));
                List<Map<String, Object>> messages = chatService.getMessages(userId, partnerUserId);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Messages fetched", messages)));
            } else {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found: " + pathInfo);
            }
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Chat error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Long userId = (Long) req.getAttribute("authenticatedUserId");
        if (userId == null) {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        try {
            Map<String, Object> body = parseJsonBody(req);

            if ("/messages".equals(pathInfo)) {
                long partnerUserId = Long.parseLong(body.get("receiverUserId").toString());
                String content = (String) body.get("content");
                String msgType = (String) body.getOrDefault("messageType", "TEXT");

                chatService.sendMessage(userId, partnerUserId, msgType, content, null, null);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Message sent")));
            } else if ("/pay".equals(pathInfo)) {
                long receiverUserId = Long.parseLong(body.get("receiverUserId").toString());
                BigDecimal amount = new BigDecimal(body.get("amount").toString());
                String remarks = (String) body.getOrDefault("remarks", "Chat Payment");

                Map<String, Object> result = chatService.executeChatPayment(userId, receiverUserId, amount, remarks);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Chat payment completed successfully", result)));
            } else {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found: " + pathInfo);
            }
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Chat payment failed: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJsonBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        if (sb.length() == 0) return Map.of();
        return objectMapper.readValue(sb.toString(), Map.class);
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.error(message)));
    }
}
