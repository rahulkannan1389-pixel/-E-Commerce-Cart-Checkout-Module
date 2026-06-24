package com.techpalle.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T>{
private Integer statusCode;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private String path;
    private List<String> errors;

    // SUCCESS RESPONSE
    public static <T> ApiResponse<T> success(T data, String message, String path) {
        return ApiResponse.<T>builder()
                .statusCode(200)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String path) {
        return success(data, "Operation successful", path);
    }

    //  ERROR RESPONSE
    public static <T> ApiResponse<T> error(int statusCode, String message, List<String> errors, String path) {
        return ApiResponse.<T>builder()
                .statusCode(statusCode)
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }

    public static <T> ApiResponse<T> error(int statusCode, String message, String path) {
        return error(statusCode, message, null, path);
    }

}
