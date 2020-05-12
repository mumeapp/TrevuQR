package com.remu.adapter;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.remu.POJO.Distance;
import com.remu.POJO.PlaceModel;
import com.remu.PlaceDetail;
import com.remu.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class FoodBeveragesTourismResultAdapter extends RecyclerView.Adapter<FoodBeveragesTourismResultAdapter.ViewHolder> {

    Application app;
    Activity activity;
    ArrayList<PlaceModel> mDataset;
    DatabaseReference databaseReference;
    String userId, senderType;

    //TODO: DELETE WHEN UPLOADING OR DOCUMENTING!
    final private String API_KEY = "";

    public FoodBeveragesTourismResultAdapter(Application app, Activity activity, String senderType, ArrayList<PlaceModel> mDataset) {
        this.app = app;
        this.activity = activity;
        this.mDataset = mDataset;
        userId = FirebaseAuth.getInstance().getUid();
        this.senderType = senderType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_vertical_mode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.recommendedName.setText(mDataset.get(position).getPlaceName());
        holder.recommendedAddress.setText(mDataset.get(position).getPlaceAddress());

        if (mDataset.get(position).getPlaceRating() == 0) {
            holder.recommendedRating.setText("-");
        } else {
            holder.recommendedRating.setText(String.format("%.1f", mDataset.get(position).getPlaceRating()));
        }

        holder.recommendedDistance.setText(String.format("%.2f km", countDistance(mDataset.get(position).getPlaceLocation())));

        if (mDataset.get(position).getPlacePhotoUri() != null) {
            Picasso.get().load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=500&photoreference=" + mDataset.get(position).getPlacePhotoUri()
                    + "&key="+ API_KEY)
                    .error(R.drawable.bg_loading_image)
                    .placeholder(R.drawable.bg_loading_image)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(holder.recommendedImage);
        } else {
            LatLng location = mDataset.get(position).getPlaceLocation();
            Picasso.get().load("https://maps.googleapis.com/maps/api/streetview?size=500x300&location=" + location.latitude + "," + location.longitude
                    + "&fov=120&pitch=10&key="+ API_KEY)
                    .error(R.drawable.bg_loading_image)
                    .placeholder(R.drawable.bg_loading_image)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(holder.recommendedImage);
        }

        holder.recommendeLayout.setOnClickListener(view -> {
            System.out.println("weight " + mDataset.get(position).getPlaceWeight());
            databaseReference = FirebaseDatabase.getInstance().getReference().child("UserData").child(userId).child(mDataset.get(position).getPlaceId());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        int temp = Integer.parseInt(dataSnapshot.child("Intensity").getValue().toString());
                        databaseReference.child("Intensity").setValue(Integer.toString(++temp)).addOnSuccessListener(aVoid -> {
                            Intent intent = new Intent(activity.getBaseContext(), PlaceDetail.class);
                            intent.putExtra("place_id", mDataset.get(position).getPlaceId());
                            intent.putExtra("sender", senderType);
                            activity.startActivity(intent);
                        });

                    } catch (NullPointerException np) {
                        databaseReference.child("Intensity").setValue("2").
                                addOnSuccessListener(aVoid ->
                                        databaseReference.child("Name").setValue(mDataset.get(position).getPlaceName()).
                                                addOnSuccessListener(aVoid1 -> {
                                                    Intent intent = new Intent(activity.getBaseContext(), PlaceDetail.class);
                                                    intent.putExtra("place_id", mDataset.get(position).getPlaceId());
                                                    intent.putExtra("sender", senderType);
                                                    activity.startActivity(intent);
                                                }));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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

        LinearLayout recommendeLayout;
        ImageView recommendedImage;
        TextView recommendedName, recommendedAddress, recommendedRating, recommendedDistance;

        ViewHolder(View itemView) {
            super(itemView);

            recommendeLayout = itemView.findViewById(R.id.food_layout);
            recommendedImage = itemView.findViewById(R.id.food_image);
            recommendedName = itemView.findViewById(R.id.food_name);
            recommendedAddress = itemView.findViewById(R.id.food_description);
            recommendedRating = itemView.findViewById(R.id.food_rating);
            recommendedDistance = itemView.findViewById(R.id.food_distance);
        }
    }
}
