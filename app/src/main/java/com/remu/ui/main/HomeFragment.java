package com.remu.ui.main;

import android.content.Context;
import android.content.Intent;
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
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.remu.DictionaryActivity;
import com.remu.FindFriendActivity;
import com.remu.FoodActivity;
import com.remu.MosqueActivity;
import com.remu.POJO.Article;
import com.remu.POJO.PrayerTime;
import com.remu.R;
import com.remu.TourismActivity;
import com.takusemba.multisnaprecyclerview.MultiSnapHelper;
import com.takusemba.multisnaprecyclerview.SnapGravity;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private String latitude, longitude;

    private CardView mosqueCardView;
    private LinearLayout fnBButton, tourismButton, findFriendButton, dictionaryButton;
    private TextView userName;
    private RecyclerView listArticle;

    private FirebaseRecyclerAdapter<Article, HomeFragment.ArticleViewHolder> firebaseRecyclerAdapter;

    private NestedScrollView homeScrollView;

    private ShimmerFrameLayout jamSholatShimmerLoad, articleShimmerLoad;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        latitude = requireActivity().getSharedPreferences("location", MODE_PRIVATE).getString("Latitude", null);
        longitude = requireActivity().getSharedPreferences("location", MODE_PRIVATE).getString("Longitude", null);

        Log.e(TAG, "Latitude: " + latitude);
        Log.e(TAG, "Longitude: " + longitude);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        initializeShimmerLoad(root);
        initializeUI(root);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //set name
        getCurrentUser(currentUser);

        homeScrollView = root.findViewById(R.id.home_scroll);
        homeScrollView.post(() -> homeScrollView.scrollTo(0, 0));

        //go to mosque activity
        mosqueCardView.setOnClickListener(view -> {
            Intent viewMosque = new Intent(HomeFragment.super.getContext(), MosqueActivity.class);
            startActivity(viewMosque);
        });

        //go to food activity
        fnBButton.setOnClickListener(view -> {
            Intent viewFood = new Intent(HomeFragment.super.getContext(), FoodActivity.class);
            startActivity(viewFood);
        });

        //go to tourism activity
        tourismButton.setOnClickListener(view -> {
            Intent viewTour = new Intent(HomeFragment.super.getContext(), TourismActivity.class);
            startActivity(viewTour);
        });

        //go to tourism activity
        findFriendButton.setOnClickListener(view -> {
            Intent viewFindFriend = new Intent(HomeFragment.super.getContext(), FindFriendActivity.class);
            startActivity(viewFindFriend);
        });

        //go to Dictionary Activity
        dictionaryButton.setOnClickListener(view -> {
            Intent viewDictonary = new Intent(HomeFragment.super.getContext(), DictionaryActivity.class);
            startActivity(viewDictonary);
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        new Handler().postDelayed(() -> firebaseRecyclerAdapter.startListening(), 200);
    }

    @Override
    public void onPause() {
        jamSholatShimmerLoad.stopShimmer();
        articleShimmerLoad.stopShimmer();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        jamSholatShimmerLoad.startShimmer();
        articleShimmerLoad.startShimmer();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    private void initializeShimmerLoad(View root) {
        jamSholatShimmerLoad = root.findViewById(R.id.shimmer_load_jam_sholat);
        articleShimmerLoad = root.findViewById(R.id.shimmer_load_article);
    }

    private void initializeUI(View root) {
        // jam card
        mosqueCardView = root.findViewById(R.id.mosque_card_view);

        // menu
        fnBButton = root.findViewById(R.id.fnb_button);
        tourismButton = root.findViewById(R.id.tourism_button);
        findFriendButton = root.findViewById(R.id.findfriend_button);
        dictionaryButton = root.findViewById(R.id.dictionary_button);

        // hello guest
        userName = root.findViewById(R.id.nama);

        // explore
        listArticle = root.findViewById(R.id.list_article);
        initializeArticle();

        // tips

        // next prayer time
        ArrayList<TextView> textViews = new ArrayList<TextView>() {{
            add(root.findViewById(R.id.jam_solat_selanjutnya));
        }};
        new PrayerTime(this.getContext(), TAG, latitude, longitude, jamSholatShimmerLoad, textViews).execute();
    }

    private void initializeArticle() {
        LinearLayoutManager articleLayoutManager = new LinearLayoutManager(HomeFragment.this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        listArticle.setLayoutManager(articleLayoutManager);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Article");

        Query query = databaseReference.orderByKey();

        FirebaseRecyclerOptions<Article> options = new FirebaseRecyclerOptions.Builder<Article>()
                .setQuery(query, Article.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Article, ArticleViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ArticleViewHolder articleViewHolder, int i, @NonNull Article article) {
                articleViewHolder.setImage(article.getImage());
                articleViewHolder.setHighlight(article.getHighlight());
                articleViewHolder.setJudul(article.getTitle());

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
            public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_article, parent, false);
                return new ArticleViewHolder(view);
            }
        };
        new Handler().postDelayed(() -> {
            listArticle.setAdapter(firebaseRecyclerAdapter);
            articleShimmerLoad.stopShimmer();
            articleShimmerLoad.setVisibility(View.GONE);
        }, 1500);
        MultiSnapHelper multiSnapHelper = new MultiSnapHelper(SnapGravity.CENTER, 1, 100);
        multiSnapHelper.attachToRecyclerView(listArticle);
    }

    private void getCurrentUser(FirebaseUser user) {
        if (user != null) {
            String name = user.getDisplayName();
            userName.setText(name);
        }
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageView bookmark;
        TextView judul;
        TextView highlight;
        TextView explore;
        boolean isSaved;

        ArticleViewHolder(@NonNull View itemView) {
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
            Glide.with(HomeFragment.this)
                    .load(foto)
                    .placeholder(R.drawable.bg_loading_image)
                    .into(image);
        }

        void setHighlight(String waktu) {
            this.highlight.setText(waktu);
        }
    }

}
