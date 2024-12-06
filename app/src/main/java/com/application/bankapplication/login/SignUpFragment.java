package com.application.bankapplication.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.application.bankapplication.R;
import com.application.bankapplication.databinding.FragmentSignupBinding;
import com.application.bankapplication.httpservice.HTTPService;
import com.application.bankapplication.models.User;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignUpFragment extends Fragment {

    private FragmentSignupBinding binding;

    private final HTTPService httpService = new HTTPService();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private User user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NotNull View view,
                              Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        binding.signupButton.setOnClickListener(v->createAccount());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void createAccount() {
        // Get all the values
        String name = binding.name.getText().toString();
        String email = binding.email.getText().toString();
        String phone = binding.phoneNumber.getText().toString().trim();
        String username = binding.username.getText().toString().trim();
        String password = binding.password.getText().toString().trim();
        String cnfpassword = binding.confirmPassword.getText().toString().trim();

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
                password.isEmpty() || cnfpassword.isEmpty() || gender.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Confirm passwords
        if (!password.equals(cnfpassword)) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create User
        user = new User(
                username, email, password, name, phone, 0.0, false
        );

        System.out.println(user);
        // Backend
        executorService.execute(()->{
            try {
                JSONObject response = httpService.createUser(user);

                Bundle bundle = new Bundle();
                bundle.putString("id", response.getString("id"));
                requireActivity().runOnUiThread(()->{
                    NavHostFragment.findNavController(SignUpFragment.this)
                            .navigate(R.id.action_signUpFragment_to_homeFragment, bundle);
                });

            }
            catch (Exception e) {

            }

        });
        

    }
}
