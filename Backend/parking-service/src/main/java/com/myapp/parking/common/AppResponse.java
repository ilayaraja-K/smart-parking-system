package com.myapp.parking.common;

import lombok.*;

/**
 * Generic response wrapper
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppResponse<T> {

    private MyServiceMessage message;
    private T appResponse;

    public static <T> AppResponse<T> createSuccessMessage(MyServiceMessage message, T data) {
        return AppResponse.<T>builder()
                .message(message)
                .appResponse(data)
                .build();
    }

    public static <T> AppResponse<T> createSuccessfullyCreatedMessage(MyServiceMessage message, T data) {
        return AppResponse.<T>builder()
                .message(message)
                .appResponse(data)
                .build();
    }

    public static <T> AppResponse<T> createBadRequestMessage(MyServiceMessage message, T data) {
        return AppResponse.<T>builder()
                .message(message)
                .appResponse(data)
                .build();
    }

    public static <T> AppResponse<T> createNotFoundMessage(MyServiceMessage message, T data) {
        return AppResponse.<T>builder()
                .message(message)
                .appResponse(data)
                .build();
    }

    public static <T> AppResponse<T> createServerErrorMessage(MyServiceMessage message, T data) {
        return AppResponse.<T>builder()
                .message(message)
                .appResponse(data)
                .build();
    }
}