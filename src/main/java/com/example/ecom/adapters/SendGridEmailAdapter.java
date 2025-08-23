package com.example.ecom.adapters;

import com.example.ecom.libraries.Sendgrid;
import org.springframework.stereotype.Component;

@Component
public class SendGridEmailAdapter implements EmailAdapter {
    private Sendgrid sendGrid;

    public SendGridEmailAdapter(Sendgrid sendGrid) {
        this.sendGrid = new Sendgrid();
    }

    @Override
    public void sendEmail(String email, String subject, String body) {
        sendGrid.sendEmailAsync(email, subject, body);
    }
}
