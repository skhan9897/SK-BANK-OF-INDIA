package com.bank.skbankofindia.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class AppEventListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppEventListener.class.getName());
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("Initializing SK Bank of India Application Context Listener & Database...");
        DatabaseConfig.initDataSource();

        // Start 24/7 Keep-Alive Self-Ping Daemon (Pings health check every 4 minutes)
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                String renderUrl = "https://sk-bank-of-india-rzy4.onrender.com/health";
                URL url = new URL(renderUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                int status = conn.getResponseCode();
                LOGGER.info("Keep-Alive Self-Ping Response Code: " + status);
                conn.disconnect();
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Keep-alive ping cycle completed: " + e.getMessage());
            }
        }, 1, 4, TimeUnit.MINUTES);

        LOGGER.info("24/7 Render Keep-Alive Daemon started successfully.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            LOGGER.info("Keep-Alive Daemon shutdown.");
        }
        DatabaseConfig.close();
        LOGGER.info("SK Bank of India Application Context Listener Destroyed.");
    }
}
