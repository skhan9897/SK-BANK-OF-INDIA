package com.bank.skbankofindia.servlet;

import com.bank.skbankofindia.service.AuthService;
import com.bank.skbankofindia.service.OtpService;
import com.bank.skbankofindia.util.JsonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/v1/auth/*")
public class AuthServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AuthServlet.class.getName());
    private final AuthService authService = new AuthService();
    private final OtpService otpService = new OtpService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        try {
            Map<String, Object> body = parseJsonBody(req);

            switch (pathInfo) {
                case "/register":
                    handleRegister(body, resp);
                    break;
                case "/login":
                    handleLogin(body, req, resp);
                    break;
                case "/send-otp":
                    handleSendOtp(body, resp);
                    break;
                case "/verify-otp":
                    handleVerifyOtp(body, resp);
                    break;
                case "/forgot-password":
                    handleForgotPassword(body, resp);
                    break;
                case "/reset-password":
                    handleResetPassword(body, resp);
                    break;
                default:
                    sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found: " + pathInfo);
                    break;
            }
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in AuthServlet", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected server error occurred: " + e.getMessage());
        }
    }

    private void handleRegister(Map<String, Object> body, HttpServletResponse resp) throws Exception {
        String username = (String) body.get("username");
        String email = (String) body.get("email");
        String phone = (String) body.get("phone");
        String password = (String) body.get("password");
        String firstName = (String) body.get("firstName");
        String middleName = (String) body.get("middleName");
        String lastName = (String) body.get("lastName");
        String dob = (String) body.get("dob");
        String gender = (String) body.get("gender");
        String pan = (String) body.get("pan");
        String aadhaar = (String) body.get("aadhaar");
        String address1 = (String) body.get("address1");
        String address2 = (String) body.get("address2");
        String city = (String) body.get("city");
        String state = (String) body.get("state");
        String pincode = (String) body.get("pincode");

        Map<String, Object> result = authService.registerCustomer(username, email, phone, password,
                firstName, middleName, lastName, dob, gender, pan, aadhaar, address1, address2, city, state, pincode);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Customer registered successfully", result)));
    }

    private void handleLogin(Map<String, Object> body, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String identifier = (String) body.get("identifier");
        String password = (String) body.get("password");

        String ipAddress = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");

        Map<String, Object> result = authService.login(identifier, password, ipAddress, userAgent);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Login successful", result)));
    }

    private void handleSendOtp(Map<String, Object> body, HttpServletResponse resp) throws Exception {
        String recipient = (String) body.get("recipient");
        String purpose = (String) body.get("purpose");

        String otp = otpService.generateAndSendOtp(recipient, purpose != null ? purpose : "GENERAL");

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("OTP sent successfully", Map.of("otp_sandbox", otp))));
    }

    private void handleVerifyOtp(Map<String, Object> body, HttpServletResponse resp) throws Exception {
        String recipient = (String) body.get("recipient");
        String otp = (String) body.get("otp");
        String purpose = (String) body.get("purpose");

        boolean verified = otpService.verifyOtp(recipient, otp, purpose != null ? purpose : "GENERAL");

        if (verified) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("OTP verified successfully")));
        } else {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid or expired OTP");
        }
    }

    private void handleForgotPassword(Map<String, Object> body, HttpServletResponse resp) throws Exception {
        String identifier = (String) body.get("identifier");
        String otp = authService.initiateForgotPassword(identifier);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Password reset OTP sent to registered email", Map.of("otp_sandbox", otp))));
    }

    private void handleResetPassword(Map<String, Object> body, HttpServletResponse resp) throws Exception {
        String identifier = (String) body.get("identifier");
        String otp = (String) body.get("otp");
        String newPassword = (String) body.get("newPassword");

        authService.resetPassword(identifier, otp, newPassword);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(objectMapper.writeValueAsString(JsonResponse.success("Password reset successfully. Please login with your new password.")));
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
