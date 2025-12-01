package com.app.emsx.repositories;

import com.app.emsx.entities.Return;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReturnRepository extends JpaRepository<Return, Long> {
    
    @Query("SELECT DISTINCT r FROM Return r LEFT JOIN FETCH r.loan")
    List<Return> findAllWithLoan();
}


