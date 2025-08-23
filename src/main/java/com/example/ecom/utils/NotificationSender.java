package com.example.ecom.utils;

import com.example.ecom.adapters.EmailAdapter;
import com.example.ecom.models.Notification;
import com.example.ecom.models.Product;
import com.example.ecom.models.User;
import com.example.ecom.models.enums.NotificationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationSender {
    private final EmailAdapter emailAdapter;

    @Autowired
    public NotificationSender(EmailAdapter emailAdapter) {
        this.emailAdapter = emailAdapter;
    }

    public void sendNotificationViaEmail(List<Notification> notifications) {
        for (Notification notification : notifications) {
            User user = notification.getUser();
            Product product = notification.getProduct();
            String emailAddress = user.getEmail();
            String subject = product.getName() + " back in stock!";
            String body = "Dear " + user.getName() + ", " + product.getName() + " is now back in stock. Grab it ASAP!";
            emailAdapter.sendEmail(emailAddress, subject, body);
            notification.setStatus(NotificationStatus.SENT);

        }
    }
}
