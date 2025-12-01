package com.app.emsx.serviceimpls;

import com.app.emsx.dtos.author.AuthorRequest;
import com.app.emsx.dtos.author.AuthorResponse;
import com.app.emsx.entities.Author;
import com.app.emsx.exceptions.BusinessRuleException;
import com.app.emsx.exceptions.ResourceNotFoundException;
import com.app.emsx.mappers.AuthorMapper;
import com.app.emsx.repositories.AuthorRepository;
import com.app.emsx.services.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * ✍️ AuthorServiceImpl
 * -----------------------------------------------------
 * Servicio para gestión de autores.
 * ✅ Aplica reglas de negocio, validaciones y conversiones DTO ↔ Entity.
 */
@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository repository;
    private final AuthorMapper mapper;

    @Override
    public AuthorResponse create(AuthorRequest request) {
        // Validar longitud de campos
        if (request.getFirstName() != null) {
            if (request.getFirstName().trim().length() < 2) {
                throw new BusinessRuleException("El nombre debe tener mínimo 2 letras");
            }
            if (request.getFirstName().length() > 25) {
                throw new BusinessRuleException("El nombre no puede tener más de 25 letras");
            }
        }
        if (request.getLastName() != null && request.getLastName().length() > 20) {
            throw new BusinessRuleException("El apellido no puede tener más de 20 letras");
        }

        // Validar email: solo caracteres ASCII válidos, parte local 4-30, dominio y extensión máx 10
        if (request.getEmail() != null) {
            // Validar espacios al inicio o final
            if (request.getEmail().startsWith(" ") || request.getEmail().endsWith(" ")) {
                throw new BusinessRuleException("El email no puede empezar ni terminar con un espacio en blanco");
            }
            String email = request.getEmail().trim();
            if (!email.matches("^[a-zA-Z0-9._]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                throw new BusinessRuleException("El email debe contener solo letras, números, puntos y guiones bajos");
            }
            String[] parts = email.split("@");
            if (parts.length == 2) {
                String localPart = parts[0];
                String domainPart = parts[1];
                String[] domainParts = domainPart.split("\\.");
                
                // Validar parte local (antes del @)
                if (localPart.length() < 4) {
                    throw new BusinessRuleException("La parte antes del @ debe tener mínimo 4 caracteres");
                }
                if (localPart.length() > 30) {
                    throw new BusinessRuleException("La parte antes del @ debe tener máximo 30 caracteres");
                }
                
                // Validar dominio y extensión
                if (domainParts.length >= 2) {
                    // Dominio (después del @ y antes del último punto)
                    String domain = String.join(".", java.util.Arrays.copyOf(domainParts, domainParts.length - 1));
                    if (domain.length() > 20) {
                        throw new BusinessRuleException("El dominio (después del @) debe tener máximo 20 caracteres");
                    }
                    
                    // Extensión (después del último punto)
                    String extension = domainParts[domainParts.length - 1];
                    if (extension.length() > 20) {
                        throw new BusinessRuleException("La extensión (después del último punto) debe tener máximo 20 caracteres");
                    }
                }
            }
        }
        
        // Verificar duplicado de email
        if (repository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Ya existe un usuario con ese gmail");
        }

        // Validar fecha de nacimiento
        if (request.getBirthDate() != null && !request.getBirthDate().trim().isEmpty()) {
            try {
                LocalDate birthDate = LocalDate.parse(request.getBirthDate().trim());
                LocalDate today = LocalDate.now();
                LocalDate reasonablePast = LocalDate.of(1500, 1, 1); // Fecha razonable mínima
                LocalDate minimumBirthDate = today.minusYears(5); // El autor debe tener al menos 5 años
                
                if (birthDate.isAfter(today)) {
                    throw new BusinessRuleException("La fecha de nacimiento no puede ser futura");
                }
                if (birthDate.isBefore(reasonablePast)) {
                    throw new BusinessRuleException("La fecha de nacimiento no puede ser anterior al año 1500");
                }
                // Validar que el autor tenga al menos 5 años
                if (birthDate.isAfter(minimumBirthDate)) {
                    throw new BusinessRuleException("No se puede crear un autor menor de 5 años. La fecha de nacimiento debe ser anterior a " + 
                        minimumBirthDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            } catch (Exception e) {
                if (e instanceof BusinessRuleException) {
                    throw e;
                }
                throw new BusinessRuleException("Formato de fecha inválido: " + request.getBirthDate());
            }
        }

        Author entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public AuthorResponse update(Long id, AuthorRequest request) {
        Author author = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autor no encontrado con ID: " + id));

        // Validar longitud de campos
        if (request.getFirstName() != null) {
            if (request.getFirstName().trim().length() < 2) {
                throw new BusinessRuleException("El nombre debe tener mínimo 2 letras");
            }
            if (request.getFirstName().length() > 25) {
                throw new BusinessRuleException("El nombre no puede tener más de 25 letras");
            }
        }
        if (request.getLastName() != null && request.getLastName().length() > 20) {
            throw new BusinessRuleException("El apellido no puede tener más de 20 letras");
        }

        // Validar email: solo caracteres ASCII válidos, parte local 4-30, dominio y extensión máx 10
        if (request.getEmail() != null) {
            // Validar espacios al inicio o final
            if (request.getEmail().startsWith(" ") || request.getEmail().endsWith(" ")) {
                throw new BusinessRuleException("El email no puede empezar ni terminar con un espacio en blanco");
            }
            String email = request.getEmail().trim();
            if (!email.matches("^[a-zA-Z0-9._]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                throw new BusinessRuleException("El email debe contener solo letras, números, puntos y guiones bajos");
            }
            String[] parts = email.split("@");
            if (parts.length == 2) {
                String localPart = parts[0];
                String domainPart = parts[1];
                String[] domainParts = domainPart.split("\\.");
                
                // Validar parte local (antes del @)
                if (localPart.length() < 4) {
                    throw new BusinessRuleException("La parte antes del @ debe tener mínimo 4 caracteres");
                }
                if (localPart.length() > 30) {
                    throw new BusinessRuleException("La parte antes del @ debe tener máximo 30 caracteres");
                }
                
                // Validar dominio y extensión
                if (domainParts.length >= 2) {
                    // Dominio (después del @ y antes del último punto)
                    String domain = String.join(".", java.util.Arrays.copyOf(domainParts, domainParts.length - 1));
                    if (domain.length() > 20) {
                        throw new BusinessRuleException("El dominio (después del @) debe tener máximo 20 caracteres");
                    }
                    
                    // Extensión (después del último punto)
                    String extension = domainParts[domainParts.length - 1];
                    if (extension.length() > 20) {
                        throw new BusinessRuleException("La extensión (después del último punto) debe tener máximo 20 caracteres");
                    }
                }
            }
        }
        
        // Validar email duplicado
        if (!author.getEmail().equals(request.getEmail()) &&
                repository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Ya existe un usuario con ese gmail");
        }

        // Validar fecha de nacimiento
        if (request.getBirthDate() != null && !request.getBirthDate().trim().isEmpty()) {
            try {
                LocalDate birthDate = LocalDate.parse(request.getBirthDate().trim());
                LocalDate today = LocalDate.now();
                LocalDate reasonablePast = LocalDate.of(1500, 1, 1); // Fecha razonable mínima
                LocalDate minimumBirthDate = today.minusYears(5); // El autor debe tener al menos 5 años
                
                if (birthDate.isAfter(today)) {
                    throw new BusinessRuleException("La fecha de nacimiento no puede ser futura");
                }
                if (birthDate.isBefore(reasonablePast)) {
                    throw new BusinessRuleException("La fecha de nacimiento no puede ser anterior al año 1500");
                }
                // Validar que el autor tenga al menos 5 años
                if (birthDate.isAfter(minimumBirthDate)) {
                    throw new BusinessRuleException("No se puede crear un autor menor de 5 años. La fecha de nacimiento debe ser anterior a " + 
                        minimumBirthDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            } catch (Exception e) {
                if (e instanceof BusinessRuleException) {
                    throw e;
                }
                throw new BusinessRuleException("Formato de fecha inválido: " + request.getBirthDate());
            }
        }

        mapper.updateEntityFromRequest(request, author);
        return mapper.toResponse(repository.save(author));
    }

    @Override
    public List<AuthorResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public AuthorResponse findById(Long id) {
        Author author = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autor no encontrado con ID: " + id));
        return mapper.toResponse(author);
    }

    @Override
    public void delete(Long id) {
        Author author = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autor no encontrado con ID: " + id));

        // Forzar carga de libros (lazy loading)
        if (author.getBooks() != null) {
            int bookCount = author.getBooks().size(); // Esto fuerza la carga de la colección
            
            if (bookCount > 0) {
                // Verificar si alguno de los libros del autor tiene préstamos activos
                boolean hasActiveLoans = author.getBooks().stream()
                        .anyMatch(book -> {
                            // Forzar carga de préstamos de cada libro
                            if (book.getLoans() != null) {
                                int loans = book.getLoans().size(); // Fuerza carga
                                if (loans > 0) {
                                    // Verificar si hay préstamos activos
                                    return book.getLoans().stream()
                                            .anyMatch(loan -> loan.getState() != null && 
                                                    (loan.getState().equalsIgnoreCase("ACTIVE") || 
                                                     loan.getState().equalsIgnoreCase("OVERDUE")));
                                }
                            }
                            return false;
                        });
                
                if (hasActiveLoans) {
                    throw new BusinessRuleException("Este autor tiene libros que se encuentran en préstamos activos. Deben ser devueltos primero.");
                }
                
                throw new BusinessRuleException("Este autor tiene libros asociados. No se puede eliminar.");
            }
        }

        repository.delete(author);
    }
}

