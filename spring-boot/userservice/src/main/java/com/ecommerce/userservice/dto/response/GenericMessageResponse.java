package com.ecommerce.userservice.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@ToString
public class GenericMessageResponse<T> {
    private int statusCode;
    private String message;
    boolean succes = false;

    private T data;
    public GenericMessageResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        if (statusCode == HttpStatus.OK.value()) {
            succes = true;
        }
    }
    public GenericMessageResponse(){}

    public static <T> GenericMessageResponse<T> failedResponse(String message){
        return failedResponse(HttpStatus.BAD_REQUEST.value(), message, null);
    }

    public static <T> GenericMessageResponse<T> failedResponse(T data) {
        return failedResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", data);
    }

    public static <T> GenericMessageResponse<T> failedResponse(int statusCode, String message){
        return failedResponse(statusCode, message, null);
    }

    public static <T> GenericMessageResponse<T> failedResponse(int statusCode, String message, T data){
        GenericMessageResponse<T> response = new GenericMessageResponse<>(statusCode, message);
        response.setSucces(false);
        response.setData(data);
        return response;
    }

    public static <T> GenericMessageResponse<T> sucessfulResponse(String message, T data){
        return sucessfulResponse(HttpStatus.OK.value(), message, data);
    }

    public static <T> GenericMessageResponse<T> sucessfulResponse(String message){
        return sucessfulResponse(HttpStatus.OK.value(), message, null);
    }

    public static <T> GenericMessageResponse<T> sucessfulResponse(int statusCode, String message, T data){
        GenericMessageResponse<T> response = new GenericMessageResponse<>(statusCode, message);
        response.setSucces(true);
        response.setData(data);
        return response;
    }
}
