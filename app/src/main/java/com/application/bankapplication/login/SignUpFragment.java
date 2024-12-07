package com.application.bankapplication.login;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.application.bankapplication.R;
import com.application.bankapplication.databinding.FragmentSignupBinding;
import com.application.bankapplication.httpservice.HTTPService;
import com.application.bankapplication.models.User;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Sign Up/Register Page Fragment
 *
 * @author amanfoundongithub
 *
 */
public class SignUpFragment extends Fragment {

    private FragmentSignupBinding binding;

    // Tracks the user object we created so far
    private User user;

    // Navigation controller for navigation
    private NavController navController;

    // HTTP Service
    private final HTTPService httpService = new HTTPService();

    // For asynchronous HTTP updates
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view,
                              Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // Initiate the navigation controller
        navController = NavHostFragment.findNavController(SignUpFragment.this);

        // Bind the Register Button
        binding.signupButton.setOnClickListener(v->createAccount());

        // Bind the functionality of Login Fragment redirection
        binding.signinLink.setOnClickListener(v->navigateToLogin());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Functions below

    public void navigateToLogin() {
        navController.navigate(R.id.action_signUpFragment_to_loginFragment);
    }


    public void createAccount() {
        disableButtons();
        // Get all the values
        String name = binding.name.getText().toString();
        String email = binding.email.getText().toString();
        String phone = binding.phoneNumber.getText().toString().trim();
        String username = binding.username.getText().toString().trim();
        String password = binding.password.getText().toString().trim();
        String confirm_password = binding.confirmPassword.getText().toString().trim();

        // Get the selected gender
        int selectedGenderId = binding.genderGroup.getCheckedRadioButtonId();
        String gender;
        if (selectedGenderId != -1) {
            gender = ((RadioButton) binding.getRoot().findViewById(selectedGenderId)).getText().toString();
        } else {
            gender = "";
        }


        // Validate inputs
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || username.isEmpty() ||
                password.isEmpty() || confirm_password.isEmpty() || gender.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            enableButtons();
            return;
        }

        // Confirm passwords
        if (!password.equals(confirm_password)) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            enableButtons();
            return;
        }

        // Create User
        user = new User(username, email, password, name, phone, 1000.0, false);

        // Backend
        executorService.execute(()->{
            try {
                JSONObject response = httpService.createUser(user);

                // Redirection
                String id = response.getString("id");

                // Run on UI Thread
                requireActivity().runOnUiThread(()-> {
                    showLoadingScreen();
                    Handler handler = new Handler();
                    handler.postDelayed(()->navigateToHome(id), 3000);
                });
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void navigateToHome(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);

        navController.navigate(R.id.action_signUpFragment_to_homeFragment, bundle);
    }

    private void disableButtons() {
        binding.signinLink.setEnabled(false);
        binding.signupButton.setEnabled(false);
    }
    private void enableButtons() {
        binding.signinLink.setEnabled(true);
        binding.signupButton.setEnabled(true);
    }

    private void showLoadingScreen() {
        binding.signupButton.setVisibility(View.GONE);
        binding.signinLink.setVisibility(View.GONE);

        binding.loadingSpinnerSignup.setVisibility(View.VISIBLE);
        binding.labelSuccessSignup.setVisibility(View.VISIBLE);
    }
}
