package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.service.CustomUserDetailsService;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String usernameOrEmail = null;
        String jwt = null;

        // Verificar si el header Authorization existe y tiene el formato correcto
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                // Extraer username o email del token
                usernameOrEmail = jwtUtil.extractUsername(jwt);
                logger.debug("Token extraído para: {}", usernameOrEmail);
            } catch (Exception e) {
                logger.warn("JWT token no válido: {}", e.getMessage());
                // Continuar con el filtro chain aunque el token sea inválido
                filterChain.doFilter(request, response);
                return;
            }
        }

        // Si usernameOrEmail no es null y no hay autenticación previa en el contexto
        if (usernameOrEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Cargar los detalles del usuario usando el método estándar de UserDetailsService
                UserDetails userDetails = userDetailsService.loadUserByUsername(usernameOrEmail);
                
                // Validar el token - ahora ambos usan el mismo identifier
                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                    // Crear el token de autenticación
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, 
                                    null, 
                                    userDetails.getAuthorities());

                    // Agregar detalles adicionales de la request
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Establecer la autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    logger.debug("Usuario autenticado: {}", userDetails.getUsername());
                }
            } catch (UsernameNotFoundException e) {
                logger.warn("Usuario no encontrado: {}", usernameOrEmail);
            } catch (Exception e) {
                logger.error("Error durante la autenticación: {}", e.getMessage());
            }
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Opcional: excluir ciertas rutas del filtro JWT
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/") || 
               path.startsWith("/api/public/") ||
               path.equals("/api/login") ||
               path.equals("/api/register");
    }
}