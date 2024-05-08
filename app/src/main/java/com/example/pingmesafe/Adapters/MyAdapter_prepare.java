package com.example.pingmesafe.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pingemesafe.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyAdapter_prepare extends RecyclerView.Adapter<MyAdapter_prepare.MyViewHolder> {

    private final List<Disaster> disasterList;

    public MyAdapter_prepare(List<Disaster> disasterList) {
        this.disasterList = disasterList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView disaster_name;
        public RoundedImageView image_disaster;

        public MyViewHolder(View itemView) {
            super(itemView);
            disaster_name = itemView.findViewById(R.id.text_disaster_name);
            image_disaster = itemView.findViewById(R.id.image_disaster);
        }

        public void setItem(Disaster disaster) {
            if (disaster != null) {
                disaster_name.setText(disaster.getName());
                image_disaster.setImageResource(disaster.getImageResourceId());
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
        Disaster disaster = disasterList.get(position);
        holder.setItem(disaster);
    }

    @Override
    public int getItemCount() {
        return disasterList.size();
    }
}
