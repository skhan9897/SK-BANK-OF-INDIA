package com.bank.skbankofindia.servlet;

import com.bank.skbankofindia.dao.AccountDao;
import com.bank.skbankofindia.model.Account;
import com.bank.skbankofindia.model.Customer;
import com.bank.skbankofindia.service.TransferService;
import com.bank.skbankofindia.utils.ValidationUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@WebServlet("/transfer")
public class TransferServlet extends HttpServlet {
    private AccountDao accountDao = new AccountDao();
    private TransferService transferService = new TransferService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            resp.sendRedirect("login");
            return;
        }
        Customer customer = (Customer) session.getAttribute("customer");
        List<Account> accounts = accountDao.findAccountsByCustomerId(customer.getId());
        req.setAttribute("accounts", accounts);
        req.getRequestDispatcher("/WEB-INF/jsp/transfer.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            resp.sendRedirect("login");
            return;
        }

        try {
            int sourceAccountId = Integer.parseInt(req.getParameter("sourceAccountId"));
            String destAccount = req.getParameter("destinationAccountNumber");
            String destIfsc = req.getParameter("destinationIfsc").trim().toUpperCase();
            BigDecimal amount = new BigDecimal(req.getParameter("amount"));

            if (!ValidationUtils.isValidIfsc(destIfsc)) {
                req.setAttribute("error", "Invalid destination IFSC format.");
                doGet(req, resp);
                return;
            }

            String idempotencyKey = UUID.randomUUID().toString();
            boolean success = transferService.executeTransfer(sourceAccountId, destAccount, destIfsc, amount, idempotencyKey);

            if (success) {
                req.setAttribute("success", "Transfer completed successfully!");
            } else {
                req.setAttribute("error", "Transfer failed or duplicate request.");
            }
        } catch (Exception e) {
            req.setAttribute("error", "Transfer error: " + e.getMessage());
        }

        doGet(req, resp);
    }
}
