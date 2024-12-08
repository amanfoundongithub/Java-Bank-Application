package com.application.bankapplication.login;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;


import com.application.bankapplication.R;
import com.application.bankapplication.databinding.FragmentLoginBinding;
import com.application.bankapplication.httpservice.HTTPService;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Login Page Fragment
 *
 * @author amanfoundongithub
 *
 */
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    // Executes asynchronous updates
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // HTTP Service handler
    private final HTTPService httpService = new HTTPService();

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view,
                              Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind the functionality of Login Button
        binding.loginButton.setOnClickListener(v -> logIn());

        // Bind the functionality of Sign Up Tab Opener
        binding.signupLink.setOnClickListener(v-> navigateToRegister());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Functions defined below

    public void navigateToRegister() {
        NavHostFragment.findNavController(LoginFragment.this)
                .navigate(R.id.action_loginFragment_to_signUpFragment);
    }

    public void logIn() {
        // Disable the Button
        disableButtons();

        // Fetch values
        String password = binding.password.getText().toString().trim();
        String email = binding.email.getText().toString().trim();

        // If email is not valid, say it is not
        if(!isValidEmail(email)) {
            Toast.makeText(getActivity(), "Incorrect Email format, Try Again!", Toast.LENGTH_SHORT).show();
            return;
        }
        // If password is not valid, say it is not
        if(!isValidPassword(password)) {
            Toast.makeText(getActivity(), "Invalid Password format, Try Again!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if credentials are valid or not
        executorService.execute(()->{
            // Http service execution
            JSONObject response = httpService.authenticateUser(email, password);

            // Now check the message from the server
            try {
                int statusCode = response.getInt("status");

                // Authentication successful
                if(statusCode == 200) {
                    String id = response.getString("id");

                    requireActivity().runOnUiThread(
                            ()->{
                                // Show loading screen
                                showLoadingScreen();

                                // Navigate To Home Screen
                                Handler handler = new Handler();
                                handler.postDelayed(()->navigateToHomePage(id), 3000);
                            }
                    );
                }
                else if(statusCode == 201 || statusCode == 500) {
                    String serverMessage = response.getString("message");
                    requireActivity().runOnUiThread(
                            ()->{
                                Toast.makeText(getContext(), serverMessage, Toast.LENGTH_SHORT)
                                        .show();
                                enableButtons();
                            }
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    private boolean isValidEmail(@NonNull String email) {
        // Regular expression for email validation
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" // local part
                + "[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";  // domain part

        // Compile the regex
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        // Return whether the email matches the pattern
        return matcher.matches();
    }


    private boolean isValidPassword(@NonNull String password) {
        // Length of password only
        return password.length() >= 8;
    }

    private void disableButtons(){
        binding.loginButton.setEnabled(false);
        binding.signupLink.setEnabled(false);
    }

    private void enableButtons(){
        binding.loginButton.setEnabled(true);
        binding.signupLink.setEnabled(true);
    }

    private void showLoadingScreen() {
        binding.email.setEnabled(false);
        binding.password.setEnabled(false);

        binding.loginButton.setVisibility(View.GONE);
        binding.signupLink.setVisibility(View.GONE);

        binding.loadingSpinner.setVisibility(View.VISIBLE);
        binding.labelSuccessLogin.setVisibility(View.VISIBLE);
    }

    private void navigateToHomePage(String finalId) {
        // Bundle
        Bundle bundle = new Bundle();
        bundle.putString("id", finalId);

        // Redirection
        NavHostFragment.findNavController(LoginFragment.this).navigate(
                R.id.action_loginFragment_to_homeFragment, bundle
        );
    }

}
