package com.application.bankapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.application.bankapplication.databinding.FragmentConfimTransactionBinding;
import com.application.bankapplication.httpservice.HTTPService;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConfirmTransactionFragment extends Fragment {

    private String transactionId;
    private String senderId;

    final private HTTPService httpService = new HTTPService();
    final private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private FragmentConfimTransactionBinding binding;

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){
        binding = FragmentConfimTransactionBinding.inflate(inflater, container, false);

        if(getArguments() != null){
            transactionId = getArguments().getString("transactionId");
            senderId = getArguments().getString("senderId");
        }
        return binding.getRoot();
    }

    public void onViewCreated(@NotNull View view,
                              Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // Load the transaction state
        initTransaction();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void goToHome() {
        Bundle bundle = new Bundle();
        bundle.putString("id", senderId);
        NavHostFragment.findNavController(ConfirmTransactionFragment.this)
                .navigate(R.id.action_confirm_transaction_fragment_to_homeFragment, bundle);
    }



    // Code below
    private void initTransaction() {
        executorService.execute(()->{
            try {
                JSONObject response = httpService.confirmTransaction(transactionId);

                boolean signed = response.getBoolean("signed");
                String receiverId = response.getString("receiverId");
                double amount = response.getDouble("amount");

                requireActivity().runOnUiThread(()->{
                    // If verified by server
                    binding.loadingSpinner.setVisibility(View.GONE);
                    if(signed){
                        binding.tickIcon.setVisibility(View.VISIBLE);

                        String message = "Successfully sent " + amount + " to " + receiverId;
                        binding.statusText.setText(message);
                    }
                    else {
                        binding.crossIcon.setVisibility(View.VISIBLE);

                        String message = "Error in sending " + amount + " to " + receiverId;
                        binding.statusText.setText(message);
                    }

                    binding.goBackButton.setVisibility(View.VISIBLE);
                    binding.goBackButton.setOnClickListener(v->goToHome());
                });

            }
            catch (Exception e) {
                e.printStackTrace();

            }
        });
    }

}
