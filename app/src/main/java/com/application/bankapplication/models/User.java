package com.application.bankapplication.models;

import androidx.annotation.NonNull;

public class User {
    public String id = "";
    public String username;

    public String email;

    public String password;

    public String name;

    public String phone;

    public double amount;

    public boolean premium;

    // Parameterized constructor
    public User(String username, String email, String password, String name, String phone, double amount, boolean premium) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.amount = amount;
        this.premium = premium;
    }

    public User() {

    }

    @NonNull
    @Override
    public String toString() {
        return "{"
//                + "\"id\": \"" + id + "\", "
                + "\"username\": \"" + username + "\", "
                + "\"email\": \"" + email + "\", "
                + "\"password\": \"" + password + "\", "
                + "\"name\": \"" + name + "\", "
                + "\"phone\": " + phone + ", "
                + "\"balance\": " + amount + ", "
                + "\"premium\": " + premium
                + "}";
    }
}
