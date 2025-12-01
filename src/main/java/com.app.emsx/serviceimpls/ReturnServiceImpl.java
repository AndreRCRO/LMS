package com.app.emsx.serviceimpls;

import com.app.emsx.dtos.return_.ReturnRequest;
import com.app.emsx.dtos.return_.ReturnResponse;
import com.app.emsx.entities.Loan;
import com.app.emsx.entities.Return;
import com.app.emsx.exceptions.BusinessRuleException;
import com.app.emsx.exceptions.ResourceNotFoundException;
import com.app.emsx.mappers.ReturnMapper;
import com.app.emsx.entities.Inventory;
import com.app.emsx.repositories.InventoryRepository;
import com.app.emsx.repositories.LoanRepository;
import com.app.emsx.repositories.ReturnRepository;
import com.app.emsx.services.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 🔄 ReturnServiceImpl
 * -----------------------------------------------------
 * Servicio para gestión de devoluciones.
 * ✅ Aplica reglas de negocio, validaciones y conversiones DTO ↔ Entity.
 */
@Service
@RequiredArgsConstructor
public class ReturnServiceImpl implements ReturnService {

    private final ReturnRepository repository;
    private final LoanRepository loanRepository;
    private final InventoryRepository inventoryRepository;
    private final ReturnMapper mapper;

    @Override
    public ReturnResponse create(ReturnRequest request) {
        // Verificar existencia del préstamo
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado con ID: " + request.getLoanId()));

        // Verificar que el préstamo no tenga devolución ya asignada
        if (loan.getReturnE() != null) {
            throw new BusinessRuleException("El préstamo ya tiene una devolución asociada");
        }

        // Mapear DTO → Entity
        Return returnEntity = mapper.toEntity(request);
        returnEntity.setLoan(loan);

        // Guardar y retornar
        ReturnResponse response = mapper.toResponse(repository.save(returnEntity));
        
        // Actualizar el préstamo con la devolución y cambiar estado a RETURNED
        loan.setReturnE(returnEntity);
        loan.setState("RETURNED");
        loanRepository.save(loan);

        // Actualizar inventario: sumar 1 disponible, restar 1 prestada
        Inventory inventory = loan.getBook().getInventory();
        if (inventory != null) {
            inventory.setAvailableCopies(
                (inventory.getAvailableCopies() != null ? inventory.getAvailableCopies() : 0) + 1
            );
            inventory.setBorrowedCopies(
                Math.max(0, (inventory.getBorrowedCopies() != null ? inventory.getBorrowedCopies() : 0) - 1)
            );
            inventory.setLastUpdated(java.time.LocalDateTime.now());
            inventoryRepository.save(inventory);
        }

        return response;
    }

    @Override
    public ReturnResponse update(Long id, ReturnRequest request) {
        Return returnEntity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Devolución no encontrada con ID: " + id));

        // Actualizar fecha de devolución si se proporciona
        if (request.getDateReturn() != null) {
            returnEntity.setDateReturn(request.getDateReturn());
        }
        
        // Actualizar observaciones (permite null y string vacío)
        if (request.getObservations() != null) {
            returnEntity.setObservations(request.getObservations());
        }
        
        // Actualizar penalty
        returnEntity.setPenalty(request.getPenalty());
        
        // El loanId no se actualiza en una actualización (ya está asociado)
        // No hacer nada con request.getLoanId()

        return mapper.toResponse(repository.save(returnEntity));
    }

    @Override
    public List<ReturnResponse> findAll() {
        // Obtener todas las devoluciones de la base de datos
        List<Return> returns = repository.findAll();
        
        // Log para depuración (puedes ver esto en la consola del backend)
        System.out.println("🔍 Total devoluciones encontradas: " + returns.size());
        
        // Mapear a DTOs
        List<ReturnResponse> responses = returns.stream()
                .map(mapper::toResponse)
                .filter(response -> response != null)
                .toList();
        
        System.out.println("🔍 Total respuestas mapeadas: " + responses.size());
        
        return responses;
    }

    @Override
    public ReturnResponse findById(Long id) {
        Return returnEntity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Devolución no encontrada con ID: " + id));
        return mapper.toResponse(returnEntity);
    }

    @Override
    public void delete(Long id) {
        Return returnEntity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Devolución no encontrada con ID: " + id));
        repository.delete(returnEntity);
    }
}


