package com.app.emsx.serviceimpls;

import com.app.emsx.dtos.student.StudentRequest;
import com.app.emsx.dtos.student.StudentResponse;
import com.app.emsx.entities.Student;
import com.app.emsx.exceptions.BusinessRuleException;
import com.app.emsx.exceptions.ResourceNotFoundException;
import com.app.emsx.mappers.StudentMapper;
import com.app.emsx.repositories.StudentRepository;
import com.app.emsx.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 🎓 StudentServiceImpl
 * -----------------------------------------------------
 * Servicio principal para gestión de estudiantes.
 * ✅ Aplica reglas de negocio, validaciones y conversiones DTO ↔ Entity.
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;
    private final StudentMapper mapper;

    /**
     * ✅ Crear nuevo estudiante
     * - Verifica que el correo no esté duplicado.
     * - Verifica que el código no esté duplicado.
     */
    @Override
    public StudentResponse create(StudentRequest request) {
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
        if (request.getPhone() != null && request.getPhone().length() != 8) {
            throw new BusinessRuleException("El teléfono debe tener exactamente 8 dígitos");
        }
        if (request.getCareer() != null && request.getCareer().length() > 65) {
            throw new BusinessRuleException("La carrera no puede tener más de 65 letras");
        }
        // Validar email: solo caracteres ASCII válidos, parte local 4-30, dominio y extensión máx 10
        if (request.getEmail() != null) {
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
        // Validar código: exactamente 10 caracteres, 3 letras seguidas de 7 números
        if (request.getCodigo() != null) {
            // Validar espacios al inicio o final
            if (request.getCodigo().startsWith(" ") || request.getCodigo().endsWith(" ")) {
                throw new BusinessRuleException("El código no puede empezar ni terminar con un espacio en blanco");
            }
            String codigo = request.getCodigo().trim();
            if (codigo.length() != 10) {
                throw new BusinessRuleException("El código debe tener exactamente 10 caracteres (3 letras y 7 números)");
            }
            if (!codigo.matches("^[A-Za-z]{3}[0-9]{7}$")) {
                throw new BusinessRuleException("El código debe tener 3 letras seguidas de 7 números (ejemplo: ABC1234567)");
            }
        }

        // Verificar duplicado de email
        if (repository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Ya existe un usuario con ese gmail");
        }

        // Verificar duplicado de codigo
        if (repository.existsByCodigo(request.getCodigo())) {
            throw new BusinessRuleException("Ya existe un estudiante con el código: " + request.getCodigo());
        }

        // Verificar duplicado de teléfono
        if (repository.existsByPhone(request.getPhone())) {
            throw new BusinessRuleException("Ya existe un usuario con ese teléfono");
        }

        // Mapear DTO → Entity
        Student student = mapper.toEntity(request);

        // Guardar y retornar
        return mapper.toResponse(repository.save(student));
    }

    /**
     * ✅ Actualizar estudiante existente
     * - Verifica duplicados de email.
     * - Verifica duplicados de codigo.
     */
    @Override
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));

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
        if (request.getPhone() != null) {
            // Validar espacios al inicio o final
            if (request.getPhone().startsWith(" ") || request.getPhone().endsWith(" ")) {
                throw new BusinessRuleException("El teléfono no puede empezar ni terminar con un espacio en blanco");
            }
            if (request.getPhone().trim().length() != 8) {
                throw new BusinessRuleException("El teléfono debe tener exactamente 8 dígitos");
            }
        }
        if (request.getCareer() != null && request.getCareer().length() > 65) {
            throw new BusinessRuleException("La carrera no puede tener más de 65 letras");
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
        // Validar código: exactamente 10 caracteres, 3 letras seguidas de 7 números
        if (request.getCodigo() != null) {
            // Validar espacios al inicio o final
            if (request.getCodigo().startsWith(" ") || request.getCodigo().endsWith(" ")) {
                throw new BusinessRuleException("El código no puede empezar ni terminar con un espacio en blanco");
            }
            String codigo = request.getCodigo().trim();
            if (codigo.length() != 10) {
                throw new BusinessRuleException("El código debe tener exactamente 10 caracteres (3 letras y 7 números)");
            }
            if (!codigo.matches("^[A-Za-z]{3}[0-9]{7}$")) {
                throw new BusinessRuleException("El código debe tener 3 letras seguidas de 7 números (ejemplo: ABC1234567)");
            }
        }

        // Validar email duplicado
        if (!student.getEmail().equals(request.getEmail()) &&
                repository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Ya existe un usuario con ese gmail");
        }

        // Validar codigo duplicado
        if (!student.getCodigo().equals(request.getCodigo()) &&
                repository.existsByCodigo(request.getCodigo())) {
            throw new BusinessRuleException("Ya existe un estudiante con el código: " + request.getCodigo());
        }

        // Validar teléfono duplicado
        if (student.getPhone() != null && request.getPhone() != null &&
                !student.getPhone().equals(request.getPhone()) &&
                repository.existsByPhone(request.getPhone())) {
            throw new BusinessRuleException("Ya existe un usuario con ese teléfono");
        }

        mapper.updateEntityFromRequest(request, student);

        return mapper.toResponse(repository.save(student));
    }

    /**
     * ✅ Obtener todos los estudiantes
     */
    @Override
    public List<StudentResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    /**
     * ✅ Buscar estudiante por ID
     */
    @Override
    public StudentResponse findById(Long id) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));
        return mapper.toResponse(student);
    }

    /**
     * ✅ Eliminar estudiante por ID
     * - Si tiene préstamos, lanzar excepción de negocio.
     */
    @Override
    public void delete(Long id) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));

        // Forzar carga de préstamos (lazy loading)
        if (student.getLoans() != null) {
            int loanCount = student.getLoans().size(); // Esto fuerza la carga de la colección
            if (loanCount > 0) {
                // Verificar si hay préstamos activos
                boolean hasActiveLoans = student.getLoans().stream()
                        .anyMatch(loan -> loan.getState() != null && 
                                (loan.getState().equalsIgnoreCase("ACTIVE") || 
                                 loan.getState().equalsIgnoreCase("OVERDUE")));
                
                if (hasActiveLoans) {
                    throw new BusinessRuleException("Este estudiante se encuentra en un préstamo activo. Debe ser devuelto primero.");
                }
            }
        }

        repository.delete(student);
    }
}

