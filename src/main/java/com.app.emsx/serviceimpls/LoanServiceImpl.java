package com.app.emsx.serviceimpls;

import com.app.emsx.dtos.loan.LoanRequest;
import com.app.emsx.dtos.loan.LoanResponse;
import com.app.emsx.entities.Book;
import com.app.emsx.entities.Loan;
import com.app.emsx.entities.Student;
import com.app.emsx.exceptions.BusinessRuleException;
import com.app.emsx.exceptions.ResourceNotFoundException;
import com.app.emsx.mappers.LoanMapper;
import com.app.emsx.entities.Inventory;
import com.app.emsx.repositories.BookRepository;
import com.app.emsx.repositories.InventoryRepository;
import com.app.emsx.repositories.LoanRepository;
import com.app.emsx.repositories.StudentRepository;
import com.app.emsx.services.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

/**
 * 📖 LoanServiceImpl
 * -----------------------------------------------------
 * Servicio para gestión de préstamos.
 * ✅ Aplica reglas de negocio, validaciones y conversiones DTO ↔ Entity.
 */
@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository repository;
    private final StudentRepository studentRepository;
    private final BookRepository bookRepository;
    private final InventoryRepository inventoryRepository;
    private final LoanMapper mapper;

    @Override
    public LoanResponse create(LoanRequest request) {
        // Verificar existencia del estudiante
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + request.getStudentId()));

        // Verificar existencia del libro
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + request.getBookId()));

        // Validar inventario: verificar si hay copias disponibles
        // Con el sistema de inventario, múltiples estudiantes pueden tener el mismo libro prestado
        // siempre que haya copias disponibles
        Inventory inventory = book.getInventory();
        if (inventory != null) {
            if (inventory.getAvailableCopies() == null || inventory.getAvailableCopies() <= 0) {
                throw new BusinessRuleException("No hay copias disponibles de este libro. Copias disponibles: " + 
                    (inventory.getAvailableCopies() != null ? inventory.getAvailableCopies() : 0));
            }
        } else {
            // Si el libro no tiene inventario, no se puede prestar
            throw new BusinessRuleException("Este libro no tiene inventario registrado. Debe crear un inventario primero.");
        }
        
        // Validar que el estudiante no tenga un préstamo activo del mismo libro
        // Un estudiante no puede tener múltiples préstamos activos del mismo libro simultáneamente
        List<String> activeStates = Arrays.asList("ACTIVE", "OVERDUE");
        List<Loan> existingActiveLoans = repository.findActiveLoansByStudentAndBook(
                request.getStudentId(), 
                request.getBookId(), 
                activeStates
        );
        if (!existingActiveLoans.isEmpty()) {
            throw new BusinessRuleException("El estudiante ya tiene un préstamo activo de este libro. Debe devolverlo antes de solicitar otro préstamo.");
        }

        // Validar monto
        if (request.getAmount() < 0) {
            throw new BusinessRuleException("El monto no puede ser negativo");
        }
        if (request.getAmount() > 9999.99) {
            throw new BusinessRuleException("El monto máximo permitido es 9999.99 (4 dígitos enteros y 2 decimales)");
        }
        // Validar formato: máximo 4 enteros y 2 decimales
        // Usar BigDecimal para evitar problemas de precisión con double
        BigDecimal amount = BigDecimal.valueOf(request.getAmount());
        amount = amount.setScale(2, RoundingMode.HALF_UP);
        
        // Obtener la parte entera
        long integerPart = amount.longValue();
        
        // Validar que la parte entera tenga máximo 4 dígitos (permitir 1, 2, 3 o 4 dígitos)
        // Un número de 4 dígitos puede ser desde 1000 hasta 9999
        if (integerPart > 9999) {
            throw new BusinessRuleException("El monto no puede tener más de 4 dígitos enteros");
        }

        // Validar fechas de préstamo
        if (request.getDateLoan() != null) {
            LocalDate today = LocalDate.now();
            if (request.getDateLoan().isBefore(today)) {
                throw new BusinessRuleException("La fecha de préstamo no puede ser en el pasado");
            }
            if (request.getDateLoan().isAfter(today)) {
                throw new BusinessRuleException("La fecha de préstamo no puede ser futura");
            }
        }
        if (request.getDateLoan() != null && request.getDueDate() != null) {
            if (request.getDueDate().isBefore(request.getDateLoan())) {
                throw new BusinessRuleException("La fecha de vencimiento no puede ser anterior a la fecha de préstamo");
            }
            // Validar que la fecha de vencimiento no sea más de 7 días después de la fecha de préstamo
            long daysBetween = ChronoUnit.DAYS.between(request.getDateLoan(), request.getDueDate());
            if (daysBetween > 7) {
                throw new BusinessRuleException("La fecha de vencimiento no puede ser más de 7 días después de la fecha de préstamo");
            }
        }

        // Validar que las observaciones no excedan 25 caracteres
        if (request.getObservations() != null && request.getObservations().length() > 25) {
            throw new BusinessRuleException("Las observaciones no pueden tener más de 25 caracteres");
        }

        // Validar que al crear un préstamo, el estado solo pueda ser ACTIVE o null (se establecerá como ACTIVE)
        if (request.getState() != null) {
            String stateUpper = request.getState().toUpperCase().trim();
            if (!stateUpper.equals("ACTIVE")) {
                throw new BusinessRuleException("No se puede crear un préstamo con estado '" + request.getState() + "'. Los préstamos solo se pueden crear como ACTIVE. Para marcar como devuelto, debe crear una devolución.");
            }
        }

        // Mapear DTO → Entity
        Loan loan = mapper.toEntity(request);
        loan.setStudent(student);
        loan.setBook(book);
        
        // Forzar que el estado sea ACTIVE al crear (por si viene null o vacío)
        loan.setState("ACTIVE");

        // Guardar el préstamo
        Loan savedLoan = repository.save(loan);

        // Actualizar inventario: restar 1 disponible, sumar 1 prestada
        if (inventory != null) {
            inventory.setAvailableCopies(inventory.getAvailableCopies() - 1);
            inventory.setBorrowedCopies(
                (inventory.getBorrowedCopies() != null ? inventory.getBorrowedCopies() : 0) + 1
            );
            inventory.setLastUpdated(java.time.LocalDateTime.now());
            inventoryRepository.save(inventory);
        }

        return mapper.toResponse(savedLoan);
    }

    @Override
    public LoanResponse update(Long id, LoanRequest request) {
        Loan loan = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado con ID: " + id));

        // Validar que el estado solo pueda ser ACTIVE o RETURNED
        if (request.getState() != null) {
            String stateUpper = request.getState().toUpperCase().trim();
            if (!stateUpper.equals("ACTIVE") && !stateUpper.equals("RETURNED")) {
                throw new BusinessRuleException("El estado solo puede ser: ACTIVE o RETURNED. Valor recibido: " + request.getState());
            }
            // Normalizar el estado a mayúsculas
            request.setState(stateUpper);
        }

        // Validar que no se intenten modificar campos inmutables
        if (request.getStudentId() != null && loan.getStudent() != null && !request.getStudentId().equals(loan.getStudent().getId())) {
            throw new BusinessRuleException("No se puede modificar el estudiante de un préstamo existente. El estudiante no se puede cambiar después de crear el préstamo.");
        }
        
        if (request.getBookId() != null && loan.getBook() != null && !request.getBookId().equals(loan.getBook().getId())) {
            throw new BusinessRuleException("No se puede modificar el libro de un préstamo existente. El libro no se puede cambiar después de crear el préstamo.");
        }
        
        if (request.getDateLoan() != null && loan.getDateLoan() != null && !request.getDateLoan().equals(loan.getDateLoan())) {
            throw new BusinessRuleException("No se puede modificar la fecha de préstamo de un préstamo existente. La fecha de préstamo no se puede cambiar después de crear el préstamo.");
        }
        
        if (request.getDueDate() != null && loan.getDueDate() != null && !request.getDueDate().equals(loan.getDueDate())) {
            throw new BusinessRuleException("No se puede modificar la fecha de vencimiento de un préstamo existente. La fecha de vencimiento no se puede cambiar después de crear el préstamo.");
        }

        // Validar que un préstamo con devolución asociada no pueda cambiar de estado
        if (loan.getReturnE() != null) {
            if (request.getState() != null && !request.getState().equalsIgnoreCase(loan.getState())) {
                throw new BusinessRuleException("Un préstamo con devolución asociada no puede cambiar de estado. Estado actual: " + loan.getState());
            }
            // Validar que un préstamo con devolución no pueda cambiar el monto
            if (Math.abs(request.getAmount() - loan.getAmount()) > 0.01) {
                throw new BusinessRuleException("No se puede modificar el monto de un préstamo con devolución asociada");
            }
        }

        // Validar que un préstamo RETURNED no pueda volver a ACTIVE
        if (loan.getState() != null && loan.getState().equalsIgnoreCase("RETURNED")) {
            if (request.getState() != null && request.getState().equalsIgnoreCase("ACTIVE")) {
                throw new BusinessRuleException("Un préstamo devuelto (RETURNED) no puede volver a activarse");
            }
            // Validar que un préstamo RETURNED no pueda cambiar el monto
            if (Math.abs(request.getAmount() - loan.getAmount()) > 0.01) {
                throw new BusinessRuleException("No se puede modificar el monto de un préstamo devuelto (RETURNED)");
            }
        }

        // Validar monto si se está actualizando (solo si no es RETURNED)
        if (!(loan.getState() != null && loan.getState().equalsIgnoreCase("RETURNED"))) {
            if (request.getAmount() < 0) {
                throw new BusinessRuleException("El monto no puede ser negativo");
            }
            if (request.getAmount() > 9999.99) {
                throw new BusinessRuleException("El monto máximo permitido es 9999.99 (4 dígitos enteros y 2 decimales)");
            }
            // Validar formato: máximo 4 enteros y 2 decimales
            // Usar BigDecimal para evitar problemas de precisión con double
            BigDecimal amount = BigDecimal.valueOf(request.getAmount());
            amount = amount.setScale(2, RoundingMode.HALF_UP);
            
            // Obtener la parte entera
            long integerPart = amount.longValue();
            
            // Validar que la parte entera tenga máximo 4 dígitos (permitir 1, 2, 3 o 4 dígitos)
            // Un número de 4 dígitos puede ser desde 1000 hasta 9999
            if (integerPart > 9999) {
                throw new BusinessRuleException("El monto no puede tener más de 4 dígitos enteros");
            }
        }

        // Validar que las observaciones no excedan 25 caracteres
        if (request.getObservations() != null && request.getObservations().length() > 25) {
            throw new BusinessRuleException("Las observaciones no pueden tener más de 25 caracteres");
        }

        // En edición, solo se pueden modificar el estado, observaciones y monto
        // No se permiten cambios en: estudiante, libro, fechas
        mapper.updateEntityFromRequest(request, loan);

        return mapper.toResponse(repository.save(loan));
    }

    @Override
    public List<LoanResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public LoanResponse findById(Long id) {
        Loan loan = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado con ID: " + id));
        return mapper.toResponse(loan);
    }

    @Override
    public void delete(Long id) {
        Loan loan = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado con ID: " + id));

        // Validar que no se pueda eliminar un préstamo en estado ACTIVE
        // Solo se pueden eliminar préstamos que estén en estado RETURNED (con devolución asociada)
        if (loan.getState() != null && loan.getState().equalsIgnoreCase("ACTIVE")) {
            throw new BusinessRuleException("No se puede eliminar un préstamo activo (ACTIVE). El préstamo debe ser devuelto primero.");
        }

        // Si el préstamo está en estado RETURNED, se puede eliminar
        // El inventario ya está correcto porque la devolución lo actualizó al crearse
        // La devolución asociada se eliminará automáticamente en cascada debido a CascadeType.ALL

        // Eliminar el préstamo (la devolución asociada se eliminará en cascada si existe)
        repository.delete(loan);
    }
}

