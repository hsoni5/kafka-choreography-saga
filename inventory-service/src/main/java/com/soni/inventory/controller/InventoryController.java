package com.soni.inventory.controller;

import com.soni.inventory.dto.InventoryDto;
import com.soni.inventory.service.InventoryService;
import com.soni.inventory.utils.InventoryConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventories")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @PostMapping
    public String addItems(@RequestBody InventoryDto inventoryDto) {
         inventoryService.addItem(inventoryDto);
         return InventoryConstant.SUCCESS;
    }
}
