package com.app.emsx.services;

import com.app.emsx.dtos.loan.LoanRequest;
import com.app.emsx.dtos.loan.LoanResponse;

import java.util.List;

public interface LoanService {
    LoanResponse create(LoanRequest request);
    LoanResponse update(Long id, LoanRequest request);
    void delete(Long id);
    LoanResponse findById(Long id);
    List<LoanResponse> findAll();
}


