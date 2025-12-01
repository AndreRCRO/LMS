package com.app.emsx.dtos.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentRequest {
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

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]+$", message = "El teléfono solo debe contener números, no caracteres")
    @Size(min = 8, max = 8, message = "El teléfono debe tener exactamente 8 dígitos")
    private String phone;

    @NotBlank(message = "La carrera es obligatoria")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$", message = "La carrera solo debe contener letras, no números ni símbolos")
    @Size(max = 65, message = "La carrera no puede tener más de 65 letras")
    private String career;

    @NotBlank(message = "El código es obligatorio")
    @Size(min = 10, max = 10, message = "El código debe tener exactamente 10 caracteres")
    @Pattern(regexp = "^[A-Za-z]{3}[0-9]{7}$", message = "El código debe tener 3 letras seguidas de 7 números (ejemplo: ABC1234567)")
    private String codigo;
}

