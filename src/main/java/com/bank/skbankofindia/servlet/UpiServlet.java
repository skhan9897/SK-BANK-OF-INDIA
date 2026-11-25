package com.bank.skbankofindia.servlet;

import com.bank.skbankofindia.model.Transaction;
import com.bank.skbankofindia.service.UpiService;
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
import java.util.Map;

@WebServlet("/api/v1/protected/upi/*")
public class UpiServlet extends HttpServlet {

    private final UpiService upiService = new UpiService();
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
            if ("/qr/me".equals(pathInfo) || "/me".equals(pathInfo)) {
                Map<String, Object> myQr = upiService.getOrCreateUpiProfile(userId);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("My QR Code fetched", myQr)));
            } else {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found: " + pathInfo);
            }
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "UPI error: " + e.getMessage());
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

            if ("/qr/resolve".equals(pathInfo) || "/resolve".equals(pathInfo)) {
                String payload = (String) body.get("qrPayload");
                Map<String, Object> resolved = upiService.resolveVpa(payload);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("VPA Resolved", resolved)));
            } else if ("/qr/pay".equals(pathInfo) || "/pay".equals(pathInfo)) {
                String recipientVpa = (String) body.get("recipientVpa");
                BigDecimal amount = new BigDecimal(body.get("amount").toString());
                String remarks = (String) body.getOrDefault("remarks", "UPI Scan & Pay");

                Transaction txn = upiService.processQrPayment(userId, recipientVpa, amount, remarks);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Scan & Pay executed successfully", txn)));
            } else {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found: " + pathInfo);
            }
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Payment execution failed: " + e.getMessage());
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
