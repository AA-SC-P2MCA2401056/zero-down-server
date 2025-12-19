package com.server.zero_down.Service;

import com.server.zero_down.Common.Jwt.JwtService;
import com.server.zero_down.Dto.Forms.AuthRequest;
import com.server.zero_down.Dto.Forms.RegisterRequest;
import com.server.zero_down.Dto.View.AuthSuccessResponse;
import com.server.zero_down.Dto.View.SuccessResponse;
import com.server.zero_down.Modal.AdmUser;
import com.server.zero_down.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public SuccessResponse<AuthSuccessResponse> login(AuthRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            AuthSuccessResponse authSuccessResponse =
                    new AuthSuccessResponse(request.getUsername(), token);

            return new SuccessResponse<>(
                    HttpStatus.OK.name(),
                    "Login Successful",
                    authSuccessResponse
            );

        } catch (AuthenticationException e) {
            // You can throw a custom exception here and handle it in @ControllerAdvice
            throw e;
        }
    }

    public SuccessResponse<?> register(RegisterRequest request) {
        if (userRepository.findByUserName(request.getUseName()) != null) {
            // In a cleaner design, throw a custom exception instead of returning text
            throw new IllegalArgumentException("Username already exists");
        }

        AdmUser user = AdmUser.builder()
                .name(request.getName())
                .userName(request.getUseName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole().getCode())
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return new SuccessResponse<>(
                HttpStatus.OK.name(),
                "User Registration Completed",
                null
        );
    }
}

