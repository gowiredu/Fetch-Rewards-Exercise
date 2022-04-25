package com.gordonowiredu.fetchrewardsexercise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class ListActivityRecyclerViewAdapter extends RecyclerView.Adapter<ListActivityRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<ListActivityItem> listActivityItem;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView id, name;

        public MyViewHolder(View view) {
            super(view);
            id = view.findViewById(R.id.customer_id);
            name = view.findViewById(R.id.name);
        }
    }


    public ListActivityRecyclerViewAdapter (ArrayList<ListActivityItem> listItem) {
        this.listActivityItem = listItem;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_activity_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.id.setText(listActivityItem.get(position).id);
        holder.name.setText(listActivityItem.get(position).name);
    }

    @Override
    public int getItemCount() {
        return listActivityItem.size();
    }
}

