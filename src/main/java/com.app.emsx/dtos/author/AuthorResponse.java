package com.app.emsx.dtos.author;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    
    // Usar String para evitar problemas de serialización de zona horaria
    private String birthDate;
    
    private List<Long> bookIds;
}


