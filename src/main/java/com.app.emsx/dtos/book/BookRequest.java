package com.app.emsx.dtos.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequest {
    @NotBlank(message = "El título es obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$", message = "El título solo debe contener letras, no números ni símbolos")
    @Size(max = 20, message = "El título no puede tener más de 20 caracteres")
    private String title;

    @NotBlank(message = "El género es obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$", message = "El género solo debe contener letras, no números ni símbolos")
    @Size(max = 20, message = "El género no puede tener más de 20 caracteres")
    private String genre;

    @NotBlank(message = "La fecha de publicación es obligatoria")
    private String publicationDate;

    @NotBlank(message = "La editorial es obligatoria")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$", message = "La editorial solo debe contener letras, no números ni símbolos")
    @Size(max = 20, message = "La editorial no puede tener más de 20 caracteres")
    private String editorial;

    @NotNull(message = "El ID del autor es obligatorio")
    private Long authorId;
}

