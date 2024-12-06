package com.application.bankapplication.login;

import android.os.Bundle;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;


    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final HTTPService httpService = new HTTPService();

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NotNull View view,
                              Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // Login Button
        binding.loginButton.setOnClickListener(v -> logIn());

        // Sign Up Button
        binding.signupLink.setOnClickListener(v-> signUp());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    private boolean isValidEmail(@NonNull String email) {
        // Regular expression for email validation
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" // local part
                + "[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";  // domain part

        // Compile the regex
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(emailRegex);
        java.util.regex.Matcher matcher = pattern.matcher(email);

        // Return whether the email matches the pattern
        return matcher.matches();
    }

    private boolean isValidPassword(@NonNull String password) {
        return password.length() >= 8;
    }

    public void signUp() {
        NavHostFragment.findNavController(LoginFragment.this)
                .navigate(R.id.action_loginFragment_to_signUpFragment);
    }
    public void logIn() {
        // Disable the Button
        binding.loginButton.setEnabled(false);

        // Email
        String email = binding.email.getText().toString().trim();

        // Password
        String password = binding.password.getText().toString().trim();

        // If email is not valid, say it is not
        if(!isValidEmail(email)) {
            Toast.makeText(getActivity(), "Incorrect Email format, Try Again!", Toast.LENGTH_SHORT).show();
        }

        // If password is not valid, say it is not
        if(!isValidPassword(password)) {
            Toast.makeText(getActivity(), "Invalid Password format, Try Again!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if credentials are valid
        executorService.execute(()->{
            String id;
            try{
                id = httpService.authenticateUser(email, password).getString("id");
            }
            catch (Exception e) {
                id = "-2";
            }

            String finalId = id;
            requireActivity().runOnUiThread(
                    ()->{
                        if("-2".equals(finalId)){
                            Toast.makeText(getActivity(), "Could not validate due to Internal Server Error, Try Again!", Toast.LENGTH_SHORT)
                                    .show();
                            binding.loginButton.setEnabled(true);
                            return;
                        }
                        else if("-1".equals(finalId)){
                            Toast.makeText(getActivity(), "Invalid Credentials, Try Again!", Toast.LENGTH_SHORT).show();
                            binding.loginButton.setEnabled(true);
                            return;
                        }

                        // Create a bundle to perform transaction
                        Bundle bundle = new Bundle();
                        bundle.putString("id", finalId);

                        NavHostFragment.findNavController(LoginFragment.this).navigate(
                                R.id.action_loginFragment_to_homeFragment, bundle
                        );
                    }
            );
        });

    }
}
