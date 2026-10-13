package com.bank.skbankofindia.servlet;

import com.bank.skbankofindia.dao.BankDao;
import com.bank.skbankofindia.model.Branch;
import com.bank.skbankofindia.utils.ValidationUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/ifsc-search")
public class IfscSearchServlet extends HttpServlet {
    private BankDao bankDao = new BankDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ifsc = req.getParameter("ifsc");
        String query = req.getParameter("query");

        if (ifsc != null && !ifsc.isEmpty()) {
            if (ValidationUtils.isValidIfsc(ifsc)) {
                Branch branch = bankDao.findBranchByIfsc(ifsc);
                req.setAttribute("branch", branch);
                if (branch == null) {
                    req.setAttribute("error", "No branch found for IFSC: " + ifsc);
                }
            } else {
                req.setAttribute("error", "Invalid IFSC format. Expected format: ABCD0123456");
            }
        } else if (query != null && !query.isEmpty()) {
            List<Branch> branches = bankDao.searchBranches(query);
            req.setAttribute("branches", branches);
            req.setAttribute("query", query);
        }

        req.getRequestDispatcher("/WEB-INF/jsp/ifsc-search.jsp").forward(req, resp);
    }
}
