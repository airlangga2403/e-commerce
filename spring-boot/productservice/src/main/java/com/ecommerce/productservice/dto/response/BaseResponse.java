package com.ecommerce.productservice.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseResponse<T> {
    private int status;
    private String message;
    private T data;
}
