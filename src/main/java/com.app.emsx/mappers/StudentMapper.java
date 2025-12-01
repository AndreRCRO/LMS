package com.app.emsx.mappers;

import com.app.emsx.dtos.student.StudentRequest;
import com.app.emsx.dtos.student.StudentResponse;
import com.app.emsx.entities.Student;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {

    default Student toEntity(StudentRequest dto) {
        if (dto == null) return null;
        Student entity = new Student();
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setCareer(dto.getCareer());
        entity.setCodigo(dto.getCodigo());
        return entity;
    }

    default StudentResponse toResponse(Student entity) {
        if (entity == null) return null;
        StudentResponse dto = new StudentResponse();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setCareer(entity.getCareer());
        dto.setCodigo(entity.getCodigo());
        return dto;
    }

    default List<StudentResponse> toResponseList(List<Student> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }

    default void updateEntityFromRequest(StudentRequest dto, Student entity) {
        if (dto == null || entity == null) return;
        if (dto.getFirstName() != null && !dto.getFirstName().isBlank())
            entity.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null && !dto.getLastName().isBlank())
            entity.setLastName(dto.getLastName());
        if (dto.getEmail() != null && !dto.getEmail().isBlank())
            entity.setEmail(dto.getEmail());
        if (dto.getPhone() != null) entity.setPhone(dto.getPhone());
        if (dto.getCareer() != null) entity.setCareer(dto.getCareer());
        if (dto.getCodigo() != null && !dto.getCodigo().isBlank())
            entity.setCodigo(dto.getCodigo());
    }
}


