package com.bank.skbankofindia.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "SKBankSession";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ACCOUNT_NO = "account_no";
    private static final String KEY_BALANCE = "balance";
    private static final String KEY_ACCOUNT_ID = "account_id";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveAuthToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getAuthToken() {
        String token = prefs.getString(KEY_TOKEN, null);
        if (token != null && !token.startsWith("Bearer ")) {
            return "Bearer " + token;
        }
        return token;
    }

    public void saveUserAccount(long accountId, String accountNo, String balance, String username) {
        prefs.edit()
                .putLong(KEY_ACCOUNT_ID, accountId)
                .putString(KEY_ACCOUNT_NO, accountNo)
                .putString(KEY_BALANCE, balance)
                .putString(KEY_USERNAME, username)
                .apply();
    }

    public long getAccountId() { return prefs.getLong(KEY_ACCOUNT_ID, 1L); }
    public String getAccountNo() { return prefs.getString(KEY_ACCOUNT_NO, "10018877665544"); }
    public String getBalance() { return prefs.getString(KEY_BALANCE, "10000.00"); }
    public String getUsername() { return prefs.getString(KEY_USERNAME, "Customer"); }

    public void clearSession() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return getAuthToken() != null;
    }
}
