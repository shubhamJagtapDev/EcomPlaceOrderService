package com.example.ecom.services;

import com.example.ecom.exceptions.*;
import com.example.ecom.models.Inventory;
import com.example.ecom.models.Notification;
import com.example.ecom.models.Product;
import com.example.ecom.models.User;
import com.example.ecom.models.enums.NotificationStatus;
import com.example.ecom.repositories.InventoryRepository;
import com.example.ecom.repositories.NotificationRepository;
import com.example.ecom.repositories.ProductRepository;
import com.example.ecom.repositories.UserRepository;
import com.example.ecom.services.service_interfaces.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final NotificationRepository notificationRepository;
    private final InventoryRepository inventoryRepository;

    @Autowired
    public NotificationServiceImpl(UserRepository userRepository, ProductRepository productRepository, NotificationRepository notificationRepository, InventoryRepository inventoryRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.notificationRepository = notificationRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public Notification registerUser(int userId, int productId) throws UserNotFoundException, ProductNotFoundException, ProductInStockException {
        // User exist check
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) throw new UserNotFoundException("User not found : userId " + userId);
        // Product exist check
        Optional<Product> productOptional = productRepository.findById(productId);
        if(productOptional.isEmpty()) throw new ProductNotFoundException("Product doesn't exist : productId " + productId);
        // Product in stock or not check
        Optional<Inventory> productInventoryOptional = inventoryRepository.findByProduct(productOptional.get());
        if (productInventoryOptional.isPresent()) {
            Inventory producInventory = productInventoryOptional.get();
            if (producInventory.getQuantity() > 0) throw new ProductInStockException("Product already in stock : productId " + productId);
        }
        // Creating notification for userId for productId as it is present in inventory
        Notification notification = new Notification();
        notification.setProduct(productOptional.get());
        notification.setUser(userOptional.get());
        notification.setStatus(NotificationStatus.PENDING);

        return notificationRepository.save(notification);

    }

    @Override
    public void deregisterUser(int userId, int notificationId) throws UserNotFoundException, NotificationNotFoundException, UnAuthorizedAccessException {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) throw new UserNotFoundException("User not found : userId " + userId);
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (optionalNotification.isEmpty()) throw new NotificationNotFoundException("Notification not found");
        if(optionalNotification.get().getUser().getId() != (userOptional.get().getId())) {
            throw new UnAuthorizedAccessException("Notification " + notificationId + " does not belong to user " + userId);
        }
        notificationRepository.delete(optionalNotification.get());
    }
}
