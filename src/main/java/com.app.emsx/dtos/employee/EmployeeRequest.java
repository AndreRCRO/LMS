package com.app.emsx.dtos.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 25, message = "El nombre debe tener entre 2 y 25 letras")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    @Email(message = "Debe proporcionar un correo válido")
    private String email;

    private String address;

    @Size(min = 8, max = 20, message = "El teléfono debe tener entre 8 y 20 caracteres")
    private String phone;

    // ✅ Atributos adaptados de Author (libraryproject-2)
    private LocalDate birthDate;

    // ✅ Atributos adaptados de Student (libraryproject-2)
    private String career;
    
    @Size(max = 50, message = "El código no puede superar los 50 caracteres")
    private String codigo;

    private Long departmentId; // FK del departamento
}
