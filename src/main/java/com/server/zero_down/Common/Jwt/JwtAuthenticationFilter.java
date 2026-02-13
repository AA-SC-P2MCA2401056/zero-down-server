package com.server.zero_down.Common.Jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.zero_down.Config.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        try {

            String path = request.getServletPath();
            if (path.startsWith("/api/auth")) {
                filterChain.doFilter(request, response);
                return;
            }

            // 1️⃣ No Authorization header → continue filter chain
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            // 2️⃣ Extract JWT
            String jwt = authHeader.substring(7);

            // 3️⃣ Extract username from token
            String username = jwtService.extractUsername(jwt);

            // 4️⃣ Authenticate only if SecurityContext is empty
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Load user from DB
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 5️⃣ Validate token
                if (!jwtService.isTokenValid(jwt, userDetails)) {
                    sendUnauthorized(response, "Invalid or expired token");
                    return;
                }

                // 6️⃣ Extract roles from JWT
                List<String> roles = jwtService.extractRoles(jwt);

                // 7️⃣ Convert roles → GrantedAuthority
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                // 8️⃣ Create authentication token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 9️⃣ Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            // Continue filter chain
            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            sendUnauthorized(response, ex.getMessage());
        }
    }


    /* -----------------------------------------------------------
       Send JSON Unauthorized Response (401)
    ----------------------------------------------------------- */
    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Unauthorized");
        body.put("message", message);

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}
