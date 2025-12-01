package com.app.emsx.services;

import com.app.emsx.dtos.book.BookRequest;
import com.app.emsx.dtos.book.BookResponse;

import java.util.List;

public interface BookService {
    BookResponse create(BookRequest request);
    BookResponse update(Long id, BookRequest request);
    void delete(Long id);
    BookResponse findById(Long id);
    List<BookResponse> findAll();
}


