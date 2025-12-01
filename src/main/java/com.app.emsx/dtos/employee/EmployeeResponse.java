package com.app.emsx.dtos.employee;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String phone;

    // ✅ Atributos adaptados de Author (libraryproject-2)
    private LocalDate birthDate;

    // ✅ Atributos adaptados de Student (libraryproject-2)
    private String career;
    private String codigo;

    private String departmentName; // ✅ Nombre del departamento
    private List<String> skillNames; // ✅ Habilidades del empleado
}
