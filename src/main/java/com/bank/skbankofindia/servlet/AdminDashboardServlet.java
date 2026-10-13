package com.bank.skbankofindia.servlet;

import com.bank.skbankofindia.dao.AdminDao;
import com.bank.skbankofindia.model.Bank;
import com.bank.skbankofindia.model.Branch;
import com.bank.skbankofindia.model.Customer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private AdminDao adminDao = new AdminDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Bank> banks = adminDao.getAllBanks();
        List<Branch> branches = adminDao.getAllBranches();
        List<Customer> customers = adminDao.getAllCustomers();

        req.setAttribute("banks", banks);
        req.setAttribute("branches", branches);
        req.setAttribute("customers", customers);

        req.getRequestDispatcher("/WEB-INF/jsp/admin-dashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("verifyBranch".equals(action)) {
            int branchId = Integer.parseInt(req.getParameter("branchId"));
            String status = req.getParameter("status");
            adminDao.updateBranchVerification(branchId, status);
        } else if ("registerBank".equals(action)) {
            String code = req.getParameter("bankCode");
            String name = req.getParameter("bankName");
            String sector = req.getParameter("sector");
            String hq = req.getParameter("headquarters");
            String website = req.getParameter("websiteUrl");
            adminDao.registerBank(code, name, sector, hq, website);
        } else if ("toggleBankStatus".equals(action)) {
            int bankId = Integer.parseInt(req.getParameter("bankId"));
            boolean active = Boolean.parseBoolean(req.getParameter("active"));
            adminDao.updateBankStatus(bankId, active);
        }
        resp.sendRedirect("dashboard");
    }
}
