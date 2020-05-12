package com.remu.adapter;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.remu.POJO.Distance;
import com.remu.POJO.PlaceModel;
import com.remu.PlaceDetail;
import com.remu.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class MidnightFoodAdapter extends RecyclerView.Adapter<MidnightFoodAdapter.ViewHolder> {

    Application app;
    Activity activity;
    ArrayList<PlaceModel> mDataset;

    //TODO: DELETE WHEN UPLOADING OR DOCUMENTING!
    final private String API_KEY = "F";

    public MidnightFoodAdapter(Application app, Activity activity, ArrayList<PlaceModel> mDataset) {
        this.app = app;
        this.activity = activity;
        this.mDataset = mDataset;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_horizontal_mode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.foodName.setText(mDataset.get(position).getPlaceName());

        if (mDataset.get(position).getPlaceRating() == 0) {
            holder.foodRating.setText("-");
        } else {
            holder.foodRating.setText(String.format("%.1f", mDataset.get(position).getPlaceRating()));
        }

        holder.foodDistance.setText(String.format("%.2f km", countDistance(mDataset.get(position).getPlaceLocation())));

        if (mDataset.get(position).getPlacePhotoUri() != null) {
            Picasso.get().load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=500&photoreference=" + mDataset.get(position).getPlacePhotoUri()
                    + "&key="+ API_KEY)
                    .error(R.drawable.bg_loading_image)
                    .placeholder(R.drawable.bg_loading_image)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(holder.foodImage);
        } else {
            LatLng location = mDataset.get(position).getPlaceLocation();
            Picasso.get().load("https://maps.googleapis.com/maps/api/streetview?size=500x300&location=" + location.latitude + "," + location.longitude
                    + "&fov=120&pitch=10&key="+ API_KEY)
                    .error(R.drawable.bg_loading_image)
                    .placeholder(R.drawable.bg_loading_image)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(holder.foodImage);
        }

        holder.foodCard.setOnClickListener((v) -> {
            Intent intent = new Intent(activity.getBaseContext(), PlaceDetail.class);
            intent.putExtra("place_id", mDataset.get(position).getPlaceId());
            intent.putExtra("sender", "HalalFood");
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private double countDistance(LatLng latLng) {
        LatLng currentLatLng = new LatLng(Double.parseDouble(app.getSharedPreferences("location", MODE_PRIVATE).getString("Latitude", null)), Double.parseDouble(app.getSharedPreferences("location", MODE_PRIVATE).getString("Longitude", null)));
        return Distance.distance(currentLatLng.latitude, latLng.latitude, currentLatLng.longitude, latLng.longitude);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CardView foodCard;
        ImageView foodImage;
        TextView foodName, foodRating, foodDistance;

        ViewHolder(View itemView) {
            super(itemView);

            foodCard = itemView.findViewById(R.id.food_card);
            foodImage = itemView.findViewById(R.id.food_image);
            foodName = itemView.findViewById(R.id.food_name);
            foodRating = itemView.findViewById(R.id.food_rating);
            foodDistance = itemView.findViewById(R.id.food_distance);
        }
    }
}
