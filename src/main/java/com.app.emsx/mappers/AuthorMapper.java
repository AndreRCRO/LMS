package com.app.emsx.mappers;

import com.app.emsx.dtos.author.AuthorRequest;
import com.app.emsx.dtos.author.AuthorResponse;
import com.app.emsx.entities.Author;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorMapper {

    default Author toEntity(AuthorRequest dto) {
        if (dto == null) return null;
        Author entity = new Author();
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        // Convertir String a LocalDate manualmente para evitar problemas de zona horaria
        if (dto.getBirthDate() != null && !dto.getBirthDate().trim().isEmpty()) {
            try {
                LocalDate birthDate = LocalDate.parse(dto.getBirthDate().trim());
                entity.setBirthDate(birthDate);
            } catch (Exception e) {
                throw new IllegalArgumentException("Formato de fecha inválido: " + dto.getBirthDate());
            }
        }
        return entity;
    }

    default AuthorResponse toResponse(Author entity) {
        if (entity == null) return null;
        AuthorResponse dto = new AuthorResponse();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        // Convertir LocalDate a String en formato YYYY-MM-DD para evitar problemas de serialización
        if (entity.getBirthDate() != null) {
            dto.setBirthDate(entity.getBirthDate().toString());
        } else {
            dto.setBirthDate(null);
        }
        if (entity.getBooks() != null) {
            dto.setBookIds(entity.getBooks().stream()
                    .map(book -> book.getId())
                    .collect(Collectors.toList()));
        } else {
            dto.setBookIds(new ArrayList<>());
        }
        return dto;
    }

    default List<AuthorResponse> toResponseList(List<Author> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }

    default void updateEntityFromRequest(AuthorRequest dto, Author entity) {
        if (dto == null || entity == null) return;
        if (dto.getFirstName() != null && !dto.getFirstName().isBlank())
            entity.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null && !dto.getLastName().isBlank())
            entity.setLastName(dto.getLastName());
        if (dto.getEmail() != null && !dto.getEmail().isBlank())
            entity.setEmail(dto.getEmail());
        // Convertir String a LocalDate manualmente para evitar problemas de zona horaria
        if (dto.getBirthDate() != null && !dto.getBirthDate().trim().isEmpty()) {
            try {
                LocalDate birthDate = LocalDate.parse(dto.getBirthDate().trim());
                entity.setBirthDate(birthDate);
            } catch (Exception e) {
                throw new IllegalArgumentException("Formato de fecha inválido: " + dto.getBirthDate());
            }
        }
    }
}


