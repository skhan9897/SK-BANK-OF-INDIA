package com.bank.skbankofindia.servlet;

import com.bank.skbankofindia.model.BankAccount;
import com.bank.skbankofindia.service.AccountService;
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

@WebServlet("/api/v1/protected/accounts/*")
public class AccountServlet extends HttpServlet {

    private final AccountService accountService = new AccountService();
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
        if (pathInfo == null) pathInfo = "/";

        try {
            if ("/".equals(pathInfo) || pathInfo.isEmpty()) {
                List<BankAccount> accounts = accountService.getCustomerAccounts(userId);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Accounts fetched", accounts)));
            } else if (pathInfo.endsWith("/statement")) {
                String[] parts = pathInfo.split("/");
                String accountNo = parts[1];
                Map<String, Object> statement = accountService.getAccountStatement(userId, accountNo, 50);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Statement generated", statement)));
            } else {
                String accountNo = pathInfo.substring(1);
                BankAccount account = accountService.getAccountDetails(userId, accountNo);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Account details fetched", account)));
            }
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error: " + e.getMessage());
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

            switch (pathInfo) {
                case "/create":
                    String type = (String) body.get("accountType");
                    BankAccount newAcc = accountService.createAdditionalAccount(userId, type);
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Account created successfully", newAcc)));
                    break;
                case "/freeze":
                    String accNo = (String) body.get("accountNumber");
                    Boolean freeze = (Boolean) body.get("freeze");
                    accountService.toggleAccountFreeze(userId, accNo, Boolean.TRUE.equals(freeze));
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Account freeze status updated")));
                    break;
                case "/nominee":
                    String accountNo = (String) body.get("accountNumber");
                    String nomineeName = (String) body.get("nomineeName");
                    String relationship = (String) body.get("relationship");
                    String dob = (String) body.get("dob");
                    String phone = (String) body.get("phone");
                    String address = (String) body.get("address");
                    BigDecimal pct = body.get("percentage") != null ? new BigDecimal(body.get("percentage").toString()) : new BigDecimal("100.00");
                    accountService.addNominee(userId, accountNo, nomineeName, relationship, dob, phone, address, pct);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Nominee added successfully")));
                    break;
                case "/pin/generate":
                case "/pin/update":
                    String newPin = (String) body.get("pin");
                    accountService.updateTransactionPin(userId, newPin);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Transaction PIN updated successfully!")));
                    break;
                default:
                    sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found: " + pathInfo);
                    break;
            }
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
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
