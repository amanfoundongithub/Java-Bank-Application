package com.application.bankapplication;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.application.bankapplication.databinding.FragmentWelcomeBinding;

import org.jetbrains.annotations.NotNull;

/**
 * Welcome Screen
 *
 * @author amanfoundongithub
 *
 */
public class WelcomeFragment extends Fragment {

    private FragmentWelcomeBinding binding;

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){
        // Get the binding element
        binding = FragmentWelcomeBinding.inflate(inflater, container, false);

        // Get the view from the binding
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view,
                              Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // Initialize the Start Button
        binding.startHereButton.setOnClickListener(v -> navigateToLogin());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void navigateToLogin() {
        // Disable button
        binding.startHereButton.setVisibility(View.GONE);
        // Show the spinner
        binding.loadingSpinnerWelcome.setVisibility(View.VISIBLE);
        binding.labelWaitWelcome.setVisibility(View.VISIBLE);

        // Handler to delayed input
        Handler handler = new Handler();

        handler.postDelayed(()->{
            NavHostFragment.findNavController(WelcomeFragment.this)
                    .navigate(R.id.action_welcomeFragment_to_loginFragment);
        }, 5000);

    }
}
