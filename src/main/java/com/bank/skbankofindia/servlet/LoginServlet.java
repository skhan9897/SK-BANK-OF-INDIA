package com.bank.skbankofindia.servlet;

import com.bank.skbankofindia.dao.CustomerDao;
import com.bank.skbankofindia.model.Customer;
import com.bank.skbankofindia.utils.SecurityUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private CustomerDao customerDao = new CustomerDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        Customer customer = customerDao.findByEmail(email);
        if (customer != null && SecurityUtils.verifyPassword(password, customer.getPasswordHash())) {
            HttpSession session = req.getSession();
            session.setAttribute("customer", customer);
            session.setAttribute("customerId", customer.getId());
            resp.sendRedirect("dashboard");
        } else {
            req.setAttribute("error", "Invalid email or password.");
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        }
    }
}
