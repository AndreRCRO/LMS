package com.app.emsx.repositories;

import com.app.emsx.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // Buscar libro por título y autorId (para validar duplicados)
    @Query("SELECT b FROM Book b WHERE b.title = :title AND b.author.id = :authorId")
    Optional<Book> findByTitleAndAuthorId(@Param("title") String title, @Param("authorId") Long authorId);
}


