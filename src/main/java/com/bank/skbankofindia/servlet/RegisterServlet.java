package com.bank.skbankofindia.servlet;

import com.bank.skbankofindia.dao.AccountDao;
import com.bank.skbankofindia.dao.CustomerDao;
import com.bank.skbankofindia.model.Customer;
import com.bank.skbankofindia.model.Account;
import com.bank.skbankofindia.utils.SecurityUtils;
import com.bank.skbankofindia.utils.ValidationUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private CustomerDao customerDao = new CustomerDao();
    private AccountDao accountDao = new AccountDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String firstName = SecurityUtils.sanitize(req.getParameter("firstName"));
        String lastName = SecurityUtils.sanitize(req.getParameter("lastName"));
        String email = SecurityUtils.sanitize(req.getParameter("email"));
        String phone = SecurityUtils.sanitize(req.getParameter("phone"));
        String password = req.getParameter("password");
        String address = SecurityUtils.sanitize(req.getParameter("address"));
        String pan = SecurityUtils.sanitize(req.getParameter("pan"));

        if (!ValidationUtils.isValidEmail(email) || !ValidationUtils.isValidPhone(phone)) {
            req.setAttribute("error", "Invalid email or phone number format.");
            req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
            return;
        }

        Customer c = new Customer();
        c.setFirstName(firstName);
        c.setLastName(lastName);
        c.setEmail(email);
        c.setPhone(phone);
        c.setPasswordHash(SecurityUtils.hashPassword(password));
        c.setAddress(address);
        c.setPanNumber(pan);

        boolean registered = customerDao.registerCustomer(c);
        if (registered) {
            Customer saved = customerDao.findByEmail(email);
            if (saved != null) {
                Account acc = new Account();
                acc.setCustomerId(saved.getId());
                acc.setBranchId(1);
                acc.setAccountType("SAVINGS");
                accountDao.createAccount(acc);
            }
            resp.sendRedirect("login?registered=true");
        } else {
            req.setAttribute("error", "Registration failed. Email or phone may already exist.");
            req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
        }
    }
}
