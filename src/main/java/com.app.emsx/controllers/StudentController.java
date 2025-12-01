package com.app.emsx.controllers;

import com.app.emsx.dtos.student.StudentRequest;
import com.app.emsx.dtos.student.StudentResponse;
import com.app.emsx.common.ApiResponse;
import com.app.emsx.services.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 🎓 StudentController
 * -----------------------------------------------------
 * CRUD completo + validaciones automáticas.
 */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService service;

    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> create(@Valid @RequestBody StudentRequest request) {
        StudentResponse created = service.create(request);
        return ResponseEntity.ok(ApiResponse.ok("Estudiante creado correctamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> update(
            @PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        StudentResponse updated = service.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Estudiante actualizado correctamente", updated));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Lista de estudiantes", service.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Estudiante encontrado", service.findById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Estudiante eliminado correctamente", null));
    }
}


