package com.example.homework03;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class myAdapter extends RecyclerView.Adapter<myAdapter.ViewHolder>{

    public static InteractWithMainActivity interact;
    Context ctx;
    ArrayList<detailedWeatherOfCity> forecasts = new ArrayList<detailedWeatherOfCity>();

    public myAdapter(ArrayList<detailedWeatherOfCity> forecasts, Context mainActivity) {
        this.forecasts = forecasts;
        this.ctx = mainActivity;
    }

    @NonNull
    @Override
    public myAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout rv_layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.fivedaysitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(rv_layout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull myAdapter.ViewHolder holder, int position) {
        interact = (InteractWithMainActivity) ctx;
        holder.selectedposition = position;
        holder.tv_itemDate.setText(forecasts.get(position).getDate());
        Picasso.get().load("http://developer.accuweather.com/sites/default/files/"+forecasts.get(position).getDayIconID()+"-s.png").into(holder.iv_itemIcon);
    }

    @Override
    public int getItemCount() {
        return forecasts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_itemDate;
        ImageView iv_itemIcon;
        int selectedposition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_itemDate = itemView.findViewById(R.id.tv_itemDate);
            iv_itemIcon = itemView.findViewById(R.id.iv_itemIcon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    interact = (InteractWithMainActivity) ctx;
                    interact.selecteditem(selectedposition);
                }
            });
        }
    }

    public interface InteractWithMainActivity{
        void selecteditem(int position);
    }
}
