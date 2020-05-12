package com.remu.ui.findfriends;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;
import com.remu.FindFriendResultActivity;
import com.remu.POJO.Distance;
import com.remu.POJO.FragmentChangeListener;
import com.remu.POJO.User;
import com.remu.R;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class FindFriend3Fragment extends Fragment {

    private RippleBackground rippleBackground;

    private FragmentActivity mActivity;

    private ImageView profilePicture;
    private ArrayList<CardView> friendCard;
    private ArrayList<ImageView> friendPicture;
    private String latitude, longitude;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);

        latitude = requireActivity().getSharedPreferences("location", MODE_PRIVATE).getString("Latitude", null);
        longitude = requireActivity().getSharedPreferences("location", MODE_PRIVATE).getString("Longitude", null);

        if (context instanceof Activity) {
            mActivity = (FragmentActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_find_friend3, container, false);

        initializeUI(root);

        rippleBackground = root.findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

        Glide.with(FindFriend3Fragment.this)
                .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                .placeholder(R.drawable.ic_default_avatar)
                .centerCrop()
                .into(profilePicture);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User Location");
        databaseReference.orderByChild(FirebaseAuth.getInstance().getUid()).equalTo(null).addListenerForSingleValueEvent(new ValueEventListener() {

            int state = 0;
            long child = 0;
            int count1 = 0;
            boolean check = false;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> userList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String[] latlong = ds.child("latlong").getValue().toString().split(",");
                    ++state;
                    child = dataSnapshot.getChildrenCount();
                    if (ds.child("status").getValue().equals(true) && Distance.distance(Double.parseDouble(latlong[0]), Double.parseDouble(latitude), Double.parseDouble(latlong[1]), Double.parseDouble(longitude)) <= 10) {
                        String id = ds.child("userId").getValue().toString();
                        ++count1;
                        check =true;
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Profile").child(id);
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {
                                    userList.add(new User(id, dataSnapshot.child("image").getValue().toString(), dataSnapshot.child("birthdate").getValue().toString(), dataSnapshot.child("gender").getValue().toString(), dataSnapshot.child("name").getValue().toString()));
                                } catch (NullPointerException np) {
                                    --count1;
                                }
                                Gson gson = new Gson();
                                String friends = gson.toJson(userList);
                                int delay = 1000;
                                for (int i = 0; i < friendPicture.size(); i++) {
                                    if (i < userList.size()) {
                                        Glide.with(FindFriend3Fragment.this)
                                                .load(userList.get(i).getImage())
                                                .centerCrop()
                                                .placeholder(R.drawable.ic_default_avatar)
                                                .into(friendPicture.get(i));
                                        int finalI = i;
                                        new Handler().postDelayed(() -> friendCard.get(finalI).setVisibility(View.VISIBLE), delay);
                                        delay += 2000;
                                    }
                                }

                                if (userList.size()==count1) {
                                    new Handler().postDelayed(() -> {
                                        Intent intent = new Intent(mActivity, FindFriendResultActivity.class);
                                        intent.putExtra("friendList", friends);
                                        startActivity(intent);
                                        FragmentChangeListener fragmentChangeListener = (FragmentChangeListener) getActivity();
                                        assert fragmentChangeListener != null;
                                        fragmentChangeListener.replaceFragment(1);
                                        mActivity.finish();
                                    }, 6000);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                    else{
                        if (state == child&&!check) {
                            new Handler().postDelayed(() -> {
                                Intent intent = new Intent(mActivity, FindFriendResultActivity.class);
                                startActivity(intent);
                                FragmentChangeListener fragmentChangeListener = (FragmentChangeListener) getActivity();
                                assert fragmentChangeListener != null;
                                fragmentChangeListener.replaceFragment(1);
                                mActivity.finish();
                            }, 6000);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }

    private void initializeUI(View root) {
        profilePicture = root.findViewById(R.id.centerImage);
        friendPicture = new ArrayList<>();
        friendCard = new ArrayList<>();
        friendPicture.add(root.findViewById(R.id.friend1));
        friendPicture.add(root.findViewById(R.id.friend2));
        friendPicture.add(root.findViewById(R.id.friend3));
        friendCard.add(root.findViewById(R.id.friendCard1));
        friendCard.add(root.findViewById(R.id.friendCard2));
        friendCard.add(root.findViewById(R.id.friendCard3));
    }

    @Override
    public void onPause() {
        super.onPause();

        rippleBackground.stopRippleAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();

        rippleBackground.startRippleAnimation();
    }

}
