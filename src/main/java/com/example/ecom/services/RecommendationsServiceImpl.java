package com.example.ecom.services;

import com.example.ecom.exceptions.ProductNotFoundException;
import com.example.ecom.models.Product;
import com.example.ecom.models.ProductGroup;
import com.example.ecom.repositories.ProductGroupRepository;
import com.example.ecom.repositories.ProductRepository;
import com.example.ecom.services.service_interfaces.RecommendationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecommendationsServiceImpl implements RecommendationsService {
    private ProductGroupRepository productGroupRepository;
    private ProductRepository productRepository;

    @Autowired
    public RecommendationsServiceImpl(ProductGroupRepository productGroupRepository, ProductRepository productRepository) {
        this.productGroupRepository = productGroupRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getRecommendations(int productId) throws ProductNotFoundException {
       Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty())
            throw new ProductNotFoundException("Product not found");
        Product product = optionalProduct.get();
        List<ProductGroup> productGroups = productGroupRepository.findByProductsContaining(product);
        Set<Product> recommendedProducts = new HashSet<>();
        for(ProductGroup productGroup : productGroups) {
            recommendedProducts.addAll(productGroup.getProducts());
        }
        recommendedProducts.remove(product);
        return new ArrayList<>(recommendedProducts);
    }
}
