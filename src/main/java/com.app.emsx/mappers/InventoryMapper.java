package com.app.emsx.mappers;

import com.app.emsx.dtos.inventory.InventoryRequest;
import com.app.emsx.dtos.inventory.InventoryResponse;
import com.app.emsx.entities.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryMapper {

    default Inventory toEntity(InventoryRequest dto) {
        if (dto == null) return null;
        Inventory entity = new Inventory();
        entity.setTotalCopies(dto.getTotalCopies() != null ? dto.getTotalCopies() : 0);
        entity.setAvailableCopies(dto.getAvailableCopies() != null ? dto.getAvailableCopies() : 0);
        entity.setBorrowedCopies(dto.getBorrowedCopies() != null ? dto.getBorrowedCopies() : 0);
        entity.setObservations(dto.getObservations());
        entity.setLastUpdated(LocalDateTime.now());
        return entity;
    }

    default InventoryResponse toResponse(Inventory entity) {
        if (entity == null) return null;
        InventoryResponse dto = new InventoryResponse();
        dto.setId(entity.getId());
        dto.setTotalCopies(entity.getTotalCopies());
        dto.setAvailableCopies(entity.getAvailableCopies());
        dto.setBorrowedCopies(entity.getBorrowedCopies());
        dto.setObservations(entity.getObservations());
        dto.setLastUpdated(entity.getLastUpdated());
        if (entity.getBook() != null) {
            dto.setBookId(entity.getBook().getId());
            dto.setBookTitle(entity.getBook().getTitle());
        }
        return dto;
    }

    default List<InventoryResponse> toResponseList(List<Inventory> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }
}


