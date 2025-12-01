package com.app.emsx.mappers;

import com.app.emsx.dtos.return_.ReturnRequest;
import com.app.emsx.dtos.return_.ReturnResponse;
import com.app.emsx.entities.Return;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReturnMapper {

    default Return toEntity(ReturnRequest dto) {
        if (dto == null) return null;
        Return entity = new Return();
        entity.setDateReturn(dto.getDateReturn());
        entity.setObservations(dto.getObservations());
        entity.setPenalty(dto.getPenalty());
        return entity;
    }

    default ReturnResponse toResponse(Return entity) {
        if (entity == null) return null;
        ReturnResponse dto = new ReturnResponse();
        dto.setId(entity.getId());
        dto.setDateReturn(entity.getDateReturn());
        dto.setObservations(entity.getObservations());
        dto.setPenalty(entity.getPenalty());
        if (entity.getLoan() != null) {
            dto.setLoanId(entity.getLoan().getId());
        }
        return dto;
    }

    default List<ReturnResponse> toResponseList(List<Return> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }
}


