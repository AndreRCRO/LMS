package com.app.emsx.controllers;

import com.app.emsx.dtos.return_.ReturnRequest;
import com.app.emsx.dtos.return_.ReturnResponse;
import com.app.emsx.common.ApiResponse;
import com.app.emsx.services.ReturnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 🔄 ReturnController
 * -----------------------------------------------------
 * CRUD completo con respuestas estandarizadas.
 */
@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ReturnResponse>> create(@Valid @RequestBody ReturnRequest request) {
        ReturnResponse created = service.create(request);
        return ResponseEntity.ok(ApiResponse.ok("Devolución creada correctamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReturnResponse>> update(
            @PathVariable Long id, @Valid @RequestBody ReturnRequest request) {
        // Las devoluciones no se pueden editar una vez creadas
        return ResponseEntity.status(405).body(
            ApiResponse.fail("Las devoluciones no se pueden editar. Una vez creada, la devolución es inmutable.")
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReturnResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Lista de devoluciones", service.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReturnResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Devolución encontrada", service.findById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Devolución eliminada correctamente", null));
    }
}


