package com.app.emsx.services;

import com.app.emsx.dtos.student.StudentRequest;
import com.app.emsx.dtos.student.StudentResponse;

import java.util.List;

public interface StudentService {
    StudentResponse create(StudentRequest request);
    StudentResponse update(Long id, StudentRequest request);
    void delete(Long id);
    StudentResponse findById(Long id);
    List<StudentResponse> findAll();
}


