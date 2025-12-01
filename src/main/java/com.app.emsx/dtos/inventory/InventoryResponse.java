package com.app.emsx.dtos.inventory;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {
    private Long id;
    private Integer totalCopies;
    private Integer availableCopies;
    private Integer borrowedCopies;
    private String observations;
    private LocalDateTime lastUpdated;
    private Long bookId;
    private String bookTitle;
}


