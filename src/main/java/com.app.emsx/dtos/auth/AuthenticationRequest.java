package com.app.emsx.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AuthenticationRequest
 * -----------------------------------------------------
 * ✔ DTO para recibir datos de inicio de sesión
 * ✔ Compatible con el frontend (React/Next.js)
 * ✔ Usado en /api/auth/login
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {

    /**
     * Correo electrónico del usuario
     */
    @NotBlank(message = "El correo es obligatorio")
    private String email;

    /**
     * Contraseña del usuario
     */
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
