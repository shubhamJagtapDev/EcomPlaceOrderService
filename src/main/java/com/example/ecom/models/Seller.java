package com.example.ecom.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Seller extends BaseModel{
    private String name;
    private String email;
    @OneToOne
    @JoinColumn(name = "address_id",  referencedColumnName = "id")
    private Address address;
}
