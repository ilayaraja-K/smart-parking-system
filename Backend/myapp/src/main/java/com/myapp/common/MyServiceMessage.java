package com.myapp.common;

import lombok.*;

/**
 * Standard response message structure
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyServiceMessage {

    private String responseCode;
    private String responseMessage;

    // ✅ Standard messages

    public static final MyServiceMessage SUCCESS =
            new MyServiceMessage("200", "Success");

    public static final MyServiceMessage CREATED =
            new MyServiceMessage("201", "Created Successfully");

    public static final MyServiceMessage BAD_REQUEST =
            new MyServiceMessage("400", "Bad Request");

    public static final MyServiceMessage NOT_FOUND =
            new MyServiceMessage("404", "Not Found");

    public static final MyServiceMessage INTERNAL_SERVER_ERROR =
            new MyServiceMessage("500", "Internal Server Error");

    public static final MyServiceMessage UNAUTHORIZED =
            new MyServiceMessage("401", "Unauthorized");

    public static final MyServiceMessage FORBIDDEN =
            new MyServiceMessage("403", "Forbidden");
}