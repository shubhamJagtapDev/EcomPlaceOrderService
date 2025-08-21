package com.example.ecom.controllers;

import com.example.ecom.dtos.*;
import com.example.ecom.models.Inventory;
import com.example.ecom.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InventoryController {


    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public CreateOrUpdateResponseDto createOrUpdateInventory(CreateOrUpdateRequestDto requestDto){
        CreateOrUpdateResponseDto responseDto = new CreateOrUpdateResponseDto();
        int userId = requestDto.getUserId();
        int productId = requestDto.getProductId();
        int quantity = requestDto.getQuantity();

        try {
            Inventory inventory = inventoryService.createOrUpdateInventory(userId, productId, quantity);
            responseDto.setInventory(inventory);
            responseDto.setResponseStatus(ResponseStatus.SUCCESS);
        } catch (Exception e) {
            responseDto.setInventory(null);
            responseDto.setResponseStatus(ResponseStatus.FAILURE);
        }

        return responseDto;
    }

    public DeleteInventoryResponseDto deleteInventory(DeleteInventoryRequestDto requestDto){
        DeleteInventoryResponseDto responseDto = new DeleteInventoryResponseDto();
        int userId = requestDto.getUserId();
        int productId = requestDto.getProductId();
        try {
            inventoryService.deleteInventory(userId, productId);
            responseDto.setResponseStatus(ResponseStatus.SUCCESS);
        } catch (Exception e) {
            responseDto.setResponseStatus(ResponseStatus.FAILURE);
        }
        return responseDto;
    }


}
