package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Usar una clave más segura (mínimo 32 caracteres para HS256)
    private final String SECRET_KEY = "mi_clave_secreta_super_segura_de_32_caracteres_minimo_para_jwt_hs256";
    
    // O mejor aún, usar desde application.properties:
    // @Value("${jwt.secret}")
    // private String SECRET_KEY;

    private final long JWT_EXPIRATION = 1000 * 60 * 60 * 10; // 10 horas

    // Generar token con el username o email como subject
    public String generateToken(String usernameOrEmail) {
        return Jwts.builder()
                .subject(usernameOrEmail)  // username o email
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSigningKey())
                .compact();
    }

    // Extraer el username (o email) del token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extraer fecha de expiración del token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extraer cualquier claim del token con función genérica   
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extraer todos los claims del token (versión para JJWT 0.12.x)
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token JWT expirado", e);
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("Token JWT no soportado", e);
        } catch (MalformedJwtException e) {
            throw new RuntimeException("Token JWT malformado", e);
        } catch (JwtException e) {
            throw new RuntimeException("Error procesando token JWT", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Token JWT vacío o nulo", e);
        }
    }

    // Validar si el token es válido (username coincide y no está expirado)
    public boolean validateToken(String token, String usernameOrEmail) {
        try {
            final String tokenUsername = extractUsername(token);
            return (tokenUsername.equals(usernameOrEmail) && !isTokenExpired(token));
        } catch (Exception e) {
            System.out.println("Error validando token: " + e.getMessage());
            return false;
        }
    }

    // Verificar si el token expiró
    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true; // Si hay error, considerar como expirado
        }
    }

    // Obtener la clave de firma
    private SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Método adicional para validar solo la estructura del token
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // Método para extraer username sin validar expiración (útil para refresh tokens)
    public String extractUsernameWithoutValidation(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}