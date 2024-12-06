package com.application.bankapplication.httpservice;

import androidx.annotation.NonNull;

import com.application.bankapplication.models.Transaction;
import com.application.bankapplication.models.User;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HTTPService implements HTTPServiceInterface {

    // Base URL for transaction
    private static final String base_url = "https://1b29-106-208-145-171.ngrok-free.app";

    // Callback to open connection
    private HttpURLConnection createConnection(
            @NonNull  String connectionUrl,
            @NonNull  String method,
            String requestBody)  {
        try {
            // URL resource creator
            URL url = new URL(connectionUrl);

            // Open the connection
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            // Configure parameters
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setRequestProperty("Content-Type","application/json");
            if(method.equals("POST")){
                httpURLConnection.setDoOutput(true);
                // Write the body to the byte stream
                try(OutputStream os = httpURLConnection.getOutputStream()){
                    byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            // Return the connection
            return httpURLConnection;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Decodes the response to an JSONObject
    private JSONObject decodeConnection(HttpURLConnection httpURLConnection) {
        try {
            // Read the response
            int responseCode = httpURLConnection.getResponseCode();

            // Successful response "OK"
            if(responseCode == 200 || responseCode == 201) {
                // Get the output
                try(BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(httpURLConnection.getInputStream())
                )){
                    // Response Builder
                    StringBuilder responseBuilder = new StringBuilder();

                    // Parse line by line
                    String line;
                    while ((line = bufferedReader.readLine()) != null){
                        responseBuilder.append(line);
                    }

                    // Parse it to a JSONObject
                    return new JSONObject(responseBuilder.toString());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public JSONObject createUser(User user) {

        final String creationURL = base_url + "/user/create";

        String requestBody = user.toString();

        // Open connection
        HttpURLConnection httpURLConnection = createConnection(creationURL, "POST", requestBody);

        // Decode connection
        return decodeConnection(httpURLConnection);
    }

    @Override
    public JSONObject authenticateUser(String email, String password) {

        final String authenticationURL = base_url + "/user/authenticate";

        String requestBody = "{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}";

        // Open connection
        HttpURLConnection httpURLConnection = createConnection(authenticationURL, "POST", requestBody);

        // Decode connection
        return decodeConnection(httpURLConnection);
    }

    @Override
    public JSONObject getUserDetails(String id) {

        final String userDetailsURL = base_url + "/user/details?id=" + id;

        // Open connection
        HttpURLConnection httpURLConnection = createConnection(userDetailsURL, "GET", null);

        // Decode connection
        return decodeConnection(httpURLConnection);
    }

    @Override
    public JSONObject getAllTransactions(String id) {

        final String transactionDetailsURL = base_url + "/transaction/all?id=" + id;

        // Open connection
        HttpURLConnection httpURLConnection = createConnection(transactionDetailsURL, "GET", null);

        // Decode connection
        return decodeConnection(httpURLConnection);
    }

    @Override
    public JSONObject sendMoney(Transaction transaction) {

        final String sendMoneyURL = base_url + "/transaction/add_new";

        String requestBody = transaction.toString();

        // Open connection
        HttpURLConnection httpURLConnection = createConnection(sendMoneyURL, "POST", requestBody);

        // Decode connection
        return decodeConnection(httpURLConnection);
    }
}
