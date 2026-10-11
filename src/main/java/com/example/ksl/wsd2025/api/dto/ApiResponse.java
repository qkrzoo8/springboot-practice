package com.example.ksl.wsd2025.api.dto;

public class ApiResponse<T> {

    private final String status;
    private final T data;

    public ApiResponse(String status, T data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }
}