package com.server.zero_down.Dto.Forms;

import com.server.zero_down.Common.Enums.RoleType;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String useName;
    private String password;
    private RoleType role;
}

