package com.app.emsx.services;

import com.app.emsx.dtos.inventory.InventoryRequest;
import com.app.emsx.dtos.inventory.InventoryResponse;

import java.util.List;

public interface InventoryService {
    InventoryResponse create(InventoryRequest request);
    InventoryResponse update(Long id, InventoryRequest request);
    void delete(Long id);
    InventoryResponse findById(Long id);
    List<InventoryResponse> findAll();
}


