package com.application.bankapplication.models;

public class TransactionDetails extends Transaction {

    // Server responses
    public boolean status;
    public String message;

    // Date
    public String transactionDate;


    public TransactionDetails(String senderId, String receiverId, String description, double amount) {
        super(senderId, receiverId, description, amount);

    }

    public void addServerDetails(boolean status, String message, String transactionDate) {
        this.message = message;
        this.status = status;
        this.transactionDate = transactionDate;
    }

}
