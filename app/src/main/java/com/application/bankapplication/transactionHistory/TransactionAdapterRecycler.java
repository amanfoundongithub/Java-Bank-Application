package com.application.bankapplication.transactionHistory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.bankapplication.R;
import com.application.bankapplication.models.Transaction;
import com.application.bankapplication.models.TransactionDetails;

import java.util.List;

public class TransactionAdapterRecycler extends RecyclerView.Adapter<TransactionAdapterRecycler.TransactionViewHolder>{

    // list
    private List<TransactionDetails> transactions;

    public TransactionAdapterRecycler(List<TransactionDetails> transactions){
        this.transactions = transactions;
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView description, amount, receiverId, serverMessage;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.transaction_description);
            amount = itemView.findViewById(R.id.transaction_amount);
            receiverId = itemView.findViewById(R.id.transaction_receiver_id);
            serverMessage = itemView.findViewById(R.id.transaction_message);

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
        TransactionDetails transaction = transactions.get(position);
        holder.description.setText(transaction.description);
        holder.amount.setText(Double.toString(transaction.amount));
        holder.serverMessage.setText(transaction.message);
        holder.receiverId.setText(transaction.receiverId);


    }

    @Override
    public int getItemCount(){
        return transactions.size();
    }


}
