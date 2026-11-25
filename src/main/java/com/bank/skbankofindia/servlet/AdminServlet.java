package com.bank.skbankofindia.servlet;

import com.bank.skbankofindia.model.Transaction;
import com.bank.skbankofindia.service.AdminService;
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

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        try {
            if ("/accounts".equals(pathInfo)) {
                List<Map<String, Object>> accounts = adminService.getAllAccounts();
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("All accounts fetched", accounts)));
            } else {
                Map<String, Object> analytics = adminService.getDashboardAnalytics();
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Admin metrics fetched", analytics)));
            }
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Admin request failed: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String role = (String) req.getAttribute("authenticatedRole");
        Long adminUserId = (Long) req.getAttribute("authenticatedUserId");

        if (!"ROLE_ADMIN".equals(role) && !"ROLE_EMPLOYEE".equals(role)) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin privileges required.");
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        try {
            Map<String, Object> body = parseJsonBody(req);

            switch (pathInfo) {
                case "/account/freeze":
                    String freezeAccNo = (String) body.get("accountNumber");
                    Boolean freeze = (Boolean) body.get("freeze");
                    adminService.toggleAccountFreeze(freezeAccNo, Boolean.TRUE.equals(freeze));
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Account freeze status updated")));
                    break;

                case "/account/block":
                    long targetUserId = Long.parseLong(body.get("userId").toString());
                    Boolean block = (Boolean) body.get("block");
                    adminService.toggleUserBlock(targetUserId, Boolean.TRUE.equals(block));
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("User block status updated")));
                    break;

                case "/deposit":
                    String depAccNo = (String) body.get("accountNumber");
                    BigDecimal depAmount = new BigDecimal(body.get("amount").toString());
                    String depRemarks = (String) body.getOrDefault("remarks", "Admin Deposit");
                    Transaction depTxn = adminService.adminDeposit(depAccNo, depAmount, depRemarks, adminUserId != null ? adminUserId : 1L);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Admin deposit completed successfully", depTxn)));
                    break;

                case "/withdraw":
                    String withAccNo = (String) body.get("accountNumber");
                    BigDecimal withAmount = new BigDecimal(body.get("amount").toString());
                    String withRemarks = (String) body.getOrDefault("remarks", "Admin Withdrawal");
                    Transaction withTxn = adminService.adminWithdraw(withAccNo, withAmount, withRemarks, adminUserId != null ? adminUserId : 1L);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Admin withdrawal completed successfully", withTxn)));
                    break;

                case "/transfer":
                    String srcAccNo = (String) body.get("sourceAccountNo");
                    String dstAccNo = (String) body.get("destAccountNo");
                    BigDecimal trfAmount = new BigDecimal(body.get("amount").toString());
                    String trfRemarks = (String) body.getOrDefault("remarks", "Admin Transfer");
                    Transaction trfTxn = adminService.adminTransfer(srcAccNo, dstAccNo, trfAmount, trfRemarks, adminUserId != null ? adminUserId : 1L);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Admin transfer completed successfully", trfTxn)));
                    break;

                case "/kyc/approve":
                    long kycId = Long.parseLong(body.get("kycId").toString());
                    Boolean approve = (Boolean) body.get("approve");
                    String reason = (String) body.getOrDefault("reason", approve ? "KYC Approved" : "Documents rejected");
                    adminService.approveKyc(kycId, adminUserId != null ? adminUserId : 1L, Boolean.TRUE.equals(approve), reason);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("KYC status updated")));
                    break;

                default:
                    sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found: " + pathInfo);
                    break;
            }
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Admin action failed: " + e.getMessage());
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
