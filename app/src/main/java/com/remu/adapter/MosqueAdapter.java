package com.remu.adapter;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.remu.POJO.Distance;
import com.remu.POJO.GetDirection;
import com.remu.POJO.PlaceModel;
import com.remu.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class MosqueAdapter extends RecyclerView.Adapter<MosqueAdapter.ViewHolder> {

    private static final String TAG = "MosqueAdapter";

    private Context context;
    private Application app;
    private ArrayList<PlaceModel> mDataset;
    private GoogleMap mMap;

    public MosqueAdapter(Application app, Context context, ArrayList<PlaceModel> mDataset, GoogleMap mMap) {
        this.app = app;
        this.context = context;
        this.mDataset = mDataset;
        this.mMap = mMap;
    }

    @NonNull
    @Override
    public MosqueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_mosque, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MosqueAdapter.ViewHolder holder, int position) {
        holder.mosqueName.setText(mDataset.get(position).getPlaceName());
        holder.targetAddress.setText(mDataset.get(position).getPlaceAddress());
        holder.distance.setText(String.format("%.2f km", countDistance(mDataset.get(position).getPlaceLocation())));

        if (mDataset.get(position).getPlaceRating() == 0) {
            holder.rating.setText("-");
        } else {
            holder.rating.setText(String.format("%.1f", mDataset.get(position).getPlaceRating()));
        }

        holder.discoverbutton.setOnClickListener((view) -> {
            new GetDirection(app, mMap, mDataset.get(position).getPlaceName(), mDataset.get(position).getPlaceLocation()).execute(getDirectionsUrl(new LatLng(Double.parseDouble(app.getSharedPreferences("location", MODE_PRIVATE).getString("Latitude", "-33.8523341")),
                            Double.parseDouble(app.getSharedPreferences("location", MODE_PRIVATE).getString("Longitude", "151.2106085"))),
                    mDataset.get(position).getPlaceLocation()));
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

        Button discoverbutton;
        TextView mosqueName, targetAddress, distance, rating;

        ViewHolder(View itemView) {
            super(itemView);
            discoverbutton = itemView.findViewById(R.id.discover_mosque);
            mosqueName = itemView.findViewById(R.id.MosqueName);
            targetAddress = itemView.findViewById(R.id.TargetAddress);
            distance = itemView.findViewById(R.id.distance);
            rating = itemView.findViewById(R.id.rating);
        }

    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String strDest = "destination=" + dest.latitude + "," + dest.longitude;

        String sensor = "sensor=false";
        String mode = "mode=driving";
        String parameters = strOrigin + "&" + strDest + "&" + sensor + "&" + mode;
        String output = "json";

        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + R.string.API_KEY;
    }

}
