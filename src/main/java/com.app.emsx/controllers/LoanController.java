package com.app.emsx.controllers;

import com.app.emsx.dtos.loan.LoanRequest;
import com.app.emsx.dtos.loan.LoanResponse;
import com.app.emsx.common.ApiResponse;
import com.app.emsx.services.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 📖 LoanController
 * -----------------------------------------------------
 * CRUD completo con respuestas estandarizadas.
 */
@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService service;

    @PostMapping
    public ResponseEntity<ApiResponse<LoanResponse>> create(@Valid @RequestBody LoanRequest request) {
        LoanResponse created = service.create(request);
        return ResponseEntity.ok(ApiResponse.ok("Préstamo creado correctamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LoanResponse>> update(
            @PathVariable Long id, @Valid @RequestBody LoanRequest request) {
        LoanResponse updated = service.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Préstamo actualizado correctamente", updated));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LoanResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Lista de préstamos", service.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LoanResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Préstamo encontrado", service.findById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Préstamo eliminado correctamente", null));
    }
}


