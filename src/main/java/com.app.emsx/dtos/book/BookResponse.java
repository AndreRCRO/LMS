package com.app.emsx.dtos.book;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponse {
    private Long id;
    private String title;
    private String genre;

    // Usar String para evitar problemas de serialización de zona horaria
    private String publicationDate;

    private String editorial;
    private Long authorId;
    private String authorName;
}


