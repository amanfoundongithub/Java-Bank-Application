package com.application.bankapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.application.bankapplication.databinding.FragmentSendMoneyBinding;
import com.application.bankapplication.httpservice.HTTPService;
import com.application.bankapplication.models.Transaction;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.w3c.dom.Text;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SendMoneyFragment extends Fragment {

    private FragmentSendMoneyBinding binding;

    private String senderId;
    private String id;
    private double amount;
    private String phone;

    final private HTTPService httpService = new HTTPService();

    final private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){
        binding = FragmentSendMoneyBinding.inflate(inflater, container, false);

        if(getArguments() != null) {
            // Username
            senderId = getArguments().getString("username");

            // ID
            id = getArguments().getString("id");

            // Get the amount in account
            amount = getArguments().getDouble("amount");

            // Phone Number
            phone = getArguments().getString("phone");
        }
        return binding.getRoot();
    }

    public void onViewCreated(@NotNull View view,
                              Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // Add method to confirm sending money
        binding.btnSendMoney.setOnClickListener(v->confirmSending());
//        disableButton();

        // Add method to go back to home
        binding.btnGoToHome.setOnClickListener(v->goToHome());

        // Bind the receiver Id to the event change listener
//        binding.recieverId.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                // Check if text is there
//                executorService.execute(()->{
//                    JSONObject response = httpService.checkIfUsernameExist(charSequence.toString());
//                            requireActivity().runOnUiThread(()->{
//                                try {
//                                    int statusCode = response.getInt("status");
//                                    if (statusCode == 200) {
//                                        boolean exists = response.getBoolean("exist");
//                                        if(exists){
//                                            enableButton();
//                                        }
//                                        else{
//                                            disableButton();
//                                        }
//                                    } else {
//                                        Toast.makeText(getContext(),
//                                                "Internal Server Error, Try Again Later",
//                                                Toast.LENGTH_SHORT).show();
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            });
//                });
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

    }

    private void disableButton() {
        binding.btnSendMoney.setEnabled(false);
    }

    private void enableButton() {
        binding.btnSendMoney.setEnabled(true);
    }

    public void goToHome() {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        NavHostFragment.findNavController(SendMoneyFragment.this)
                .navigate(R.id.action_send_money_fragment_to_homeFragment, bundle);
    }

    public void confirmSending() {
        // Receiver ID
        String receiverId = binding.recieverId.getText().toString().trim();

        if(receiverId.isEmpty()) {
            Toast.makeText(getContext(), "Receiver ID cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get amount
        double requestedAmount = Double.parseDouble(binding.transactionAmount.getText().toString().trim());

        if(requestedAmount > amount) {
            Toast.makeText(getContext(), "You don't have this much money in account!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get description
        String description = binding.description.getText().toString().trim();
        if(description.isEmpty()) {
            Toast.makeText(getContext(), "Add a Description also!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the transaction is confirmed by the user
        if(!binding.confirmTransaction.isChecked()){
            Toast.makeText(getContext(), "Please confirm your transaction", Toast.LENGTH_SHORT).show();
            return;
        }

        Transaction transaction = new Transaction(senderId, receiverId, description, requestedAmount);

        // Confirm once before sending
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Confirmation")
                .setMessage("Are you really sure? You cannot undo this action.")
                .setPositiveButton("Proceed", (dialog,which) -> {
                    processTransaction(transaction);
                })
                .setNegativeButton("OK",(dialog, which)->dialog.dismiss())
                .show();
    }

    private void processTransaction(Transaction transaction) {
        // Instantiate
        AlertDialog.Builder dialogWindow = new AlertDialog.Builder(getContext());

        // Set Title
        dialogWindow.setTitle("To confirm your transaction, enter your 10 digit phone number:");

        // Create an input field
        final EditText phoneNumber = new EditText(getContext());

        phoneNumber.setHint("Enter your phone Number");
        phoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);

        dialogWindow.setView(phoneNumber);

        dialogWindow.setPositiveButton("Confirm", (dialog, which)->{
            String phN = phoneNumber.getText().toString().trim();

            if(phN.equals(phone)){
                Toast.makeText(getContext(), "Confirmed!", Toast.LENGTH_SHORT).show();
                binding.btnSendMoney.setEnabled(false);
                binding.btnGoToHome.setEnabled(false);
                sendMoneyRequest(transaction);
            }
            else {
                Toast.makeText(getContext(),"Invalid, Try Again!", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
        });

        dialogWindow.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        dialogWindow.show();
    }

    private void sendMoneyRequest(Transaction transaction) {
        executorService.execute(()->{
            JSONObject response = httpService.sendMoney(transaction);

            try {
                int statusCode = response.getInt("status");
                if(statusCode == 200){
                    String transactionId = response.getString("id");

                    requireActivity().runOnUiThread(()->{
                        Bundle bundle = new Bundle();
                        bundle.putString("transactionId",transactionId);
                        bundle.putString("senderId", senderId);
                        bundle.putString("id",id);

                        // Redirect to confirmation page
                        NavHostFragment.findNavController(SendMoneyFragment.this)
                                .navigate(R.id.action_send_money_fragment_to_confirm_transaction_fragment, bundle);
                    });

                }
                // Server error
                else {
                    String serverMessage = "Server response:" + response.getString("message");
                    binding.btnSendMoney.setEnabled(true);
                    binding.btnGoToHome.setEnabled(true);
                    new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Error")
                            .setMessage(serverMessage)
                            .setNegativeButton("OK",(dialog, which)->{dialog.dismiss();})
                            .show();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}
