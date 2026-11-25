package com.bank.skbankofindia.model;

import com.google.gson.JsonElement;

public class ApiResponse {
    private boolean success;
    private String message;
    private JsonElement data;
    private String errorCode;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public JsonElement getData() { return data; }
    public String getErrorCode() { return errorCode; }
}
