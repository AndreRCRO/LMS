package com.app.emsx.dtos.inventory;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryRequest {
    @NotNull(message = "El total de copias es obligatorio")
    private Integer totalCopies;
    
    @NotNull(message = "Las copias disponibles son obligatorias")
    private Integer availableCopies;
    
    private Integer borrowedCopies = 0;
    private String observations;
    
    @NotNull(message = "El ID del libro es obligatorio")
    private Long bookId;
}


