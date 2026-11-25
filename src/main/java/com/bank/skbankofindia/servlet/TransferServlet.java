package com.bank.skbankofindia.servlet;

import com.bank.skbankofindia.dao.BankAccountDAO;
import com.bank.skbankofindia.model.BankAccount;
import com.bank.skbankofindia.model.Transaction;
import com.bank.skbankofindia.service.LedgerService;
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

@WebServlet("/api/v1/protected/transfers/*")
public class TransferServlet extends HttpServlet {

    private final LedgerService ledgerService = new LedgerService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Long userId = (Long) req.getAttribute("authenticatedUserId");
        if (userId == null) {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        try {
            Map<String, Object> body = parseJsonBody(req);

            long sourceAccountId = Long.parseLong(body.get("sourceAccountId").toString());
            BigDecimal amount = new BigDecimal(body.get("amount").toString());
            String paymentMethod = (String) body.getOrDefault("paymentMethod", "INTERNAL");
            String remarks = (String) body.getOrDefault("remarks", "Fund Transfer");

            String destInput = body.containsKey("destinationAccount") ? body.get("destinationAccount").toString().trim() :
                    (body.containsKey("destinationAccountNo") ? body.get("destinationAccountNo").toString().trim() : null);

            Transaction txn;

            if ("UPI".equalsIgnoreCase(paymentMethod) || (destInput != null && destInput.contains("@"))) {
                UpiService upiService = new UpiService();
                txn = upiService.processQrPayment(userId, destInput != null ? destInput : "priya@skbank", amount, remarks);
            } else {
                long destinationAccountId = 0L;
                if (body.containsKey("destinationAccountId") && body.get("destinationAccountId") != null) {
                    try {
                        destinationAccountId = Long.parseLong(body.get("destinationAccountId").toString());
                    } catch (Exception ignored) {}
                }

                if (destinationAccountId == 0L && destInput != null) {
                    BankAccountDAO bankAccountDAO = new BankAccountDAO();
                    BankAccount destAcc = bankAccountDAO.findByAccountNumber(destInput);
                    if (destAcc != null) {
                        destinationAccountId = destAcc.getAccountId();
                    } else {
                        throw new IllegalArgumentException("Destination bank account not found: " + destInput);
                    }
                }

                if (destinationAccountId == 0L) {
                    throw new IllegalArgumentException("Destination account ID or number is required.");
                }

                txn = ledgerService.processFundTransfer(sourceAccountId, destinationAccountId, amount, paymentMethod, remarks, userId);
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Fund transfer executed successfully", txn)));

        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Transaction failed: " + e.getMessage());
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
