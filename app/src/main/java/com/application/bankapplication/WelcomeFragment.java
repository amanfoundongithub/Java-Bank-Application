package com.application.bankapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.application.bankapplication.databinding.FragmentWelcomeBinding;

import org.jetbrains.annotations.NotNull;

public class WelcomeFragment extends Fragment {

    private FragmentWelcomeBinding binding;


    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){

        binding = FragmentWelcomeBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    public void onViewCreated(@NotNull View view,
                              Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        binding.startHereButton.setOnClickListener(v ->
                NavHostFragment.findNavController(WelcomeFragment.this)
                        .navigate(R.id.action_welcomeFragment_to_loginFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}
