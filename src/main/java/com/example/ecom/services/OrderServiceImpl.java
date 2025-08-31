package com.example.ecom.services;

import com.example.ecom.exceptions.*;
import com.example.ecom.models.*;
import com.example.ecom.models.enums.OrderStatus;
import com.example.ecom.repositories.*;
import com.example.ecom.services.service_interfaces.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class OrderServiceImpl implements OrderService {
    private UserRepository userRepository;
    private AddressRepository addressRepository;
    private ProductRepository productRepository;
    private InventoryRepository inventoryRepository;
    private HighDemandProductRepository highDemandProductRepository;
    private OrderRepository orderRepository;
    private OrderDetailRepository orderDetailRepository;
//    private ReentrantLock lock = new ReentrantLock();

    @Autowired
    public OrderServiceImpl(UserRepository userRepository, AddressRepository addressRepository, ProductRepository productRepository, InventoryRepository inventoryRepository, HighDemandProductRepository highDemandProductRepository, OrderRepository orderRepository, OrderDetailRepository orderDetailRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.highDemandProductRepository = highDemandProductRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    @Override
    public Order placeOrder(int userId, int addressId, List<Pair<Integer, Integer>> orderDetails) throws UserNotFoundException, InvalidAddressException, OutOfStockException, InvalidProductException, HighDemandProductException {
        User user = null;
        Optional<User> optionalUser = userRepository.findById(userId);
        throwIfEmpty(optionalUser, () -> new UserNotFoundException("User with id" + userId +" is invalid"));
        user = optionalUser.get();

        Address address = null;
        Optional<Address> optionalAddress = addressRepository.findById(addressId);
        throwIfEmpty(optionalAddress, () -> new InvalidAddressException("Address with id: " + addressId + "is invalid."));
        address = optionalAddress.get();
        // if(!user.getAddresses().contains(address)) throw new InvalidAddressException("Address with id: " + addressId + "does not belong to user.");

        List<OrderDetail> orderDetailList = new ArrayList<>();

        for(Pair<Integer, Integer> productQuantPair : orderDetails) {
            Integer product_id = productQuantPair.getFirst();
            Integer quantityOrdered = productQuantPair.getSecond();

            Product product = null;
            Optional<Product> optionalProduct = productRepository.findById(product_id);
            throwIfEmpty(optionalProduct, () -> new InvalidProductException("Product with id: " + " is invalid"));
            product = optionalProduct.get();

            HighDemandProduct highDemandProduct = null;
            Optional<HighDemandProduct> optionalHighDemanProduct = highDemandProductRepository.findByProduct(product);
            if(optionalHighDemanProduct.isPresent()) {
                highDemandProduct = optionalHighDemanProduct.get();
                if(quantityOrdered > highDemandProduct.getMaxQuantity()) throw new HighDemandProductException("High demand product ordered quantity is greater then max allowed quantity.");
            }

            Inventory inventory = null;
            Optional<Inventory> optionalInventory = inventoryRepository.findByProduct(product);
            throwIfEmpty(optionalInventory, () -> new OutOfStockException("Product with id: " + product_id + "does not have inventory"));
            inventory = optionalInventory.get();
            if(inventory.getQuantity()<quantityOrdered) throw new OutOfStockException("The product does not have enough quantity to fulfill the order");

            inventory.setQuantity(inventory.getQuantity() - quantityOrdered);
            inventoryRepository.save(inventory);

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProduct(product);
            orderDetail.setQuantity(quantityOrdered);
            orderDetailList.add(orderDetail);
        }

        Order order = new Order();
        order.setUser(user);
        order.setDeliveryAddress(address);
        order.setOrderDetails(orderDetailList);
        order.setOrderStatus(OrderStatus.PLACED);
        order = orderRepository.save(order);
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrder(order);
            orderDetailRepository.save(orderDetail);
        }
        return order;
    }

    @Override
    @Transactional
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

//        lock.lock();
        List<OrderDetail> orderDetails = orderToCancel.getOrderDetails();
        for (OrderDetail orderDetail : orderDetails) {
            Inventory inventory = inventoryRepository.findByProduct(orderDetail.getProduct()).orElse(new Inventory());
            inventory.setProduct(orderDetail.getProduct());
            inventory.setQuantity(inventory.getQuantity() + orderDetail.getQuantity());
            inventoryRepository.save(inventory);
        }
        orderToCancel.setOrderStatus(OrderStatus.CANCELLED);
        orderToCancel = orderRepository.save(orderToCancel);
//        lock.unlock();
        return orderToCancel;
    }

    private <T, E extends Exception> void throwIfEmpty(Optional<T> optional, Supplier<E> exceptionSupplier) throws E {
        if (optional.isEmpty()) {
            throw exceptionSupplier.get();
        }
    }
}
