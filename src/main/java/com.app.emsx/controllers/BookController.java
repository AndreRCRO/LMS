package com.app.emsx.controllers;

import com.app.emsx.dtos.book.BookRequest;
import com.app.emsx.dtos.book.BookResponse;
import com.app.emsx.common.ApiResponse;
import com.app.emsx.services.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 📚 BookController
 * -----------------------------------------------------
 * CRUD completo con respuestas estandarizadas.
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService service;

    @PostMapping
    public ResponseEntity<ApiResponse<BookResponse>> create(@Valid @RequestBody BookRequest request) {
        BookResponse created = service.create(request);
        return ResponseEntity.ok(ApiResponse.ok("Libro creado correctamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> update(
            @PathVariable Long id, @Valid @RequestBody BookRequest request) {
        BookResponse updated = service.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Libro actualizado correctamente", updated));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Lista de libros", service.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Libro encontrado", service.findById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Libro eliminado correctamente", null));
    }
}


