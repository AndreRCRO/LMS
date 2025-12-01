package com.app.emsx.services;

import com.app.emsx.dtos.return_.ReturnRequest;
import com.app.emsx.dtos.return_.ReturnResponse;

import java.util.List;

public interface ReturnService {
    ReturnResponse create(ReturnRequest request);
    ReturnResponse update(Long id, ReturnRequest request);
    void delete(Long id);
    ReturnResponse findById(Long id);
    List<ReturnResponse> findAll();
}


