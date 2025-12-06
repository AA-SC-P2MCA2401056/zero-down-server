package com.server.zero_down.Dto.View;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthSuccessResponse {
    private String username;
    private String token;
}
