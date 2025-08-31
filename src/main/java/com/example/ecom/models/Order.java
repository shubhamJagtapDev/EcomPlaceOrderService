package com.example.ecom.models;

import com.example.ecom.models.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Order extends BaseModel{
    @ManyToOne
    private User user;
    @ManyToOne
    private Address deliveryAddress;
    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;
    @Enumerated(EnumType.ORDINAL)
    private OrderStatus orderStatus;
}
