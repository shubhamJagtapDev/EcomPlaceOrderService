package com.example.ecom.adapters;

public class SendEmailAdapterImpl implements SendEmailAdapter{
    @Override
    public void sendEmail(String emailAddress, String message) {
        System.out.println("sending mail to : " + emailAddress + " with message : " + message);
    }
}
