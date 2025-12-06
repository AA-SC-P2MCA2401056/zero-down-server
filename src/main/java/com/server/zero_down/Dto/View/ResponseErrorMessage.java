package com.server.zero_down.Dto.View;

import lombok.Data;

@Data
public class ResponseErrorMessage {
    private String status;
    private String message;
    private String details;
}
