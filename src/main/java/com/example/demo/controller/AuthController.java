package com.example.demo.controller;

import com.example.demo.dto.request.auth.LoginRequest;
import com.example.demo.dto.request.auth.RegisterRequest;
import com.example.demo.dto.response.AuthResponse;
import com.example.demo.entity.UserEntity;
import com.example.demo.enums.Role;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserRepository.UserInfoDTO;
import com.example.demo.enums.Role;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserRepository.UserInfoDTO;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.request.auth.ChangePasswordRequest;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")

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
    // 1. OJO: Quité el "@Valid" temporalmente para que entre SÍ o SÍ
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {

        // 2. EL CHISMOSO: Vamos a imprimir en el log qué llegó
        System.out.println("============== DEBUG START ==============");
        System.out.println("¿Llegó la petición?: SÍ");
        System.out.println("Datos recibidos (Raw): " + request);
        System.out.println("Email: " + request.getEmail());
        System.out.println("Username: " + request.getUsername());
        System.out.println("Password: " + request.getPassword());
        System.out.println("============== DEBUG END ==============");

        try {
            // Validación Manual (para ver si falla aquí)
            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                System.out.println("🚨 ERROR: El email llegó NULO");
                return ResponseEntity.badRequest().body(Map.of("error", "Email es nulo en el backend"));
            }

            // ... Resto de tu lógica normal ...
            if (userService.findByEmail(request.getEmail()) != null) {
                return ResponseEntity.badRequest().body(Map.of("error", "El email ya está registrado"));
            }
            
            // ... (El resto del código sigue igual) ...
            UserEntity user = new UserEntity();
            user.setEmail(request.getEmail());
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setName(request.getName());
            user.setRole(Role.COLLAB);
            
            UserEntity savedUser = userService.registerUser(user);
            String token = jwtUtil.generateToken(savedUser.getEmail());
            
            return ResponseEntity.ok(new AuthResponse(token, savedUser.getName(), savedUser.getUsername(), savedUser.getEmail()));

        } catch (Exception e) {
            e.printStackTrace(); // Imprime el error real en la consola
            return ResponseEntity.badRequest().body(Map.of("error", "Error interno: " + e.getMessage()));
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
                return ResponseEntity.status(404).body(Map.of("error", "Credenciales incorrectas"));
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

    // --- AGREGAR ESTE NUEVO ENDPOINT ---
@PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        // Obtener el usuario autenticado
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        if (username == null) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no autenticado"));
        }

        // Usar los getters del DTO
        boolean success = userService.changePassword(
            username, 
            request.getCurrentPassword(), 
            request.getNewPassword()
        );

        if (success) {
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "La contraseña actual es incorrecta"));
        }
    }
}