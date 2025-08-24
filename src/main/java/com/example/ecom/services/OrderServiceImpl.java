package com.example.ecom.services;

import com.example.ecom.exceptions.OrderCannotBeCancelledException;
import com.example.ecom.exceptions.OrderDoesNotBelongToUserException;
import com.example.ecom.exceptions.OrderNotFoundException;
import com.example.ecom.exceptions.UserNotFoundException;
import com.example.ecom.models.Inventory;
import com.example.ecom.models.Order;
import com.example.ecom.models.OrderDetail;
import com.example.ecom.models.User;
import com.example.ecom.models.enums.OrderStatus;
import com.example.ecom.repositories.*;
import com.example.ecom.services.service_interfaces.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class OrderServiceImpl implements OrderService {
    private InventoryRepository inventoryRepository;
    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private ReentrantLock lock = new ReentrantLock();

    @Autowired
    public OrderServiceImpl(InventoryRepository inventoryRepository, OrderRepository orderRepository,
                            UserRepository userRepository) {
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Order cancelOrder(int orderId, int userId) throws UserNotFoundException, OrderNotFoundException,
            OrderDoesNotBelongToUserException, OrderCannotBeCancelledException {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) throw new UserNotFoundException("User not found");
        User user = optionalUser.get();

        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) throw new OrderNotFoundException("Order not found");
        Order orderToCancel = order.get();

        if (orderToCancel.getUser().getId() != user.getId())
            throw new OrderDoesNotBelongToUserException("This order does not belong to this user");

        if (List.of(OrderStatus.SHIPPED, OrderStatus.DELIVERED, OrderStatus.CANCELLED)
                .contains(orderToCancel.getOrderStatus()))
            throw new OrderCannotBeCancelledException("Order not be cancelled as the Order status is : " + orderToCancel.getOrderStatus());

        lock.lock();
        List<OrderDetail> orderDetails = orderToCancel.getOrderDetails();
        for (OrderDetail orderDetail : orderDetails) {
            Inventory inventory = inventoryRepository.findByProduct(orderDetail.getProduct()).orElse(new Inventory());
            inventory.setProduct(orderDetail.getProduct());
            inventory.setQuantity(inventory.getQuantity() + orderDetail.getQuantity());
            inventoryRepository.save(inventory);
        }
        orderToCancel.setOrderStatus(OrderStatus.CANCELLED);
        orderToCancel = orderRepository.save(orderToCancel);
        lock.unlock();
        return orderToCancel;
    }
}
