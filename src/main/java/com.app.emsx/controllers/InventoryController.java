package com.app.emsx.controllers;

import com.app.emsx.dtos.inventory.InventoryRequest;
import com.app.emsx.dtos.inventory.InventoryResponse;
import com.app.emsx.common.ApiResponse;
import com.app.emsx.services.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 📦 InventoryController
 * -----------------------------------------------------
 * CRUD completo con respuestas estandarizadas.
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService service;

    @PostMapping
    public ResponseEntity<ApiResponse<InventoryResponse>> create(@Valid @RequestBody InventoryRequest request) {
        InventoryResponse created = service.create(request);
        return ResponseEntity.ok(ApiResponse.ok("Inventario creado correctamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryResponse>> update(
            @PathVariable Long id, @Valid @RequestBody InventoryRequest request) {
        InventoryResponse updated = service.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Inventario actualizado correctamente", updated));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Lista de inventarios", service.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Inventario encontrado", service.findById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Inventario eliminado correctamente", null));
    }
}


