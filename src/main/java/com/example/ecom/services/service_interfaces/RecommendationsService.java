package com.example.ecom.services.service_interfaces;

import com.example.ecom.exceptions.ProductNotFoundException;
import com.example.ecom.models.Product;

import java.util.List;

public interface RecommendationsService {

    public List<Product> getRecommendations(int productId) throws ProductNotFoundException;
}

