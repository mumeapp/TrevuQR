package com.remu;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.remu.POJO.Distance;
import com.remu.POJO.Rating;
import com.saber.chentianslideback.SlideBackActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class HalalGiftDetail extends SlideBackActivity {

    public static final String TAG = "HalalGiftDetail";

    private PlacesClient placesClient;

    private ImageView giftImage, userImage;
    private TextView giftName, giftDistance, giftRating, giftAddress;
    private RatingBar giftRatingBar;
    private EditText giftReview;
    private Button giftReviewButton;
    private RecyclerView listReviews;
    private DatabaseReference databaseReference, databaseReview;
    private ProgressDialog progressDialog;
    private FirebaseRecyclerAdapter<Rating, HalalGiftDetail.HalalGiftDetailAdapter> firebaseRecyclerAdapter;
    private boolean isSaved;
    private CardView buttonSave;
    private ImageView imageButtonSave;

    //TODO: DELETE WHEN UPLOADING OR DOCUMENTING!
    final private String API_KEY = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halal_gift_detail);

        Places.initialize(getApplicationContext(), getString(R.string.API_KEY));
        placesClient = Places.createClient(this);

        initializeUI();
        Animatoo.animateSlideLeft(this);

        String uId = FirebaseAuth.getInstance().getUid();
        String nama = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        Glide.with(HalalGiftDetail.this)
                .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                .placeholder(R.drawable.ic_default_avatar)
                .into(userImage);



        databaseReview = FirebaseDatabase.getInstance().getReference().child("Places Review").child(getIntent().getStringExtra("place_id"));
        System.out.println("TEST#01: " + databaseReview);

        Query query = databaseReview.orderByChild(uId);

        FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>()
                .setQuery(query, Rating.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Rating, HalalGiftDetailAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HalalGiftDetailAdapter halalGiftDetailAdapter, int i, @NonNull Rating rating) {
                halalGiftDetailAdapter.setReview(rating.getReview());

                halalGiftDetailAdapter.setRating(rating.getRating());

                DatabaseReference profileReference = FirebaseDatabase.getInstance().getReference().child("Profile").child(rating.getIdUser());

                profileReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            //halalGiftDetailAdapter.setNama(dataSnapshot.child("name").getValue().toString());
                            halalGiftDetailAdapter.setNama(dataSnapshot.child("name").getValue().toString());
                            halalGiftDetailAdapter.setImage(dataSnapshot.child("image").getValue().toString());
                        } catch (NullPointerException np) {
                            halalGiftDetailAdapter.setNama(rating.getNamaUser());
                            halalGiftDetailAdapter.setImage("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public HalalGiftDetailAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_review_user, parent, false);
                return new HalalGiftDetailAdapter(view);
            }

        };

        runOnUiThread(()->{
            LinearLayoutManager articleLayoutManager = new LinearLayoutManager(HalalGiftDetail.this, LinearLayoutManager.VERTICAL, false);
            listReviews.setLayoutManager(articleLayoutManager);
            listReviews.setAdapter(firebaseRecyclerAdapter);
        });

        getPlace(getIntent().getStringExtra("place_id"));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Places Review").child(getIntent().getStringExtra("place_id")).child(uId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    giftReview.setText(dataSnapshot.child("review").getValue().toString());
                    giftRatingBar.setRating(Float.parseFloat(dataSnapshot.child("rating").getValue().toString()));
                    giftReviewButton.setText("Edit");
                } catch (NullPointerException np) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        giftReviewButton.setOnClickListener((v -> {
            progressDialog.show();
            String review = giftReview.getText().toString();
            Rating rating = new Rating(uId, nama, review, Float.toString(giftRatingBar.getRating()), getIntent().getStringExtra("place_id"), giftName.getText().toString());
            databaseReference.setValue(rating).addOnSuccessListener(aVoid -> {
                databaseReference.child(uId).setValue(true);
                giftReviewButton.setText("Edit");
                progressDialog.dismiss();
            });
        }));

        // ini mustinya get apakah uda disave atau belum
        isSaved = false;

        buttonSave.setOnClickListener(v -> {
            if (isSaved) {
                isSaved = false;
                imageButtonSave.setImageDrawable(getDrawable(R.drawable.ic_save_blank));
            } else {
                isSaved = true;
                imageButtonSave.setImageDrawable(getDrawable(R.drawable.ic_save_filled));
            }
        });

        setSlideBackDirection(SlideBackActivity.LEFT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    private void getPlace(String placeId) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.PHOTO_METADATAS, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.RATING, Place.Field.ADDRESS);
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            Log.i(TAG, "Place found: " + place.getName());
            applyPlaceInfoToView(place);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                Log.e(TAG, "Place not found (ERROR[" + statusCode + "]): " + exception.getMessage());
            }
        });
    }

    private void applyPlaceInfoToView(Place giftPlace) {
        if (giftPlace != null) {
            if (giftPlace.getPhotoMetadatas() != null) {
                PhotoMetadata photoMetadata = giftPlace.getPhotoMetadatas().get(0);
                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxHeight(500) // Optional.
                        .build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    giftImage.setImageBitmap(bitmap);
                }).addOnFailureListener((exception) -> {
                    Log.e(TAG, exception.toString());
                });
            } else {
                LatLng location = giftPlace.getLatLng();
                Picasso.get().load("https://maps.googleapis.com/maps/api/streetview?size=500x300&location=" + location.latitude + "," + location.longitude
                        + "&fov=120&pitch=10&key=" + API_KEY)
                        .error(R.drawable.bg_loading_image)
                        .placeholder(R.drawable.bg_loading_image)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .into(giftImage);
            }

            giftName.setText(giftPlace.getName());
            giftDistance.setText(String.format("%.2f km", countDistance(giftPlace.getLatLng())));

            if (giftPlace.getRating() == null) {
                giftRating.setText("-");
            } else {
                giftRating.setText(String.format("%.1f", giftPlace.getRating()));
            }

            giftAddress.setText(giftPlace.getAddress());
        }
    }

    @Override
    protected void slideBackSuccess() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        Animatoo.animateSlideRight(this);
    }

    private void initializeUI() {
        giftImage = findViewById(R.id.gift_detail_image);
        userImage = findViewById(R.id.gift_detail_profile_image);
        giftName = findViewById(R.id.gift_detail_name);
        giftDistance = findViewById(R.id.gift_detail_distance);
        giftRating = findViewById(R.id.gift_detail_rating);
        giftAddress = findViewById(R.id.gift_detail_address);
        giftRatingBar = findViewById(R.id.gift_detail_rating_bar);
        giftReview = findViewById(R.id.gift_detail_review_edit_text);
        giftReviewButton = findViewById(R.id.gift_detail_submit_button);
        listReviews = findViewById(R.id.list_gift_detail_review_users);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching result...");
        progressDialog.setCancelable(false);

        buttonSave = findViewById(R.id.toggle_gift_save);
        imageButtonSave = findViewById(R.id.img_clicked);
    }

    private double countDistance(LatLng latLng) {
        LatLng currentLatLng = new LatLng(Double.parseDouble(getApplication().getSharedPreferences("location", MODE_PRIVATE).getString("Latitude", null)),
                Double.parseDouble(getApplication().getSharedPreferences("location", MODE_PRIVATE).getString("Longitude", null)));
        return Distance.distance(currentLatLng.latitude, latLng.latitude, currentLatLng.longitude, latLng.longitude);
    }

    public class HalalGiftDetailAdapter extends RecyclerView.ViewHolder {
        ImageView image;
        TextView nama;
        TextView review,rating2;

        public HalalGiftDetailAdapter(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profile_image);
            nama = itemView.findViewById(R.id.username_review);
            review = itemView.findViewById(R.id.review_user);
            rating2 = itemView.findViewById(R.id.rating);
        }

        public void setNama(String nama) {
            this.nama.setText(nama);
        }

        public void setImage(String foto) {
            Glide.with(HalalGiftDetail.this)
                    .load(foto)
                    .placeholder(R.drawable.ic_default_avatar)
                    .into(image);
        }

        public void setReview(String review) {
            this.review.setText(review);
        }

        public void setRating(String rating){
            this.rating2.setText(rating);
        }
    }

}
