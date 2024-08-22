package com.verraki.globalmart.stockmonitoring.util;

import lombok.Data;

@Data
public class ApiResponse<T> {

    private boolean success;
    private int responseCode;
    private String responseMessage;
    private T data;

    public ApiResponse(boolean success, int responseCode, String responseMessage, T data) {
        this.success = success;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.data = data;
    }

}
