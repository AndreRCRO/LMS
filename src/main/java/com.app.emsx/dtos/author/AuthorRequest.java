package com.app.emsx.dtos.author;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorRequest {
    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$", message = "El nombre solo debe contener letras, no números")
    @Size(min = 2, max = 25, message = "El nombre debe tener entre 2 y 25 letras")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$", message = "El apellido solo debe contener letras, no números")
    @Size(max = 20, message = "El apellido no puede tener más de 20 letras")
    private String lastName;

    @NotBlank(message = "El email es obligatorio")
    @Pattern(regexp = "^[a-zA-Z0-9._]{4,30}@[a-zA-Z0-9.-]{1,20}\\.[a-zA-Z]{2,20}$", message = "El email debe contener solo letras, números, puntos y guiones bajos. Parte local: 4-30 caracteres, dominio: máx 20, extensión: máx 20")
    private String email;

    @NotBlank(message = "La fecha de nacimiento es obligatoria")
    private String birthDate;

    private List<Long> bookIds;
}

