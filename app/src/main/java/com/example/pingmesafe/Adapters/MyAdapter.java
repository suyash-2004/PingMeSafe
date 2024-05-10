package com.example.pingmesafe.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pingmesafe.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private final List<List<String>> data;

    public MyAdapter(List<List<String>> data) {
        this.data = data;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView disaster_name, disaster_details, disaster_time;
        public AppCompatImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            disaster_name = itemView.findViewById(R.id.disaster_name);
            disaster_details = itemView.findViewById(R.id.disaster_details);
            disaster_time = itemView.findViewById(R.id.disaster_time);
            image = itemView.findViewById(R.id.image);
        }

        public void setItem(List<String> item) {
            if (item != null && item.size() >= 3) {
                disaster_name.setText(item.get(0));
                disaster_details.setText(item.get(1));
                disaster_time.setText(item.get(2));
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        List<String> listItem = data.get(position);
        if (listItem != null && !listItem.isEmpty()) {
            holder.setItem(listItem); // Pass the listItem to setItem method
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
