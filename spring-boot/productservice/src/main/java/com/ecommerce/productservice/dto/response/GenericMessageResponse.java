package com.ecommerce.productservice.dto.response;

import org.springframework.http.HttpStatus;

public class GenericMessageResponse<T> extends BaseResponse<T> {

    private boolean succes = false;

    public GenericMessageResponse(int status, String message) {
        super(status, message, null);
        if (status == HttpStatus.OK.value()) {
            this.succes = true;
        }
    }

    public GenericMessageResponse() {
        super();
    }

    public static <T> GenericMessageResponse<T> failedResponse(String message) {
        return failedResponse(HttpStatus.BAD_REQUEST.value(), message, null);
    }

    public static <T> GenericMessageResponse<T> failedResponse(String message, T data) {
        return failedResponse(HttpStatus.BAD_REQUEST.value(), message, data);
    }

    public static <T> GenericMessageResponse<T> failedResponse(int status, String message, T data) {
        GenericMessageResponse<T> response = new GenericMessageResponse<>(status, message);
        response.setSucces(false);
        response.setData(data);
        return response;
    }

    public static <T> GenericMessageResponse<T> successfulResponse(String message, T data) {
        return successfulResponse(HttpStatus.OK.value(), message, data);
    }

    public static <T> GenericMessageResponse<T> successfulResponse(String message) {
        return successfulResponse(HttpStatus.OK.value(), message, null);
    }

    public static <T> GenericMessageResponse<T> successfulResponse(int status, String message, T data) {
        GenericMessageResponse<T> response = new GenericMessageResponse<>(status, message);
        response.setSucces(true);
        response.setData(data);
        return response;
    }

    public boolean isSucces() {
        return succes;
    }

    public void setSucces(boolean succes) {
        this.succes = succes;
    }
}
