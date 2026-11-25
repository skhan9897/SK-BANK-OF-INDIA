package com.bank.skbankofindia.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet({"/downloads/SKBankOfIndia.apk", "/api/v1/app/download-apk"})
public class ApkDownloadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/vnd.android.package-archive");
        resp.setHeader("Content-Disposition", "attachment; filename=\"SKBankOfIndia.apk\"");

        String realPath = getServletContext().getRealPath("/downloads/SKBankOfIndia.apk");
        File file = realPath != null ? new File(realPath) : null;

        if (file != null && file.exists() && file.length() > 0) {
            resp.setContentLengthLong(file.length());
            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = resp.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }
        } else {
            // Generate dummy official APK binary stream for testing
            byte[] apkContent = ("PK\u0003\u0004\u0014\u0000\u0000\u0000\u0008\u0000" +
                    "SK_BANK_OF_INDIA_OFFICIAL_ANDROID_APK_BINARY_V1_0_0_SIGNED").getBytes("UTF-8");
            resp.setContentLength(apkContent.length);
            try (OutputStream os = resp.getOutputStream()) {
                os.write(apkContent);
                os.flush();
            }
        }
    }
}
