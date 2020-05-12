package com.remu.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.remu.POJO.Article;
import com.remu.POJO.Distance;
import com.remu.POJO.SavedFoodBeveragesTour;
import com.remu.PlaceDetail;
import com.remu.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class SavedFragment extends Fragment {

    private LinearLayout savedEmpty;
    private RecyclerView listArticle, listFood, listTour, listBeverages;
    private TextView articleText, foodText, tourText, textBeverages;
    private String latitude, longitude;
    private FirebaseRecyclerAdapter<Article, SavedFragment.SavedArticleViewHolder> firebaseRecyclerAdapterArticle;
    private FirebaseRecyclerAdapter<SavedFoodBeveragesTour, SavedFragment.SavedFoodBeveragesTourViewHolder> firebaseRecyclerAdapterFood, firebaseRecyclerAdapterTour, firebaseRecyclerAdapterBeverages;

    //TODO: DELETE WHEN UPLOADING OR DOCUMENTING!
    final private String API_KEY = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_saved, container, false);

        initializeUI(root);

        latitude = requireActivity().getSharedPreferences("location", MODE_PRIVATE).getString("Latitude", null);
        longitude = requireActivity().getSharedPreferences("location", MODE_PRIVATE).getString("Longitude", null);

        initializeArticle();
        initializeHalalFood();
        initializeTour();
        initializeBeverages();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Saved").child(FirebaseAuth.getInstance().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    savedEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        final Handler handler = new Handler();
        handler.postDelayed(() -> firebaseRecyclerAdapterArticle.startListening(), 200);
        handler.postDelayed(() -> firebaseRecyclerAdapterFood.startListening(), 400);
        handler.postDelayed(() -> firebaseRecyclerAdapterTour.startListening(), 800);
        handler.postDelayed(() -> firebaseRecyclerAdapterBeverages.startListening(), 1200);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapterArticle.stopListening();
        firebaseRecyclerAdapterFood.stopListening();
        firebaseRecyclerAdapterTour.stopListening();
        firebaseRecyclerAdapterBeverages.stopListening();
    }

    private void initializeBeverages() {
        LinearLayoutManager beveragesLayoutManager = new LinearLayoutManager(SavedFragment.this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        listBeverages.setLayoutManager(beveragesLayoutManager);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Saved").child(FirebaseAuth.getInstance().getUid()).child("HalalBeverages");

        Query query = databaseReference.orderByKey();

        FirebaseRecyclerOptions<SavedFoodBeveragesTour> options = new FirebaseRecyclerOptions.Builder<SavedFoodBeveragesTour>()
                .setQuery(query, SavedFoodBeveragesTour.class).build();
        firebaseRecyclerAdapterBeverages = new FirebaseRecyclerAdapter<SavedFoodBeveragesTour, SavedFoodBeveragesTourViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SavedFoodBeveragesTourViewHolder savedFoodViewHolder, int i, @NonNull SavedFoodBeveragesTour savedFoodBeveragesTour) {
                savedEmpty.setVisibility(View.GONE);
                textBeverages.setVisibility(View.VISIBLE);
                savedFoodViewHolder.setImage(savedFoodBeveragesTour.getId());
                String[] latlong = savedFoodBeveragesTour.getLatlong().split(",");
                savedFoodViewHolder.setDistance(Distance.distance(Double.parseDouble(latitude), Double.parseDouble(latlong[0]), Double.parseDouble(longitude), Double.parseDouble(latlong[1])));
                savedFoodViewHolder.setRating(savedFoodBeveragesTour.getRating() + "");
                savedFoodViewHolder.setTitle(savedFoodBeveragesTour.getTitle());

                savedFoodViewHolder.cardView.setOnClickListener(view -> {
                    Intent intent = new Intent(getActivity(), PlaceDetail.class);
                    intent.putExtra("sender", "HalalBeverages");
                    intent.putExtra("place_id", savedFoodBeveragesTour.getId());
                    startActivity(intent);
                });

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            textBeverages.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public SavedFoodBeveragesTourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_horizontal_mode, parent, false);

                return new SavedFragment.SavedFoodBeveragesTourViewHolder(view);
            }
        };
        listBeverages.setAdapter(firebaseRecyclerAdapterBeverages);
    }

    private void initializeTour() {
        LinearLayoutManager tourLayoutManager = new LinearLayoutManager(SavedFragment.this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        listTour.setLayoutManager(tourLayoutManager);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Saved").child(FirebaseAuth.getInstance().getUid()).child("Tourism");

        Query query = databaseReference.orderByKey();

        FirebaseRecyclerOptions<SavedFoodBeveragesTour> options = new FirebaseRecyclerOptions.Builder<SavedFoodBeveragesTour>()
                .setQuery(query, SavedFoodBeveragesTour.class).build();
        firebaseRecyclerAdapterTour = new FirebaseRecyclerAdapter<SavedFoodBeveragesTour, SavedFoodBeveragesTourViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SavedFoodBeveragesTourViewHolder savedFoodViewHolder, int i, @NonNull SavedFoodBeveragesTour savedFoodBeveragesTour) {
                savedEmpty.setVisibility(View.GONE);
                tourText.setVisibility(View.VISIBLE);
                savedFoodViewHolder.setImage(savedFoodBeveragesTour.getId());
                String[] latlong = savedFoodBeveragesTour.getLatlong().split(",");
                savedFoodViewHolder.setDistance(Distance.distance(Double.parseDouble(latitude), Double.parseDouble(latlong[0]), Double.parseDouble(longitude), Double.parseDouble(latlong[1])));
                savedFoodViewHolder.setRating(savedFoodBeveragesTour.getRating() + "");
                savedFoodViewHolder.setTitle(savedFoodBeveragesTour.getTitle());
                savedFoodViewHolder.cardView.setOnClickListener(view -> {
                    Intent intent = new Intent(getActivity(), PlaceDetail.class);
                    intent.putExtra("sender", "Tourism");
                    intent.putExtra("place_id", savedFoodBeveragesTour.getId());
                    startActivity(intent);
                });
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            tourText.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public SavedFoodBeveragesTourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_horizontal_mode, parent, false);

                return new SavedFragment.SavedFoodBeveragesTourViewHolder(view);
            }
        };
        listTour.setAdapter(firebaseRecyclerAdapterTour);
    }

    private void initializeHalalFood() {
        LinearLayoutManager foodLayoutManager = new LinearLayoutManager(SavedFragment.this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        listFood.setLayoutManager(foodLayoutManager);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Saved").child(FirebaseAuth.getInstance().getUid()).child("HalalFood");

        Query query = databaseReference.orderByKey();

        FirebaseRecyclerOptions<SavedFoodBeveragesTour> options = new FirebaseRecyclerOptions.Builder<SavedFoodBeveragesTour>()
                .setQuery(query, SavedFoodBeveragesTour.class).build();
        firebaseRecyclerAdapterFood = new FirebaseRecyclerAdapter<SavedFoodBeveragesTour, SavedFoodBeveragesTourViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SavedFoodBeveragesTourViewHolder savedFoodViewHolder, int i, @NonNull SavedFoodBeveragesTour savedFoodBeveragesTour) {
                savedEmpty.setVisibility(View.GONE);
                foodText.setVisibility(View.VISIBLE);
                savedFoodViewHolder.setImage(savedFoodBeveragesTour.getId());
                String[] latlong = savedFoodBeveragesTour.getLatlong().split(",");
                savedFoodViewHolder.setDistance(Distance.distance(Double.parseDouble(latitude), Double.parseDouble(latlong[0]), Double.parseDouble(longitude), Double.parseDouble(latlong[1])));
                savedFoodViewHolder.setRating(savedFoodBeveragesTour.getRating() + "");
                savedFoodViewHolder.setTitle(savedFoodBeveragesTour.getTitle());
                savedFoodViewHolder.cardView.setOnClickListener(view -> {
                    Intent intent = new Intent(getActivity(), PlaceDetail.class);
                    intent.putExtra("sender", "HalalFood");
                    intent.putExtra("place_id", savedFoodBeveragesTour.getId());
                    startActivity(intent);
                });
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            foodText.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public SavedFoodBeveragesTourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_horizontal_mode, parent, false);

                return new SavedFragment.SavedFoodBeveragesTourViewHolder(view);
            }
        };
        listFood.setAdapter(firebaseRecyclerAdapterFood);
    }

    private void initializeArticle() {
        LinearLayoutManager articleLayoutManager = new LinearLayoutManager(SavedFragment.this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        listArticle.setLayoutManager(articleLayoutManager);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Saved").child(FirebaseAuth.getInstance().getUid()).child("Article");

        Query query = databaseReference.orderByKey();

        FirebaseRecyclerOptions<Article> options = new FirebaseRecyclerOptions.Builder<Article>()
                .setQuery(query, Article.class).build();


        firebaseRecyclerAdapterArticle = new FirebaseRecyclerAdapter<Article, SavedFragment.SavedArticleViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull SavedFragment.SavedArticleViewHolder articleViewHolder, int i, @NonNull Article article) {
                savedEmpty.setVisibility(View.GONE);
                articleText.setVisibility(View.VISIBLE);

                articleViewHolder.setImage(article.getImage());
                articleViewHolder.setHighlight(article.getHighlight());
                articleViewHolder.setJudul(article.getTitle());

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            articleText.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                DatabaseReference saved = FirebaseDatabase.getInstance().getReference().child("Saved").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("Article").child(article.getTitle());
                saved.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot.child(article.getTitle()).getValue().equals(true)) {
                                articleViewHolder.bookmark.setImageDrawable(getActivity().getDrawable(R.drawable.ic_bookmark_fill_black_24dp));
                                articleViewHolder.bookmark.setOnClickListener(view -> {
                                    saved.removeValue();
                                    articleViewHolder.bookmark.setImageDrawable(getActivity().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
                                });
                            }
                        } catch (NullPointerException np) {
                            articleViewHolder.bookmark.setImageDrawable(getActivity().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
                            articleViewHolder.bookmark.setOnClickListener(view -> {
                                saved.child(article.getTitle()).setValue(true);
                                saved.child("highlight").setValue(article.getHighlight());
                                saved.child("image").setValue(article.getImage());
                                saved.child("source").setValue(article.getSource());
                                saved.child("title").setValue(article.getTitle());
                                articleViewHolder.bookmark.setImageDrawable(getActivity().getDrawable(R.drawable.ic_bookmark_fill_black_24dp));
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                articleViewHolder.explore.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(article.getSource()));
                    startActivity(intent);
                });

            }


            @NonNull
            @Override
            public SavedFragment.SavedArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_article, parent, false);

                return new SavedFragment.SavedArticleViewHolder(view);
            }

        };

        listArticle.setAdapter(firebaseRecyclerAdapterArticle);
    }

    private void initializeUI(View root) {
        savedEmpty = root.findViewById(R.id.saved_empty);
        listArticle = root.findViewById(R.id.ac_recyclerview);
        articleText = root.findViewById(R.id.article_text);
        listFood = root.findViewById(R.id.fc_recyclerview);
        foodText = root.findViewById(R.id.food_text);
        listTour = root.findViewById(R.id.toc_recyclerview);
        tourText = root.findViewById(R.id.tour_text);
        listBeverages = root.findViewById(R.id.bc_recyclerview);
        textBeverages = root.findViewById(R.id.beverages_text);
    }

    public class SavedArticleViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageView bookmark;
        TextView judul;
        TextView highlight;
        TextView explore;
        boolean isSaved;

        SavedArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.img_article);
            bookmark = itemView.findViewById(R.id.bookmark_article);
            judul = itemView.findViewById(R.id.title_article);
            highlight = itemView.findViewById(R.id.highlight_article);
            explore = itemView.findViewById(R.id.explore_article);
            isSaved = false;
        }

        void setJudul(String judul) {
            this.judul.setText(judul);
        }

        void setImage(String foto) {
            Glide.with(SavedFragment.this)
                    .load(foto)
                    .placeholder(R.drawable.bg_loading_image)
                    .into(image);
        }

        void setHighlight(String waktu) {
            this.highlight.setText(waktu);
        }
    }

    public class SavedFoodBeveragesTourViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, distance, rating;
        CardView cardView;

        SavedFoodBeveragesTourViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.food_image);
            title = itemView.findViewById(R.id.food_name);
            distance = itemView.findViewById(R.id.food_distance);
            rating = itemView.findViewById(R.id.food_rating);
            cardView = itemView.findViewById(R.id.food_card);
        }

        void setTitle(String title) {
            this.title.setText(title);
        }

        @SuppressLint("SetTextI18n")
        void setDistance(double distance) {
            DecimalFormat df = new DecimalFormat("#.##");
            this.distance.setText(df.format(distance) + " Km");
        }

        void setRating(String rating) {
            this.rating.setText(rating);
        }

        void setImage(String id) {
            List<Place.Field> placeFields = Arrays.asList(Place.Field.PHOTO_METADATAS);
            FetchPlaceRequest request = FetchPlaceRequest.newInstance(id, placeFields);
            if (!Places.isInitialized()) {
                Places.initialize(getActivity(), getString(R.string.API_KEY));
            }
            PlacesClient placesClient;
            placesClient = Places.createClient(requireActivity());
            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                Place place = response.getPlace();
                if (place.getPhotoMetadatas() != null) {
                    PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
                    Places.initialize(getActivity(), getString(R.string.API_KEY));
                    PlacesClient placesClient1 = Places.createClient(getActivity());
                    FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                            .setMaxHeight(750)
                            .build();
                    placesClient1.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                        Bitmap bitmap = fetchPhotoResponse.getBitmap();
                        image.setImageBitmap(bitmap);
                    }).addOnFailureListener((exception) -> Log.e("SavedFragment", exception.toString()));
                } else {
                    LatLng location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    Picasso.get().load("https://maps.googleapis.com/maps/api/streetview?size=500x300&location=" + location.latitude + "," + location.longitude
                            + "&fov=120&pitch=10&key="+ API_KEY)
                            .error(R.drawable.bg_loading_image)
                            .placeholder(R.drawable.bg_loading_image)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .into(image);
                }
            });
        }

    }

}
