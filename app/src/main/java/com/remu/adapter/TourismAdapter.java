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

public class TourismAdapter extends RecyclerView.Adapter<TourismAdapter.ViewHolder> {

    Application app;
    Activity activity;
    private ArrayList<PlaceModel> mDataset;
    private LatLng currentLatLng;
    private String userId;
    private DatabaseReference databaseReference;

    //TODO: DELETE WHEN UPLOADING OR DOCUMENTING!
    final private String API_KEY = "";

    public TourismAdapter(Application app, Activity activity, ArrayList<PlaceModel> mDataset, LatLng currentLatLng) {
        this.app = app;
        this.activity = activity;
        this.mDataset = mDataset;
        this.currentLatLng = currentLatLng;
        userId = FirebaseAuth.getInstance().getUid();
    }

    @NonNull
    @Override
    public TourismAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_tourism, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TourismAdapter.ViewHolder holder, int position) {
        holder.title.setText(mDataset.get(position).getPlaceName());

        if (mDataset.get(position).getPlaceRating() == 0) {
            holder.rating.setText("-");
        } else {
            holder.rating.setText(String.format("%.1f", mDataset.get(position).getPlaceRating()));
        }

        holder.distance.setText(String.format("%.2f km", countDistance(mDataset.get(position).getPlaceLocation())));

        if (mDataset.get(position).getPlacePhotoUri() != null) {
            Picasso.get().load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=500&photoreference=" + mDataset.get(position).getPlacePhotoUri()
                    + "&key=" + API_KEY)
                    .error(R.drawable.bg_loading_image)
                    .placeholder(R.drawable.bg_loading_image)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(holder.image);
        } else {
            LatLng location = mDataset.get(position).getPlaceLocation();
            Picasso.get().load("https://maps.googleapis.com/maps/api/streetview?size=500x300&location=" + location.latitude + "," + location.longitude
                    + "&fov=120&pitch=10&key=" + API_KEY)
                    .error(R.drawable.bg_loading_image)
                    .placeholder(R.drawable.bg_loading_image)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(holder.image);
        }

        holder.cardView.setOnClickListener((view) -> {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("UserData").child(userId).child(mDataset.get(position).getPlaceId());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        int temp = Integer.parseInt(dataSnapshot.child("Intensity").getValue().toString());
                        databaseReference.child("Intensity").setValue(Integer.toString(++temp));
                    } catch (NullPointerException np) {
                        databaseReference.child("Intensity").setValue("2").
                                addOnSuccessListener(aVoid ->
                                        databaseReference.child("Name").setValue(mDataset.get(position).getPlaceName()));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Intent intent = new Intent(activity.getBaseContext(), PlaceDetail.class);
            intent.putExtra("place_id", mDataset.get(position).getPlaceId());
            intent.putExtra("sender", "Tourism");
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView image;
        TextView title, rating, distance;

        ViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.tourism_card);
            image = itemView.findViewById(R.id.img_tour_place);
            title = itemView.findViewById(R.id.title_tour_place);
            rating = itemView.findViewById(R.id.rating_tour_place);
            distance = itemView.findViewById(R.id.distance_tour_place);
        }

    }

    private double countDistance(LatLng latLng) {
        LatLng currentLatLng = new LatLng(Double.parseDouble(app.getSharedPreferences("location", MODE_PRIVATE).getString("Latitude", null)), Double.parseDouble(app.getSharedPreferences("location", MODE_PRIVATE).getString("Longitude", null)));
        return Distance.distance(currentLatLng.latitude, latLng.latitude, currentLatLng.longitude, latLng.longitude);
    }

}
