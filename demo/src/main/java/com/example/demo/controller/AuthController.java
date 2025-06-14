package com.example.demo.controller;

import com.example.demo.dto.request.auth.LoginRequest;
import com.example.demo.dto.request.auth.RegisterRequest;
import com.example.demo.dto.response.AuthResponse;
import com.example.demo.entity.UserEntity;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {

        System.out.println("👉 Registro recibido: " + request);


        try {
            // Verificar si el email ya existe
            if (userService.findByEmail(request.getEmail()) != null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "El email ya está registrado"));
            }
            
            // Verificar si el username ya existe
            if (userService.findByUsername(request.getUsername()) != null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error ", "El username ya está registrado"));
            }

            UserEntity user = new UserEntity();
            user.setEmail(request.getEmail());
            user.setUsername(request.getUsername()); // ¡IMPORTANTE! Estaba faltando
            user.setPassword(request.getPassword());
            user.setName(request.getName());
            user.setRoles(Set.of("USER")); // Rol por defecto
            
            UserEntity savedUser = userService.registerUser(user);
            
            // Generar token automáticamente después del registro
            // Usar el email como identificador principal para el token
            String token = jwtUtil.generateToken(savedUser.getEmail());
            
            return ResponseEntity.ok(new AuthResponse(token, savedUser.getName(), savedUser.getUsername(), savedUser.getEmail()));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Error al registrar usuario: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsernameOrEmail(),
                    loginRequest.getPassword()
                )
            );

            UserEntity user = userService.findByUsernameOrEmail(loginRequest.getUsernameOrEmail());
            
            // Generar token usando el mismo identificador que se usó para el login
            // Esto es CRÍTICO para que funcione correctamente
            String token = jwtUtil.generateToken(loginRequest.getUsernameOrEmail());

            return ResponseEntity.ok(new AuthResponse(token, user.getName(), user.getUsername(), user.getEmail()));

        } catch (Exception e) {
            return ResponseEntity.status(401)
                .body(Map.of("error", "Credenciales incorrectas"));
        }
    }
}