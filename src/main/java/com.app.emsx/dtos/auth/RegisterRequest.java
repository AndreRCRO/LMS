package com.app.emsx.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RegisterRequest
 * -----------------------------------------------------
 * ✔ DTO para registro de nuevos usuarios
 * ✔ Compatible con el frontend (React/Next.js)
 * ✔ Usado en /api/auth/register
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /**
     * Nombre del usuario
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$", message = "El nombre solo debe contener letras")
    @Size(min = 2, max = 20, message = "El nombre debe tener entre 2 y 20 letras")
    private String firstname;

    /**
     * Apellido del usuario
     */
    @NotBlank(message = "El apellido es obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$", message = "El apellido solo debe contener letras")
    @Size(max = 20, message = "El apellido no puede tener más de 20 caracteres")
    private String lastname;

    /**
     * Correo electrónico único del usuario
     */
    @NotBlank(message = "El correo es obligatorio")
    @Pattern(regexp = "^[a-zA-Z0-9._]{4,30}@[a-zA-Z0-9.-]{1,20}\\.[a-zA-Z]{2,20}$", message = "El email debe contener solo letras, números, puntos y guiones bajos. Parte local: 4-30 caracteres, dominio: máx 20, extensión: máx 20")
    private String email;

    /**
     * Contraseña del usuario
     */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 30, message = "La contraseña debe tener entre 6 y 30 caracteres")
    @Pattern(regexp = "^(?!\\s)(?!.*\\s$).*$", message = "La contraseña no puede empezar ni terminar con un espacio en blanco")
    private String password;
}
