package com.example.ecom.services.service_interfaces;

import com.example.ecom.exceptions.*;
import com.example.ecom.models.Notification;

public interface NotificationService {
    public Notification registerUser(int userId, int productId) throws UserNotFoundException, ProductNotFoundException, ProductInStockException;

    public void deregisterUser(int userId, int notificationId) throws UserNotFoundException, NotificationNotFoundException, UnAuthorizedAccessException;
}
