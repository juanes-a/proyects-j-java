package com.example.demo.service;

import com.example.demo.model.UserEntity;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registrar un nuevo usuario
     */
    public UserEntity registerUser(UserEntity userEntity) {
        // Verificar si el usuario ya existe
        if (existsByUsernameOrEmail(userEntity.getUsername(), userEntity.getEmail())) {
            throw new RuntimeException("El usuario o email ya existe");
        }
        
        // Encriptar contraseña
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        
        return userRepository.save(userEntity);
    }

    /**
     * Buscar usuario por username o email
     */
    public UserEntity findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository
            .findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
            .orElse(null);
    }

    /**
     * Buscar usuario por email únicamente - MÉTODO FALTANTE
     */
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Buscar usuario por username únicamente - MÉTODO FALTANTE
     */
    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    /**
     * Buscar usuario por username o email (devuelve Optional)
     * Más seguro para evitar NullPointerException
     */
    public Optional<UserEntity> findByUsernameOrEmailOptional(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }

    /**
     * Verificar si existe un usuario con el username o email
     */
    public boolean existsByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email).isPresent();
    }

    /**
     * Verificar contraseña
     */
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Autenticar usuario (útil para el login)
     */
    public UserEntity authenticateUser(String usernameOrEmail, String rawPassword) {
        UserEntity user = findByUsernameOrEmail(usernameOrEmail);
        
        if (user != null && checkPassword(rawPassword, user.getPassword())) {
            return user;
        }
        
        return null; // Credenciales inválidas
    }

    /**
     * Cambiar contraseña de usuario
     */
    public boolean changePassword(String usernameOrEmail, String oldPassword, String newPassword) {
        UserEntity user = findByUsernameOrEmail(usernameOrEmail);
        
        if (user != null && checkPassword(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        
        return false;
    }
}