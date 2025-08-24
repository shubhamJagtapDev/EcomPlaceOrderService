package com.example.ecom.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "\"user\"")
public class User extends BaseModel{
    private String name;
    private String email;
    @Enumerated(EnumType.ORDINAL)
    private UserType userType;
    @OneToMany
    List<Address> addresses;
    @OneToMany
    private List<Order> orders;
}
