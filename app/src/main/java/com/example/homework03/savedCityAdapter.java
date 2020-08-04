package com.example.homework03;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;

public class savedCityAdapter extends RecyclerView.Adapter<savedCityAdapter.ViewHolder> {
    public static savedCityAdapter.InteractWithMA interact;
    Context ctx;
    ArrayList<City> savedCities = new ArrayList<City>();

    public savedCityAdapter(ArrayList<City> savedCities, Context mainActivity) {
        this.savedCities = savedCities;
        this.ctx = mainActivity;
    }

    @NonNull
    @Override
    public savedCityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout rv_layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.savedcities_item, parent, false);
        savedCityAdapter.ViewHolder viewHolder = new savedCityAdapter.ViewHolder(rv_layout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final savedCityAdapter.ViewHolder holder, final int position) {
        interact = (savedCityAdapter.InteractWithMA) ctx;
        holder.selectedposition = position;
        holder.tv_savedCItyName.setText(savedCities.get(position).getCityName()+", "+savedCities.get(position).getCountry());
        holder.tv_savedCityTemp.setText("Temperature : "+savedCities.get(position).getMetricValue()+" "+savedCities.get(position).getMetricUnit());
        if(savedCities.get(position).isFavoutite()){
            holder.iv_Star.setImageResource(android.R.drawable.btn_star_big_on);
        }
        else{
            holder.iv_Star.setImageResource(android.R.drawable.btn_star_big_off);
        }
        PrettyTime p = new PrettyTime();
        holder.tv_savedCityUpdated.setText("Updated : "+String.valueOf(p.format(savedCities.get(position).getLocalObservationTime())));
       // holder.tv_savedCityUpdated.setText("updated: -- mins ago");
        holder.iv_Star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interact.setFavourate(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return savedCities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_savedCItyName, tv_savedCityTemp, tv_savedCityUpdated;
        ImageView iv_Star;
        int selectedposition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_savedCItyName = itemView.findViewById(R.id.tv_savedCityName);
            tv_savedCityTemp = itemView.findViewById(R.id.tv_savedCityTemp);
            tv_savedCityUpdated = itemView.findViewById(R.id.tv_savedCityUpdated);
            iv_Star = itemView.findViewById(R.id.iv_grayStar);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    interact = (savedCityAdapter.InteractWithMA) ctx;
                    interact.selecteditem(selectedposition);
                    return false;
                }
            });
        }
    }

    public interface InteractWithMA{
        void selecteditem(int position);
        void setFavourate(int position);
    }
}
