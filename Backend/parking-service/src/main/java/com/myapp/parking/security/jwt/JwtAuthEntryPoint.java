package com.myapp.parking.security.jwt;

import java.io.IOException;

import org.springframework.http.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.parking.common.AppResponse;
import com.myapp.parking.common.MyServiceMessage;

import jakarta.servlet.http.*;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException ex)
            throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        AppResponse<Object> res =
                AppResponse.createBadRequestMessage(
                        MyServiceMessage.UNAUTHORIZED,
                        "Unauthorized"
                );

        response.getWriter().write(mapper.writeValueAsString(res));
    }
}