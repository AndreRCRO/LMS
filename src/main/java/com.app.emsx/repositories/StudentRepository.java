package com.app.emsx.repositories;

import com.app.emsx.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByEmail(String email);
    boolean existsByCodigo(String codigo);
    boolean existsByPhone(String phone);
}

