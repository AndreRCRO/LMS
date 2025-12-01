package com.app.emsx.controllers;

import com.app.emsx.dtos.author.AuthorRequest;
import com.app.emsx.dtos.author.AuthorResponse;
import com.app.emsx.common.ApiResponse;
import com.app.emsx.services.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ✍️ AuthorController
 * -----------------------------------------------------
 * CRUD completo con respuestas estandarizadas.
 */
@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService service;

    @PostMapping
    public ResponseEntity<ApiResponse<AuthorResponse>> create(@Valid @RequestBody AuthorRequest request) {
        AuthorResponse created = service.create(request);
        return ResponseEntity.ok(ApiResponse.ok("Autor creado correctamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorResponse>> update(
            @PathVariable Long id, @Valid @RequestBody AuthorRequest request) {
        AuthorResponse updated = service.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Autor actualizado correctamente", updated));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuthorResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Lista de autores", service.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Autor encontrado", service.findById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Autor eliminado correctamente", null));
    }
}


