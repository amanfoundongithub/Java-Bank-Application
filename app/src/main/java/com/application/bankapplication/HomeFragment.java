package com.application.bankapplication;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.application.bankapplication.databinding.FragmentHomeBinding;
import com.application.bankapplication.httpservice.HTTPService;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private final HTTPService httpService = new HTTPService();

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        if(getArguments() != null) {
            email = getArguments().getString("email", "");
            id = getArguments().getString("id", "");
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onViewCreated(@NonNull View view,
                              Bundle savedInstanceState) {


        super.onViewCreated(view, savedInstanceState);

        // Fetch details from server
        initPage();
    }

    private void showBalanceModal() {

        // Content of the modal
        String modalContent = "Dear " + username + ", you have currently ₹" + account_balance + " left in"
                + " your account.";

        new AlertDialog.Builder(
                requireContext()
        )
                .setTitle("Account Balance")
                .setMessage(modalContent)
                .setNegativeButton("OK",null)
                .show();
    }

    private void showTransactions() {

        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        NavHostFragment.findNavController(HomeFragment.this)
                .navigate(R.id.action_homeFragment_to_transactionFragment,bundle);
    }

    private void sendMoney() {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putDouble("amount", account_balance);
        bundle.putString("phone", phone);

        NavHostFragment.findNavController(HomeFragment.this)
                .navigate(R.id.action_homeFragment_to_send_money_fragment, bundle);
    }

    private void initPage() {
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
                String welcomeMsg = "Welcome, " + username;
                binding.welcomeText.setText(welcomeMsg);

                // Initialize the email
                String emailMsg = "Email : <b>" + email + "</b>";
                binding.userEmail.setText(HtmlCompat.fromHtml(emailMsg, HtmlCompat.FROM_HTML_MODE_LEGACY));

                // If pro user, display a badge
                if(isProUser){
                    binding.proBadge.setVisibility(View.VISIBLE);
                    TooltipCompat.setTooltipText(binding.proBadge, "The User has subscribed" +
                            " to premium use!");
                }
                else {
                    binding.proBadge.setVisibility(View.GONE);
                }

                // Initialize a modal viewer for the balance
                binding.getBankStatement.setOnClickListener(v -> showBalanceModal());

                // Initialize a method to view transactions
                binding.getMyTransactions.setOnClickListener(v -> showTransactions());

                // Initialize a functionality to allow user to send money
                binding.sendMoney.setOnClickListener(v->sendMoney());

                // Finally unlock all content
                binding.homeLoadingSpinner.setVisibility(View.GONE);
                binding.mainContentHome.setVisibility(View.VISIBLE);

            });

        });
    }
}
