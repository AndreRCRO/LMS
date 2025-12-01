package com.app.emsx.dtos.loan;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanResponse {
    private Long id;
    private String state;
    private String observations;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate dateLoan;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate dueDate;

    private double amount;
    private Long studentId;
    private String studentName;
    private Long bookId;
    private String bookTitle;
}

