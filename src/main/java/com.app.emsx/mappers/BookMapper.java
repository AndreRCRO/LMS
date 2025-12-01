package com.app.emsx.mappers;

import com.app.emsx.dtos.book.BookRequest;
import com.app.emsx.dtos.book.BookResponse;
import com.app.emsx.entities.Book;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    default Book toEntity(BookRequest dto) {
        if (dto == null) return null;
        Book entity = new Book();
        entity.setTitle(dto.getTitle());
        entity.setGenre(dto.getGenre());
        // Convertir String a LocalDate manualmente para evitar problemas de zona horaria
        if (dto.getPublicationDate() != null && !dto.getPublicationDate().trim().isEmpty()) {
            try {
                LocalDate publicationDate = LocalDate.parse(dto.getPublicationDate().trim());
                entity.setPublicationDate(publicationDate);
            } catch (Exception e) {
                throw new IllegalArgumentException("Formato de fecha inválido: " + dto.getPublicationDate());
            }
        }
        entity.setEditorial(dto.getEditorial());
        return entity;
    }

    default BookResponse toResponse(Book entity) {
        if (entity == null) return null;
        BookResponse dto = new BookResponse();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setGenre(entity.getGenre());
        // Convertir LocalDate a String en formato YYYY-MM-DD para evitar problemas de serialización
        if (entity.getPublicationDate() != null) {
            dto.setPublicationDate(entity.getPublicationDate().toString());
        } else {
            dto.setPublicationDate(null);
        }
        dto.setEditorial(entity.getEditorial());
        if (entity.getAuthor() != null) {
            dto.setAuthorId(entity.getAuthor().getId());
            dto.setAuthorName(entity.getAuthor().getFirstName() + " " + entity.getAuthor().getLastName());
        }
        return dto;
    }

    default List<BookResponse> toResponseList(List<Book> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }

    default void updateEntityFromRequest(BookRequest dto, Book entity) {
        if (dto == null || entity == null) return;
        if (dto.getTitle() != null && !dto.getTitle().isBlank())
            entity.setTitle(dto.getTitle());
        if (dto.getGenre() != null) entity.setGenre(dto.getGenre());
        // Convertir String a LocalDate manualmente para evitar problemas de zona horaria
        if (dto.getPublicationDate() != null && !dto.getPublicationDate().trim().isEmpty()) {
            try {
                LocalDate publicationDate = LocalDate.parse(dto.getPublicationDate().trim());
                entity.setPublicationDate(publicationDate);
            } catch (Exception e) {
                throw new IllegalArgumentException("Formato de fecha inválido: " + dto.getPublicationDate());
            }
        }
        if (dto.getEditorial() != null) entity.setEditorial(dto.getEditorial());
    }
}


