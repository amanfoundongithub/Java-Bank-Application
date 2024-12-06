package com.application.bankapplication.httpservice;

import com.application.bankapplication.models.Transaction;
import com.application.bankapplication.models.User;

import org.json.JSONObject;

public interface HTTPServiceInterface {

    // Fetches User Details
    JSONObject getUserDetails(final String id);

    // Fetches all the transactions
    JSONObject getAllTransactions(final String id);

    // Authenticate the user details
    JSONObject authenticateUser(String email, String password);

    // Creates a user to the backend
    JSONObject createUser(User user);

    // Send money
    JSONObject sendMoney(Transaction transaction);

}
