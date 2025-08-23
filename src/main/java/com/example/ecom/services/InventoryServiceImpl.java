package com.example.ecom.services;

import com.example.ecom.exceptions.ProductNotFoundException;
import com.example.ecom.exceptions.UnAuthorizedAccessException;
import com.example.ecom.exceptions.UserNotFoundException;
import com.example.ecom.models.*;
import com.example.ecom.repositories.InventoryRepository;
import com.example.ecom.repositories.NotificationRepository;
import com.example.ecom.repositories.ProductRepository;
import com.example.ecom.repositories.UserRepository;
import com.example.ecom.services.service_interfaces.InventoryService;
import com.example.ecom.utils.NotificationSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationSender notificationSender;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository, ProductRepository productRepository, UserRepository userRepository, NotificationRepository notificationRepository, NotificationSender notificationSender) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.notificationSender = notificationSender;
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
        inventory = inventoryRepository.save(inventory);

        if(inventory.getQuantity()>0) {
            Optional<List<Notification>> optionalNotifications = notificationRepository.findNotificationsByProduct_Id(productId);
            optionalNotifications.ifPresent(notificationSender::sendNotificationViaEmail);
        }
        return inventory;
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
