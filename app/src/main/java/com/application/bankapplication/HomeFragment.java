package com.application.bankapplication;



import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.TooltipCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.application.bankapplication.databinding.FragmentHomeBinding;
import com.application.bankapplication.httpservice.HTTPService;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;



    // Email
    private String email;

    // Username
    private String username = "admin_xx";

    // Account Balance
    private double account_balance = 24567.23;

    // Name
    private String name = "Admin Admin";

    // Pro user?
    private boolean isProUser = false;

    //
    private String phone = "12";

    // ID of the User
    private String id;

    // Executor to execute the new thread
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // HTTP Service
    private final HTTPService httpService = new HTTPService();

    // Navigator
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(HomeFragment.this);
        // Fetch ID
        if(getArguments() != null) {
            id = getArguments().getString("id", "");
        }

        // Initialize a modal viewer for the balance
        binding.getBankStatement.setOnClickListener(v -> showBalanceModal());

        // Initialize a method to view transactions
        binding.getMyTransactions.setOnClickListener(v -> showTransactions());

        // Initialize a functionality to allow user to send money
        binding.sendMoney.setOnClickListener(v->sendMoney());

        // Log Out Button Initialization
        binding.logOut.setOnClickListener(v->logOut());

        // Copies username to clipboard
        binding.copyUsernameButton.setOnClickListener(v->copyToClipBoard());

        // Fetch details from server
        addUserDetails();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void showBalanceModal() {
        String modalContent = "Dear " + username + ", you have currently ₹" + account_balance + " left in"
                + " your account.";

        new AlertDialog.Builder(requireContext())
                .setTitle("Account Balance")
                .setMessage(modalContent)
                .setNegativeButton("OK",null)
                .show();
    }

    public void showTransactions() {
        Bundle bundle = new Bundle();
        bundle.putString("id",id);

        navController.navigate(R.id.action_homeFragment_to_transactionFragment,bundle);
    }

    public void sendMoney() {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putDouble("amount", account_balance);
        bundle.putString("phone", phone);

        navController.navigate(R.id.action_homeFragment_to_send_money_fragment, bundle);
    }

    public void logOut(){
        navController.navigate(R.id.action_homeFragment_to_loginFragment);
    }



    private void addUserDetails() {
        executorService.execute(() -> {
            JSONObject response = httpService.getUserDetails(id);

            try{
                // All variables are adjusted
                JSONObject details = response.getJSONObject("details");
                email = details.getString("email");
                account_balance = details.getDouble("balance");
                username = details.getString("username");
                phone = details.getString("phone");
                name = details.getString("name");
                isProUser = details.getBoolean("premium");
            }
            catch (Exception e) {
                e.printStackTrace();
            }


            requireActivity().runOnUiThread(()->{

                // Initialize the welcome Message
                String welcomeMsg = getGreetings() + ", " + name;
                binding.welcomeText.setText(welcomeMsg);

                String usernameMsg = "Your username: " + username;
                binding.userUsername.setText(usernameMsg);

                // If pro user, display a badge
                if(isProUser){
                    binding.proBadge.setVisibility(View.VISIBLE);
                    TooltipCompat.setTooltipText(binding.proBadge, "The User has subscribed" +
                            " to premium use!");
                }
                else {
                    binding.proBadge.setVisibility(View.GONE);
                }

                // Finally unlock all content
                unlockContent();
            });

        });
    }

    @NonNull
    private String getGreetings(){
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        // Greetings
        if(hour >= 5 && hour <= 12) {
            return "Good Morning";
        }
        else if(hour >= 12 && hour <= 17){
            return "Good Afternoon";
        }
        else if(hour >= 17 && hour <= 21){
            return "Good Evening";
        }
        else{
            return "Good Night";
        }
    }

    private void unlockContent(){
        binding.homeLoadingSpinner.setVisibility(View.GONE);
        binding.mainContentHome.setVisibility(View.VISIBLE);

        binding.logOut.setVisibility(View.VISIBLE);
    }

    private void copyToClipBoard(){
        ClipboardManager clipboardManager = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("username", username);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(requireContext(), "Username copied to clipboard", Toast.LENGTH_SHORT).show();
    }


}
