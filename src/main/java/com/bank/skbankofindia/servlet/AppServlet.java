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

        // Disable HTTP caching for app version manifest so mobile app detects updates instantly
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Expires", "0");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        if ("/version".equals(pathInfo) || "/check-update".equals(pathInfo)) {
            Map<String, Object> versionInfo = new HashMap<>();
            versionInfo.put("latestVersionCode", 3);
            versionInfo.put("latestVersionName", "1.2.0");
            versionInfo.put("downloadUrl", "https://sk-bank-of-india-rzy4.onrender.com/download-app.jsp");
            versionInfo.put("apkUrl", "https://sk-bank-of-india-rzy4.onrender.com/downloads/SKBankOfIndia.apk");
            versionInfo.put("mandatoryUpdate", false);
            versionInfo.put("releaseNotes", "SK Bank v1.2.0 Release: Real-Time Camera QR Scanner, My Payment QR Code generation, Scan & Pay, and Chat & Send Money!");

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Latest app version info", versionInfo)));
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.error("Endpoint not found")));
        }
    }
}
