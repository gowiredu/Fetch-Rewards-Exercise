package com.gordonowiredu.fetchrewardsexercise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivityRecyclerViewAdapter extends RecyclerView.Adapter<MainActivityRecyclerViewAdapter.ViewHolder> {

    ArrayList<String> listIDs;
    private OnIDRecyclerViewClickListener onIDRecyclerViewClickListener;


    public MainActivityRecyclerViewAdapter(ArrayList<String> listIDs,OnIDRecyclerViewClickListener onIDRecyclerViewClickListener){
        this.listIDs = listIDs;
        this.onIDRecyclerViewClickListener = onIDRecyclerViewClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        //setting the layout view
        View view = layoutInflater.inflate(R.layout.main_activity_row,parent,false);
        return new ViewHolder(view, onIDRecyclerViewClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        // set the values of the RecyclerView rows from the listIDs ArrayList.
        holder.textView.setText( listIDs.get(position));

    }

    @Override
    public int getItemCount() {
        return listIDs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //initializing the elements of the view to the view holder
        TextView textView;
        OnIDRecyclerViewClickListener onIDRecyclerViewClickListener;

        public ViewHolder(@NonNull View itemView, OnIDRecyclerViewClickListener onIDRecyclerViewClickListener) {
            super(itemView);
            textView = itemView.findViewById(R.id.listId);
            this.onIDRecyclerViewClickListener = onIDRecyclerViewClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onIDRecyclerViewClickListener.onIDClick(getAbsoluteAdapterPosition());
        }
    }
    public interface OnIDRecyclerViewClickListener{
        void onIDClick(int pos);
    }
}


