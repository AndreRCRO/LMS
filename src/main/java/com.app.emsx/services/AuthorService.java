package com.app.emsx.services;

import com.app.emsx.dtos.author.AuthorRequest;
import com.app.emsx.dtos.author.AuthorResponse;

import java.util.List;

public interface AuthorService {
    AuthorResponse create(AuthorRequest request);
    AuthorResponse update(Long id, AuthorRequest request);
    void delete(Long id);
    AuthorResponse findById(Long id);
    List<AuthorResponse> findAll();
}


