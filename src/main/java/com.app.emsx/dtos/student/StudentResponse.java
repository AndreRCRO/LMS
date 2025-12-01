package com.app.emsx.dtos.student;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String career;
    private String codigo;
}


