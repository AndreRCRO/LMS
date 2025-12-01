package com.app.emsx.mappers;

import com.app.emsx.dtos.loan.LoanRequest;
import com.app.emsx.dtos.loan.LoanResponse;
import com.app.emsx.entities.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanMapper {

    default Loan toEntity(LoanRequest dto) {
        if (dto == null) return null;
        Loan entity = new Loan();
        entity.setState(dto.getState());
        entity.setObservations(dto.getObservations());
        entity.setDateLoan(dto.getDateLoan());
        entity.setDueDate(dto.getDueDate());
        entity.setAmount(dto.getAmount());
        return entity;
    }

    default LoanResponse toResponse(Loan entity) {
        if (entity == null) return null;
        LoanResponse dto = new LoanResponse();
        dto.setId(entity.getId());
        dto.setState(entity.getState());
        dto.setObservations(entity.getObservations());
        dto.setDateLoan(entity.getDateLoan());
        dto.setDueDate(entity.getDueDate());
        dto.setAmount(entity.getAmount());
        if (entity.getStudent() != null) {
            dto.setStudentId(entity.getStudent().getId());
            dto.setStudentName(entity.getStudent().getFirstName() + " " + entity.getStudent().getLastName());
        }
        if (entity.getBook() != null) {
            dto.setBookId(entity.getBook().getId());
            dto.setBookTitle(entity.getBook().getTitle());
        }
        return dto;
    }

    default List<LoanResponse> toResponseList(List<Loan> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }

    default void updateEntityFromRequest(LoanRequest dto, Loan entity) {
        if (dto == null || entity == null) return;
        // Permitir actualizar estado, observaciones y monto
        if (dto.getState() != null) entity.setState(dto.getState());
        if (dto.getObservations() != null) entity.setObservations(dto.getObservations());
        // Permitir actualizar monto para corregir errores
        entity.setAmount(dto.getAmount());
        // No actualizar: dateLoan, dueDate, studentId, bookId
    }
}


