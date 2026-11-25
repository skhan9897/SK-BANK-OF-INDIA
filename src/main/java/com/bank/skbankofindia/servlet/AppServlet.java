package com.bank.skbankofindia.servlet;

import com.bank.skbankofindia.util.JsonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/v1/app/*")
public class AppServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        if ("/version".equals(pathInfo) || "/check-update".equals(pathInfo)) {
            Map<String, Object> versionInfo = new HashMap<>();
            versionInfo.put("latestVersionCode", 1);
            versionInfo.put("latestVersionName", "1.0.0");
            versionInfo.put("downloadUrl", req.getContextPath() + "/download-app.jsp");
            versionInfo.put("apkUrl", req.getContextPath() + "/downloads/SKBankOfIndia.apk");
            versionInfo.put("forceUpdate", false);
            versionInfo.put("releaseNotes", "Official SK Bank of India Android App v1.0.0 - Instant UPI transfers, biometric login, and real-time ledger statements.");

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Latest app version info", versionInfo)));
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.error("Endpoint not found")));
        }
    }
}
