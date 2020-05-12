package com.remu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.remu.POJO.User;
import com.remu.ui.main.FriendFragment;
import com.saber.chentianslideback.SlideBackActivity;

import java.util.Calendar;

public class UserProfileActivity extends SlideBackActivity {

    private TextView name, gender, age, about;
    private ImageView image;
    private RecyclerView friendList;
    private FirebaseRecyclerAdapter<User, UserProfileActivity.FriendListViewHolder> firebaseRecyclerAdapterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Animatoo.animateSlideLeft(this);

        initializeUI();
        initializeFriendList();

        name.setText(getIntent().getStringExtra("name").split(" ")[0]);
        gender.setText(getIntent().getStringExtra("gender"));
        age.setText(getIntent().getStringExtra("age"));
        about.setText(getIntent().getStringExtra("about"));
        Glide.with(UserProfileActivity.this)
                .load(getIntent().getStringExtra("image"))
                .centerCrop()
                .placeholder(R.drawable.ic_default_avatar)
                .into(image);

        setSlideBackDirection(SlideBackActivity.LEFT);
    }

    private void initializeUI(){
        name = findViewById(R.id.view_profile_name);
        age = findViewById(R.id.view_profile_age);
        gender = findViewById(R.id.view_profile_gender);
        about = findViewById(R.id.view_profile_about);
        image = findViewById(R.id.view_profile_image);
        friendList = findViewById(R.id.view_profile_friend_list);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapterList.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapterList.stopListening();
    }

    @Override
    protected void slideBackSuccess() {
        super.slideBackSuccess();

        finish();
    }

    @Override
    public void finish() {
        super.finish();

        Animatoo.animateSlideRight(this);
    }

    private void initializeFriendList(){
        LinearLayoutManager articleLayoutManager = new LinearLayoutManager(UserProfileActivity.this, LinearLayoutManager.HORIZONTAL, false);
        friendList.setLayoutManager(articleLayoutManager);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(getIntent().getStringExtra("id"));

        Query query = databaseReference.orderByChild(getIntent().getStringExtra("id")).equalTo(true);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class).build();


        firebaseRecyclerAdapterList = new FirebaseRecyclerAdapter<User, UserProfileActivity.FriendListViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull UserProfileActivity.FriendListViewHolder friendListViewHolder, int i, @NonNull User user) {
                DatabaseReference profileReference = FirebaseDatabase.getInstance().getReference().child("Profile").child(user.getId());
                profileReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        friendListViewHolder.setImage(dataSnapshot.child("image").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public UserProfileActivity.FriendListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_friend_in_profile, parent, false);
                return new UserProfileActivity.FriendListViewHolder(view);
            }
        };
        friendList.setAdapter(firebaseRecyclerAdapterList);
    }

    public class FriendListViewHolder extends RecyclerView.ViewHolder {
        ImageView image;


        FriendListViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.friend_profile_photo);
        }

        public void setImage(String foto) {
            Glide.with(UserProfileActivity.this)
                    .load(foto)
                    .centerCrop()
                    .placeholder(R.drawable.ic_default_avatar)
                    .into(image);
        }

    }
}
