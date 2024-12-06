package com.application.bankapplication.models;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {

    public String senderId;
    public String receiverId;

    public String transactionDate;

    public String description;

    public double amount;

    public Transaction(String senderId, String receiverId, String description, double amount){
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.description = description;
        this.amount = amount;
        this.transactionDate = getCurrentDateTime();
    }

    // Helper method to get the current date and time as a formatted string
    private String getCurrentDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

    @NonNull
    @Override
    public String toString() {
        return "{"
                + "\"senderId\":\"" + senderId + "\","
                + "\"receiverId\":\"" + receiverId + "\","
                + "\"transactionDate\":\"" + transactionDate + "\","
                + "\"description\":\"" + description + "\","
                + "\"amount\":" + amount
                + "}";
    }
}
