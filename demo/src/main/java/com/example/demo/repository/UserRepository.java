package com.example.demo.repository;

import com.example.demo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    // Buscar por username O email (para login)
    Optional<UserEntity> findByUsernameOrEmail(String username, String email);
    
    // Buscar solo por email
    Optional<UserEntity> findByEmail(String email);
    
    // Buscar solo por username  
    Optional<UserEntity> findByUsername(String username);
    
    // Método personalizado más explícito (opcional)
    @Query("SELECT u FROM UserEntity u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<UserEntity> findByUsernameOrEmailCustom(@Param("identifier") String identifier);
    
}