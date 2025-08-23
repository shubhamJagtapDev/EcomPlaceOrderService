package com.example.ecom.models;

import com.example.ecom.models.enums.NotificationStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Notification extends BaseModel {
    @ManyToOne
    private Product product;
    @ManyToOne
    private User user;
    @Enumerated(EnumType.ORDINAL)
    private NotificationStatus status;
}
