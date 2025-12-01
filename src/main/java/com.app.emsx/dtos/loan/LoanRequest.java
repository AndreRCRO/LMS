package com.app.emsx.dtos.loan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"id", "studentName", "bookTitle"}, ignoreUnknown = true)
public class LoanRequest {
    private String state;
    private String observations;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @NotNull(message = "La fecha de préstamo es obligatoria")
    private LocalDate dateLoan;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate dueDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "El monto no puede ser negativo")
    @Digits(integer = 4, fraction = 2, message = "El monto debe tener máximo 4 dígitos enteros y 2 decimales (ejemplo: 9999.99)")
    private double amount;

    @NotNull(message = "El ID del estudiante es obligatorio")
    private Long studentId;

    @NotNull(message = "El ID del libro es obligatorio")
    private Long bookId;
}

