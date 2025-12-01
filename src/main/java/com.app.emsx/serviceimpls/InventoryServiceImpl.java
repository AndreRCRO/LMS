package com.app.emsx.serviceimpls;

import com.app.emsx.dtos.inventory.InventoryRequest;
import com.app.emsx.dtos.inventory.InventoryResponse;
import com.app.emsx.entities.Book;
import com.app.emsx.entities.Inventory;
import com.app.emsx.exceptions.BusinessRuleException;
import com.app.emsx.exceptions.ResourceNotFoundException;
import com.app.emsx.mappers.InventoryMapper;
import com.app.emsx.repositories.BookRepository;
import com.app.emsx.repositories.InventoryRepository;
import com.app.emsx.repositories.LoanRepository;
import com.app.emsx.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 📦 InventoryServiceImpl
 * -----------------------------------------------------
 * Servicio para gestión de inventario.
 * ✅ Aplica reglas de negocio, validaciones y conversiones DTO ↔ Entity.
 */
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository repository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final InventoryMapper mapper;

    @Override
    public InventoryResponse create(InventoryRequest request) {
        // Verificar existencia del libro
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + request.getBookId()));

        // Verificar que el libro no tenga inventario ya asignado
        if (book.getInventory() != null) {
            throw new ResourceNotFoundException("El libro ya tiene un inventario asignado");
        }

        // Validar que totalCopies sea obligatorio
        if (request.getTotalCopies() == null) {
            throw new BusinessRuleException("El total de copias es obligatorio");
        }

        // Validar que availableCopies sea obligatorio
        if (request.getAvailableCopies() == null) {
            throw new BusinessRuleException("Las copias disponibles son obligatorias");
        }

        // Validar que totalCopies no sea negativo
        if (request.getTotalCopies() < 0) {
            throw new BusinessRuleException("El total de copias no puede ser negativo");
        }

        // Validar que totalCopies tenga máximo 3 dígitos (máximo 999)
        if (request.getTotalCopies() != null && request.getTotalCopies() > 999) {
            throw new BusinessRuleException("El total de copias no puede tener más de 3 dígitos (máximo 999)");
        }

        // Validar que availableCopies no sea negativo
        if (request.getAvailableCopies() != null && request.getAvailableCopies() < 0) {
            throw new BusinessRuleException("Las copias disponibles no pueden ser negativas");
        }

        // Validar que las observaciones no excedan 25 caracteres
        if (request.getObservations() != null && request.getObservations().length() > 25) {
            throw new BusinessRuleException("Las observaciones no pueden tener más de 25 caracteres");
        }

        // Validar que al crear un inventario, borrowedCopies debe ser 0 o null
        // No se puede crear un inventario con libros ya prestados
        if (request.getBorrowedCopies() != null && request.getBorrowedCopies() > 0) {
            throw new BusinessRuleException("No se puede crear un inventario con libros prestados. Al crear un inventario, las copias prestadas deben ser 0.");
        }

        // Validar que borrowedCopies no sea negativo
        if (request.getBorrowedCopies() != null && request.getBorrowedCopies() < 0) {
            throw new BusinessRuleException("Las copias prestadas no pueden ser negativas");
        }

        // Validar que availableCopies no sea mayor que totalCopies
        if (request.getTotalCopies() != null && request.getAvailableCopies() != null) {
            if (request.getAvailableCopies() > request.getTotalCopies()) {
                throw new BusinessRuleException("Las copias disponibles (" + request.getAvailableCopies() + ") no pueden ser mayores que el total de copias (" + request.getTotalCopies() + ")");
            }
        }

        // Mapear DTO → Entity
        Inventory inventory = mapper.toEntity(request);
        inventory.setBook(book);
        
        // Asegurar que borrowedCopies sea 0 al crear
        inventory.setBorrowedCopies(0);

        // Guardar y retornar
        return mapper.toResponse(repository.save(inventory));
    }

    @Override
    public InventoryResponse update(Long id, InventoryRequest request) {
        Inventory inventory = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado con ID: " + id));

        // Validar que no se intente cambiar el libro asociado
        if (request.getBookId() != null && inventory.getBook() != null && !request.getBookId().equals(inventory.getBook().getId())) {
            throw new BusinessRuleException("No se puede modificar el libro de un inventario existente. El libro no se puede cambiar después de crear el inventario.");
        }

        // Validar que totalCopies no sea negativo
        Integer totalCopiesToCheck = request.getTotalCopies() != null ? request.getTotalCopies() : inventory.getTotalCopies();
        if (totalCopiesToCheck != null && totalCopiesToCheck < 0) {
            throw new BusinessRuleException("El total de copias no puede ser negativo");
        }

        // Validar que totalCopies tenga máximo 3 dígitos (máximo 999)
        if (request.getTotalCopies() != null && request.getTotalCopies() > 999) {
            throw new BusinessRuleException("El total de copias no puede tener más de 3 dígitos (máximo 999)");
        }

        // Validar que availableCopies no sea negativo
        Integer availableCopiesToCheck = request.getAvailableCopies() != null ? request.getAvailableCopies() : inventory.getAvailableCopies();
        if (availableCopiesToCheck != null && availableCopiesToCheck < 0) {
            throw new BusinessRuleException("Las copias disponibles no pueden ser negativas");
        }

        // Validar que las observaciones no excedan 25 caracteres
        if (request.getObservations() != null && request.getObservations().length() > 25) {
            throw new BusinessRuleException("Las observaciones no pueden tener más de 25 caracteres");
        }

        // Validar que no se intente editar borrowedCopies manualmente
        // borrowedCopies se calcula automáticamente basado en los préstamos activos
        // Si se envía un valor, debe coincidir con el real o se rechaza
        Book book = inventory.getBook();
        int realBorrowedCopies = 0;
        if (book != null) {
            List<String> activeStates = Arrays.asList("ACTIVE", "OVERDUE");
            List<com.app.emsx.entities.Loan> activeLoans = loanRepository.findActiveLoansByBook(
                    book.getId(), 
                    activeStates
            );
            realBorrowedCopies = activeLoans.size();
        }
        
        // Si se intenta enviar un valor de borrowedCopies que no existe (mayor que préstamos reales)
        if (request.getBorrowedCopies() != null) {
            if (request.getBorrowedCopies() > realBorrowedCopies) {
                throw new BusinessRuleException("No se pueden tener " + request.getBorrowedCopies() + " copias prestadas cuando solo existen " + realBorrowedCopies + " préstamo(s) activo(s) del libro. Las copias prestadas se calculan automáticamente basado en los préstamos activos.");
            }
            if (request.getBorrowedCopies() < 0) {
                throw new BusinessRuleException("Las copias prestadas no pueden ser negativas");
            }
        }

        // Validar que availableCopies no sea mayor que totalCopies
        // Reutilizar las variables ya definidas arriba
        if (totalCopiesToCheck != null && availableCopiesToCheck != null) {
            if (availableCopiesToCheck > totalCopiesToCheck) {
                throw new BusinessRuleException("Las copias disponibles (" + availableCopiesToCheck + ") no pueden ser mayores que el total de copias (" + totalCopiesToCheck + ")");
            }
        }

        // Actualizar valores
        if (request.getTotalCopies() != null) {
            inventory.setTotalCopies(request.getTotalCopies());
        }
        if (request.getAvailableCopies() != null) {
            inventory.setAvailableCopies(request.getAvailableCopies());
        }
        
        // Calcular automáticamente borrowedCopies basado en préstamos activos reales
        // Esto siempre sobrescribe cualquier valor enviado en el request
        inventory.setBorrowedCopies(realBorrowedCopies);
        
        if (request.getObservations() != null) {
            inventory.setObservations(request.getObservations());
        }

        return mapper.toResponse(repository.save(inventory));
    }

    @Override
    public List<InventoryResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public InventoryResponse findById(Long id) {
        Inventory inventory = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado con ID: " + id));
        return mapper.toResponse(inventory);
    }

    @Override
    public void delete(Long id) {
        Inventory inventory = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado con ID: " + id));

        // Obtener el libro asociado
        Book book = inventory.getBook();
        if (book != null) {
            // Verificar si el libro tiene préstamos activos
            List<String> activeStates = Arrays.asList("ACTIVE", "OVERDUE");
            List<com.app.emsx.entities.Loan> activeLoans = loanRepository.findActiveLoansByBook(
                    book.getId(), 
                    activeStates
            );
            
            if (!activeLoans.isEmpty()) {
                throw new BusinessRuleException("No se puede eliminar el inventario. El libro tiene préstamos activos. Debe devolver todos los préstamos primero.");
            }
            
            // También verificar si hay copias prestadas (borrowedCopies > 0)
            // Esto cubre el caso de préstamos RETURNED que aún no han sido limpiados
            if (inventory.getBorrowedCopies() != null && inventory.getBorrowedCopies() > 0) {
                throw new BusinessRuleException("No se puede eliminar el inventario. El libro tiene " + inventory.getBorrowedCopies() + " copia(s) prestada(s). Debe devolver todos los préstamos primero.");
            }
        }

        repository.delete(inventory);
    }
}


