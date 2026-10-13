package com.bank.skbankofindia.servlet;

import com.bank.skbankofindia.dao.AccountDao;
import com.bank.skbankofindia.dao.TransactionDao;
import com.bank.skbankofindia.model.Account;
import com.bank.skbankofindia.model.Customer;
import com.bank.skbankofindia.model.Transaction;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private AccountDao accountDao = new AccountDao();
    private TransactionDao transactionDao = new TransactionDao();

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

        if (!accounts.isEmpty()) {
            List<Transaction> transactions = transactionDao.findTransactionsByAccountId(accounts.get(0).getId());
            req.setAttribute("transactions", transactions);
        }

        req.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(req, resp);
    }
}
