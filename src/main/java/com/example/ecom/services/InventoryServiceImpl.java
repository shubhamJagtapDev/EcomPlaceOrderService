package com.example.ecom.services;

import com.example.ecom.exceptions.ProductNotFoundException;
import com.example.ecom.exceptions.UnAuthorizedAccessException;
import com.example.ecom.exceptions.UserNotFoundException;
import com.example.ecom.models.Inventory;
import com.example.ecom.models.Product;
import com.example.ecom.models.User;
import com.example.ecom.models.UserType;
import com.example.ecom.repositories.InventoryRepository;
import com.example.ecom.repositories.ProductRepository;
import com.example.ecom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService{
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Inventory createOrUpdateInventory(int userId, int productId, int quantity) throws ProductNotFoundException, UserNotFoundException, UnAuthorizedAccessException {
        userValidation(userId);
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) throw new ProductNotFoundException("Product doesn't exist :  product_id - " + productId);
        Product product = productOptional.get();
        Inventory inventory = inventoryRepository.findByProduct_Id(productId).orElse(new Inventory());
        inventory.setProduct(product);
        int updatedQuantity = inventory.getQuantity() + quantity;
        inventory.setQuantity(updatedQuantity);

        return inventoryRepository.save(inventory);
    }

    @Override
    public void deleteInventory(int userId, int productId) throws UserNotFoundException, UnAuthorizedAccessException {
        userValidation(userId);
        inventoryRepository.deleteByProduct_Id(productId);
    }

    private void userValidation(int userId) throws UserNotFoundException, UnAuthorizedAccessException {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) throw new UserNotFoundException("User was found : userId - " + userId);
        User user = userOptional.get();
        if(!user.getUserType().equals(UserType.ADMIN)) {
            throw new UnAuthorizedAccessException("User not authorized to create or update inventory for any product : userId - " + userId);
        }
    }
}
