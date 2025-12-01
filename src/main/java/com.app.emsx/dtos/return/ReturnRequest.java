package com.app.emsx.dtos.return_;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequest {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateReturn;

    private String observations;
    private double penalty;

    @NotNull(message = "El ID del préstamo es obligatorio")
    private Long loanId;
}


