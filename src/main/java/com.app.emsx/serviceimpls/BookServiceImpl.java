package com.app.emsx.serviceimpls;

import com.app.emsx.dtos.book.BookRequest;
import com.app.emsx.dtos.book.BookResponse;
import com.app.emsx.entities.Author;
import com.app.emsx.entities.Book;
import com.app.emsx.exceptions.BusinessRuleException;
import com.app.emsx.exceptions.ResourceNotFoundException;
import com.app.emsx.mappers.BookMapper;
import com.app.emsx.repositories.AuthorRepository;
import com.app.emsx.repositories.BookRepository;
import com.app.emsx.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 📚 BookServiceImpl
 * -----------------------------------------------------
 * Servicio para gestión de libros.
 * ✅ Aplica reglas de negocio, validaciones y conversiones DTO ↔ Entity.
 */
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository repository;
    private final AuthorRepository authorRepository;
    private final BookMapper mapper;

    @Override
    public BookResponse create(BookRequest request) {
        // Validar longitud y formato de campos
        if (request.getTitle() != null) {
            if (request.getTitle().length() > 20) {
                throw new BusinessRuleException("El título no puede tener más de 20 caracteres");
            }
            if (!request.getTitle().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$")) {
                throw new BusinessRuleException("El título solo debe contener letras, no números ni símbolos");
            }
        }
        if (request.getGenre() != null) {
            if (request.getGenre().length() > 20) {
                throw new BusinessRuleException("El género no puede tener más de 20 caracteres");
            }
            if (!request.getGenre().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$")) {
                throw new BusinessRuleException("El género solo debe contener letras, no números ni símbolos");
            }
        }
        if (request.getEditorial() != null) {
            if (request.getEditorial().length() > 20) {
                throw new BusinessRuleException("La editorial no puede tener más de 20 caracteres");
            }
            if (!request.getEditorial().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$")) {
                throw new BusinessRuleException("La editorial solo debe contener letras, no números ni símbolos");
            }
        }

        // Verificar existencia del autor
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Autor no encontrado con ID: " + request.getAuthorId()));

        // Validar que no exista un libro con el mismo título y autor
        if (repository.findByTitleAndAuthorId(request.getTitle().trim(), request.getAuthorId()).isPresent()) {
            throw new BusinessRuleException("Ya existe un libro con el título \"" + request.getTitle().trim() + "\" del mismo autor");
        }

        // Validar fecha de publicación
        if (request.getPublicationDate() != null && !request.getPublicationDate().trim().isEmpty()) {
            try {
                LocalDate publicationDate = LocalDate.parse(request.getPublicationDate().trim());
                LocalDate firstBookDate = LocalDate.of(868, 1, 1); // Primer libro impreso conocido
                LocalDate today = LocalDate.now();
                
                if (publicationDate.isBefore(firstBookDate)) {
                    throw new BusinessRuleException("La fecha de publicación no puede ser anterior al año 868 (primer libro impreso conocido)");
                }
                if (publicationDate.isAfter(today)) {
                    throw new BusinessRuleException("La fecha de publicación no puede ser futura");
                }
                
                // Validar que la fecha de publicación no sea anterior a la fecha de nacimiento del autor
                if (author.getBirthDate() != null && publicationDate.isBefore(author.getBirthDate())) {
                    throw new BusinessRuleException("La fecha de publicación no puede ser anterior a la fecha de nacimiento del autor");
                }
            } catch (Exception e) {
                if (e instanceof BusinessRuleException) {
                    throw e;
                }
                throw new BusinessRuleException("Formato de fecha inválido: " + request.getPublicationDate());
            }
        }

        // Mapear DTO → Entity
        Book book = mapper.toEntity(request);
        book.setAuthor(author);

        // Guardar y retornar
        return mapper.toResponse(repository.save(book));
    }

    @Override
    public BookResponse update(Long id, BookRequest request) {
        Book book = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + id));

        // Validar longitud y formato de campos
        if (request.getTitle() != null) {
            if (request.getTitle().length() > 20) {
                throw new BusinessRuleException("El título no puede tener más de 20 caracteres");
            }
            if (!request.getTitle().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$")) {
                throw new BusinessRuleException("El título solo debe contener letras, no números ni símbolos");
            }
        }
        if (request.getGenre() != null) {
            if (request.getGenre().length() > 20) {
                throw new BusinessRuleException("El género no puede tener más de 20 caracteres");
            }
            if (!request.getGenre().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$")) {
                throw new BusinessRuleException("El género solo debe contener letras, no números ni símbolos");
            }
        }
        if (request.getEditorial() != null) {
            if (request.getEditorial().length() > 20) {
                throw new BusinessRuleException("La editorial no puede tener más de 20 caracteres");
            }
            if (!request.getEditorial().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$")) {
                throw new BusinessRuleException("La editorial solo debe contener letras, no números ni símbolos");
            }
        }

        // Validar que no se intente modificar el autor (es inmutable)
        if (request.getAuthorId() != null && book.getAuthor() != null && !request.getAuthorId().equals(book.getAuthor().getId())) {
            throw new BusinessRuleException("No se puede modificar el autor de un libro existente. El autor no se puede cambiar después de crear el libro.");
        }
        
        // Obtener el autor del libro existente para validar la fecha de publicación
        Author author = book.getAuthor();

        // Validar que no exista otro libro con el mismo título y autor (excluyendo el libro actual)
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty() && book.getAuthor() != null) {
            Optional<Book> existingBook = repository.findByTitleAndAuthorId(request.getTitle().trim(), book.getAuthor().getId());
            if (existingBook.isPresent() && !existingBook.get().getId().equals(id)) {
                throw new BusinessRuleException("Ya existe un libro con el título \"" + request.getTitle().trim() + "\" del mismo autor");
            }
        }

        // Validar fecha de publicación
        if (request.getPublicationDate() != null && !request.getPublicationDate().trim().isEmpty()) {
            try {
                LocalDate publicationDate = LocalDate.parse(request.getPublicationDate().trim());
                LocalDate firstBookDate = LocalDate.of(868, 1, 1); // Primer libro impreso conocido
                LocalDate today = LocalDate.now();
                
                if (publicationDate.isBefore(firstBookDate)) {
                    throw new BusinessRuleException("La fecha de publicación no puede ser anterior al año 868 (primer libro impreso conocido)");
                }
                if (publicationDate.isAfter(today)) {
                    throw new BusinessRuleException("La fecha de publicación no puede ser futura");
                }
                
                // Validar que la fecha de publicación no sea anterior a la fecha de nacimiento del autor
                if (author != null && author.getBirthDate() != null && publicationDate.isBefore(author.getBirthDate())) {
                    throw new BusinessRuleException("La fecha de publicación no puede ser anterior a la fecha de nacimiento del autor");
                }
            } catch (Exception e) {
                if (e instanceof BusinessRuleException) {
                    throw e;
                }
                throw new BusinessRuleException("Formato de fecha inválido: " + request.getPublicationDate());
            }
        }

        mapper.updateEntityFromRequest(request, book);
        // El autor no se actualiza (es inmutable)

        return mapper.toResponse(repository.save(book));
    }

    @Override
    public List<BookResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public BookResponse findById(Long id) {
        Book book = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + id));
        return mapper.toResponse(book);
    }

    @Override
    public void delete(Long id) {
        Book book = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + id));

        // Forzar carga de préstamos (lazy loading)
        if (book.getLoans() != null) {
            int loanCount = book.getLoans().size(); // Esto fuerza la carga de la colección
            if (loanCount > 0) {
                // Verificar si hay préstamos activos
                boolean hasActiveLoans = book.getLoans().stream()
                        .anyMatch(loan -> loan.getState() != null && 
                                (loan.getState().equalsIgnoreCase("ACTIVE") || 
                                 loan.getState().equalsIgnoreCase("OVERDUE")));
                
                if (hasActiveLoans) {
                    throw new BusinessRuleException("Este libro se encuentra en un préstamo activo. Debe ser devuelto primero.");
                }
            }
        }

        repository.delete(book);
    }
}

