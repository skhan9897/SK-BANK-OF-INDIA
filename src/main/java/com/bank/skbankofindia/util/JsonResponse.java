package com.bank.skbankofindia.util;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    private String timestamp;

    public JsonResponse() {
        this.timestamp = Instant.now().toString();
    }

    public JsonResponse(boolean success, String message, T data, String errorCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
        this.timestamp = Instant.now().toString();
    }

    public static <T> JsonResponse<T> success(String message, T data) {
        return new JsonResponse<>(true, message, data, null);
    }

    public static <T> JsonResponse<T> success(String message) {
        return new JsonResponse<>(true, message, null, null);
    }

    public static <T> JsonResponse<T> error(String message, String errorCode) {
        return new JsonResponse<>(false, message, null, errorCode);
    }

    public static <T> JsonResponse<T> error(String message) {
        return new JsonResponse<>(false, message, null, "INTERNAL_ERROR");
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
