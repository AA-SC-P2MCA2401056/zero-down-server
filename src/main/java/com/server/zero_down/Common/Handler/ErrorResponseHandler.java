package com.server.zero_down.Common.Handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class ErrorResponseHandler {
    private String status;
    private String message;
    private LocalDateTime timestamp;
}
