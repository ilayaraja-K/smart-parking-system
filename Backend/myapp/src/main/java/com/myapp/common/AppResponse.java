package com.myapp.common;



import lombok.*;

/**
 * Generic response wrapper for all APIs
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppResponse<T> {

    private MyServiceMessage message;
    private T appResponse;


    /**
     * Success response
     */
    public static <T> AppResponse<T> createSuccessMessage(MyServiceMessage message, T data) {
        return AppResponse.<T>builder()
                .message(message)
                .appResponse(data)
                .build();
    }

    /**
     * Created response
     */
    public static <T> AppResponse<T> createSuccessfullyCreatedMessage(MyServiceMessage message, T data) {
        return AppResponse.<T>builder()
                .message(message)
                .appResponse(data)
                .build();
    }

    /**
     * Bad Request response
     */
    public static <T> AppResponse<T> createBadRequestMessage(MyServiceMessage message, T data) {
        return AppResponse.<T>builder()
                .message(message)
                .appResponse(data)
                .build();
    }

    /**
     * Not Found response
     */
    public static <T> AppResponse<T> createNotFoundMessage(MyServiceMessage message, T data) {
        return AppResponse.<T>builder()
                .message(message)
                .appResponse(data)
                .build();
    }

    /**
     * Forbidden response (NEW)
     */
    public static <T> AppResponse<T> createForbiddenMessage(MyServiceMessage message, T data) {
        return AppResponse.<T>builder()
                .message(message)
                .appResponse(data)
                .build();
    }

    /**
     * Server Error response
     */
    public static <T> AppResponse<T> createServerErrorMessage(MyServiceMessage message, T data) {
        return AppResponse.<T>builder()
                .message(message)
                .appResponse(data)
                .build();
    }
}