package com.example.ecom.controllers;

import com.example.ecom.dtos.DeliveryEstimateRequestDto;
import com.example.ecom.dtos.DeliveryEstimateResponseDto;
import com.example.ecom.dtos.ResponseStatus;
import com.example.ecom.exceptions.AddressNotFoundException;
import com.example.ecom.exceptions.ProductNotFoundException;
import com.example.ecom.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class ProductController {
    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    public DeliveryEstimateResponseDto estimateDeliveryTime(DeliveryEstimateRequestDto requestDto){
        DeliveryEstimateResponseDto responseDto = new DeliveryEstimateResponseDto();
        int productId = requestDto.getProductId();
        int addressId = requestDto.getAddressId();
        try {
            Date expectedDeliveryDate = productService.estimateDeliveryDate(productId, addressId);
            responseDto.setExpectedDeliveryDate(expectedDeliveryDate);
            responseDto.setResponseStatus(ResponseStatus.SUCCESS);
        } catch (ProductNotFoundException | AddressNotFoundException e) {
            responseDto.setResponseStatus(ResponseStatus.FAILURE);
            responseDto.setExpectedDeliveryDate(null);
        }
        return responseDto;
    }
}
