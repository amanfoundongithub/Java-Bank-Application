package com.application.bankapplication.transaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.bankapplication.R;
import com.application.bankapplication.models.Transaction;

import java.util.List;

public class TransactionAdapterRecycler extends RecyclerView.Adapter<TransactionAdapterRecycler.TransactionViewHolder>{

    // list
    private List<Transaction> transactions;

    public TransactionAdapterRecycler(List<Transaction> transactions){
        this.transactions = transactions;
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView description, amount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.transaction_description);
            amount = itemView.findViewById(R.id.transaction_amount);
        }
    }



    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction_layout, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.description.setText(transaction.description);
        holder.amount.setText("20093");
    }

    @Override
    public int getItemCount(){
        return transactions.size();
    }


}
