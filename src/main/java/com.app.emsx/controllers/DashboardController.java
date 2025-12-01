package com.app.emsx.controllers;

import com.app.emsx.repositories.StudentRepository;
import com.app.emsx.repositories.AuthorRepository;
import com.app.emsx.repositories.BookRepository;
import com.app.emsx.repositories.LoanRepository;
import com.app.emsx.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // ✅ Permitir acceso desde el frontend (localhost:3000)
public class DashboardController {

    private final StudentRepository studentRepository;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final InventoryRepository inventoryRepository;

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            long students = studentRepository.count();
            long authors = authorRepository.count();
            long books = bookRepository.count();
            long loans = loanRepository.count();
            long inventories = inventoryRepository.count();

            // Agrupar libros por autor
            List<Map<String, Object>> booksPerAuthor = new ArrayList<>();
            authorRepository.findAll().forEach(author -> {
                int bookCount = author.getBooks() != null ? author.getBooks().size() : 0;
                booksPerAuthor.add(Map.of(
                        "name", author.getFirstName() + " " + author.getLastName(),
                        "value", bookCount
                ));
            });

            Map<String, Object> response = new HashMap<>();
            response.put("students", students);
            response.put("authors", authors);
            response.put("books", books);
            response.put("loans", loans);
            response.put("inventories", inventories);
            response.put("employeesPerDept", booksPerAuthor); // Mantener nombre para compatibilidad con frontend

            // Simular datos de tendencias
            List<Map<String, Object>> trendData = List.of(
                    Map.of("month", "Jan", "newLoans", 5, "returns", 2),
                    Map.of("month", "Feb", "newLoans", 3, "returns", 1),
                    Map.of("month", "Mar", "newLoans", 6, "returns", 3),
                    Map.of("month", "Apr", "newLoans", 4, "returns", 2),
                    Map.of("month", "May", "newLoans", 8, "returns", 1)
            );

            response.put("trendData", trendData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error loading dashboard: " + e.getMessage()));
        }
    }
}
