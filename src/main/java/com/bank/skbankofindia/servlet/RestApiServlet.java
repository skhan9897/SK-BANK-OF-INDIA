package com.bank.skbankofindia.servlet;

import com.bank.skbankofindia.dao.AccountDao;
import com.bank.skbankofindia.dao.BankDao;
import com.bank.skbankofindia.dao.CustomerDao;
import com.bank.skbankofindia.model.Account;
import com.bank.skbankofindia.model.Branch;
import com.bank.skbankofindia.model.Customer;
import com.bank.skbankofindia.utils.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/v1/*")
public class RestApiServlet extends HttpServlet {
    private CustomerDao customerDao = new CustomerDao();
    private AccountDao accountDao = new AccountDao();
    private BankDao bankDao = new BankDao();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();

        if (pathInfo == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid API endpoint\"}");
            return;
        }

        if (pathInfo.startsWith("/ifsc/")) {
            String ifsc = pathInfo.substring(6);
            Branch branch = bankDao.findBranchByIfsc(ifsc);
            if (branch != null) {
                resp.getWriter().write(objectMapper.writeValueAsString(branch));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Branch not found\"}");
            }
        } else if (pathInfo.equals("/branches/search")) {
            String q = req.getParameter("q");
            List<Branch> branches = bankDao.searchBranches(q != null ? q : "");
            resp.getWriter().write(objectMapper.writeValueAsString(branches));
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"Endpoint not found\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();

        if ("/auth/login".equals(pathInfo)) {
            Map<String, String> body = objectMapper.readValue(req.getInputStream(), Map.class);
            String email = body.get("email");
            String password = body.get("password");

            Customer customer = customerDao.findByEmail(email);
            if (customer != null && SecurityUtils.verifyPassword(password, customer.getPasswordHash())) {
                List<Account> accounts = accountDao.findAccountsByCustomerId(customer.getId());
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("status", "SUCCESS");
                responseMap.put("customer", customer);
                responseMap.put("accounts", accounts);
                resp.getWriter().write(objectMapper.writeValueAsString(responseMap));
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"status\":\"FAILED\",\"error\":\"Invalid credentials\"}");
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"Endpoint not found\"}");
        }
    }
}
