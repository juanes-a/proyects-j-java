package com.example.demo.controller;

import com.example.demo.dto.request.auth.LoginRequest;
import com.example.demo.dto.request.auth.RegisterRequest;
import com.example.demo.dto.response.AuthResponse;
import com.example.demo.entity.UserEntity;
import com.example.demo.enums.Role;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserRepository.UserInfoDTO;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private UserRepository userRepository;

    
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
            user.setRole(Role.COLLAB); // Rol por defecto
            
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
            System.out.println("👉 Intento de login para: " + loginRequest.getUsernameOrEmail());
            
            // Primero buscar el usuario
            UserEntity user = userService.findByUsernameOrEmail(loginRequest.getUsernameOrEmail());
            if (user == null) {
                System.out.println("❌ Usuario no encontrado: " + loginRequest.getUsernameOrEmail());
                return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
            }
            
            System.out.println("✅ Usuario encontrado: " + user.getUsername());
            
            // Luego autenticar
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsernameOrEmail(),
                    loginRequest.getPassword()
                )
            );
            
            System.out.println("✅ Autenticación exitosa");
            
            // Generar token usando un identificador consistente (recomiendo usar email)
            String token = jwtUtil.generateToken(user.getEmail());
            
            System.out.println("✅ Token generado exitosamente");
            
            return ResponseEntity.ok(new AuthResponse(
                token, user.getName(), user.getUsername(), user.getEmail()
            ));

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            System.out.println("❌ Credenciales incorrectas para: " + loginRequest.getUsernameOrEmail());
            return ResponseEntity.status(401)
                .body(Map.of("error", "Credenciales incorrectas"));
        } catch (Exception e) {
            System.out.println("❌ Error inesperado en login:");
            e.printStackTrace(); // 👈 Esto mostrará el stack trace completo
            return ResponseEntity.status(500)
                .body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoDTO> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 👉 USAR EL MISMO MÉTODO que usa tu CustomUserDetailsService
        UserEntity user = userRepository.findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Retornar solo lo necesario
        return ResponseEntity.ok(
            new UserInfoDTO(user.getId(), user.getEmail(), user.getRole().name())
        );
    }

}