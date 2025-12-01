package com.app.emsx.dtos.return_;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnResponse {
    private Long id;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateReturn;

    private String observations;
    private double penalty;
    private Long loanId;
}


