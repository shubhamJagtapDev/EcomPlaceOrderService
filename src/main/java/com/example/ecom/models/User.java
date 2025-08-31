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
    @OneToMany(mappedBy = "user")
    List<Address> addresses;
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Order> orders;
}
