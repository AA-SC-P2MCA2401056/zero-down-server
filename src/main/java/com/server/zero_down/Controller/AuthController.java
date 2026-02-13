package com.server.zero_down.Controller;

import com.server.zero_down.Dto.Forms.AuthRequest;
import com.server.zero_down.Dto.Forms.RegisterRequest;
import com.server.zero_down.Dto.View.AuthSuccessResponse;
import com.server.zero_down.Dto.View.SuccessResponse;
import com.server.zero_down.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            SuccessResponse<AuthSuccessResponse> response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            SuccessResponse<?> msg = authService.register(request);
            return ResponseEntity.ok(msg);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}
