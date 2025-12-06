package com.server.zero_down.Dto.Forms;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String useName;
    private String password;
    private int role;
}

