package com.application.bankapplication.transactionHistory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.application.bankapplication.R;
import com.application.bankapplication.httpservice.HTTPService;
import com.application.bankapplication.models.Transaction;
import com.application.bankapplication.models.TransactionDetails;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionFragment extends Fragment {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final HTTPService httpService = new HTTPService();

    private String id;

    private RecyclerView recyclerView;

    private List<TransactionDetails> transactionList = new ArrayList<TransactionDetails>();

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){

        if(getArguments() != null) {
            id = getArguments().getString("id");

        }

        System.out.println(transactionList);
        //
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        //
        findAllTransactions(view);
        return view;
    }

    public void onViewCreated(@NotNull View view,
                              Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // Bind the event to the button
        view.findViewById(R.id.goBackButton).setOnClickListener(v->goBack());

    }

    public void goBack() {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        NavHostFragment.findNavController(TransactionFragment.this)
                .navigate(R.id.action_transactionFragment_to_homeFragment, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void findAllTransactions(View view) {
        executorService.execute(()->{
            JSONObject response = httpService.getAllTransactions(id);

            try {
                // Get all transactions
                JSONArray trans = response.getJSONArray("transactions");
                for (int i = 0 ; i < trans.length() ; i++){
                    JSONObject obj = trans.getJSONObject(i);
                    // New object
                    TransactionDetails transactionDetails = new TransactionDetails(
                            obj.getString("senderId"),
                            obj.getString("receiverId"),
                            obj.getString("description"),
                            obj.getDouble("amount")
                    );
                    transactionDetails.addServerDetails(
                            obj.getBoolean("status"),
                            obj.getString("message"),
                            obj.getString("transactionDate")
                    );

                    transactionList.add(transactionDetails);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            requireActivity().runOnUiThread(()->{
                recyclerView = view.findViewById(R.id.transactionrecyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                TransactionAdapterRecycler adapter = new TransactionAdapterRecycler(transactionList);
                recyclerView.setAdapter(adapter);
            });
        });
    }

}
