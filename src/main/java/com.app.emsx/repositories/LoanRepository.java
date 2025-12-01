package com.app.emsx.repositories;

import com.app.emsx.entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    /**
     * Busca préstamos activos de un estudiante para un libro específico
     * Un préstamo se considera activo si su estado es ACTIVE o OVERDUE
     */
    @Query("SELECT l FROM Loan l WHERE l.student.id = :studentId AND l.book.id = :bookId AND l.state IN :states")
    List<Loan> findActiveLoansByStudentAndBook(
            @Param("studentId") Long studentId, 
            @Param("bookId") Long bookId, 
            @Param("states") List<String> states
    );
    
    /**
     * Busca préstamos activos de un libro (sin importar el estudiante)
     * Un préstamo se considera activo si su estado es ACTIVE o OVERDUE
     */
    @Query("SELECT l FROM Loan l WHERE l.book.id = :bookId AND l.state IN :states")
    List<Loan> findActiveLoansByBook(
            @Param("bookId") Long bookId, 
            @Param("states") List<String> states
    );
}

