package com.application.bankapplication.transactionHistory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.bankapplication.R;
import com.application.bankapplication.models.TransactionDetails;

import java.util.List;

public class TransactionAdapterRecycler extends RecyclerView.Adapter<TransactionAdapterRecycler.TransactionViewHolder>{

    // list
    private final List<TransactionDetails> transactions;

    private final String username;

    public TransactionAdapterRecycler(List<TransactionDetails> transactions, String username){
        this.transactions = transactions;
        this.username = username;
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView description, amount, receiverId, serverMessage;

        ImageView credit, debit;
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.transaction_description);
            amount = itemView.findViewById(R.id.transaction_amount);
            receiverId = itemView.findViewById(R.id.transaction_receiver_id);
            serverMessage = itemView.findViewById(R.id.transaction_message);
            credit = itemView.findViewById(R.id.receive_amount);
            debit = itemView.findViewById(R.id.send_amount);
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

        // Check if debit or credit
        boolean isSending = transaction.senderId.equals(username);

        System.out.println(transaction);
        System.out.println(username);
        // Add description
        holder.description.setText(transaction.description);
        // Add amount
        String amountString = "₹" + transaction.amount;
        holder.amount.setText(amountString);
        // Server Message if any
        holder.serverMessage.setText(transaction.message);
        // Receiver ID as well as the transaction if any
        if(isSending){
            holder.receiverId.setText(transaction.receiverId);
            holder.debit.setVisibility(View.VISIBLE);
        }
        else{
            holder.receiverId.setText(transaction.senderId);
            holder.credit.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount(){
        return transactions.size();
    }

}
