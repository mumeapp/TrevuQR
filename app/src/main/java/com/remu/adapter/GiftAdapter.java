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
import com.remu.HalalGiftDetail;
import com.remu.POJO.Distance;
import com.remu.POJO.PlaceModel;
import com.remu.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.ViewHolder> {

    Application app;
    Activity activity;
    ArrayList<PlaceModel> mDataset;

    //TODO: DELETE WHEN UPLOADING OR DOCUMENTING!
    final private String API_KEY = "";

    public GiftAdapter(Application app, Activity activity, ArrayList<PlaceModel> mDataset) {
        this.app = app;
        this.activity = activity;
        this.mDataset = mDataset;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_toko, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.giftName.setText(mDataset.get(position).getPlaceName());
        holder.giftAddress.setText(mDataset.get(position).getPlaceAddress());

        if (mDataset.get(position).getPlaceRating() == 0) {
            holder.giftRating.setText("-");
        } else {
            holder.giftRating.setText(String.format("%.1f", mDataset.get(position).getPlaceRating()));
        }

        holder.giftDistance.setText(String.format("%.2f km", countDistance(mDataset.get(position).getPlaceLocation())));

        if (mDataset.get(position).getPlacePhotoUri() != null) {
            Picasso.get().load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=500&photoreference=" + mDataset.get(position).getPlacePhotoUri()
                    + "&key=" + API_KEY)
                    .error(R.drawable.bg_loading_image)
                    .placeholder(R.drawable.bg_loading_image)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(holder.giftImage);
        } else {
            LatLng location = mDataset.get(position).getPlaceLocation();
            Picasso.get().load("https://maps.googleapis.com/maps/api/streetview?size=500x300&location=" + location.latitude + "," + location.longitude
                    + "&fov=120&pitch=10&key=" + API_KEY)
                    .error(R.drawable.bg_loading_image)
                    .placeholder(R.drawable.bg_loading_image)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(holder.giftImage);
        }

        holder.giftCard.setOnClickListener((v) -> {
            Intent intent = new Intent(activity.getBaseContext(), HalalGiftDetail.class);
            intent.putExtra("place_id", mDataset.get(position).getPlaceId());
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private double countDistance(LatLng latLng) {
        LatLng currentLatLng = new LatLng(Double.parseDouble(app.getSharedPreferences("location", MODE_PRIVATE).getString("Latitude", null)),
                Double.parseDouble(app.getSharedPreferences("location", MODE_PRIVATE).getString("Longitude", null)));
        return Distance.distance(currentLatLng.latitude, latLng.latitude, currentLatLng.longitude, latLng.longitude);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CardView giftCard;
        ImageView giftImage;
        TextView giftName, giftAddress, giftRating, giftDistance;

        ViewHolder(View itemView) {
            super(itemView);

            giftCard = itemView.findViewById(R.id.gift_card);
            giftImage = itemView.findViewById(R.id.gift_image);
            giftName = itemView.findViewById(R.id.gift_name);
            giftAddress = itemView.findViewById(R.id.gift_address);
            giftRating = itemView.findViewById(R.id.gift_rating);
            giftDistance = itemView.findViewById(R.id.gift_distance);
        }
    }

}
