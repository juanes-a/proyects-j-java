package com.example.demo.controller;

import com.example.demo.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailTestController {

    private final EmailService emailService;

    public EmailTestController(EmailService emailService) {
        this.emailService = emailService;
    }

    // GET simple para probar envío
    @GetMapping("/send")
    public ResponseEntity<String> sendTestEmail(
            @RequestParam String to,
            @RequestParam(defaultValue = "Prueba de correo") String subject,
            @RequestParam(defaultValue = "Hola, este es un correo de prueba desde Spring Boot!") String text) {

        emailService.enviarCorreo(to, subject, text);
        return ResponseEntity.ok("Correo enviado a " + to);
    }
}
