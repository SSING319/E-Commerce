package com.shop.inventory.service;

import com.shop.inventory.dto.InventoryResponse;
import com.shop.inventory.repo.InventoryRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class InventoryService {

    private final InventoryRepo inventoryRepo;

    @Transactional
    public List<InventoryResponse> isInStock(List<String> skuCode){
        return inventoryRepo.findBySkuCodeIn(skuCode).stream()
                .map(inventory ->
                    InventoryResponse.builder()
                            .skuCode(inventory.getSkuCode())
                            .isInStock(inventory.getQuantity()>0)
                            .build()
                ).toList();
    }
}
