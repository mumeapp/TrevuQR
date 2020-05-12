package com.remu.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.remu.FindFriendActivity;
import com.remu.POJO.User;
import com.remu.QRActivity;
import com.remu.R;
import com.remu.UserProfileActivity;

import java.util.Calendar;

public class FriendFragment extends Fragment {
    private Button btnFindFriend, btnShowQR;
    private LinearLayout friendEmpty;
    private RecyclerView friendRequest, friendList, pendingFriendRequest;
    private TextView friendRequestTxt, friendListTxt, pendingFriendTxt;
    private FirebaseRecyclerAdapter<User, FriendFragment.FriendRequestViewHolder> firebaseRecyclerAdapterrequest;
    private FirebaseRecyclerAdapter<User, FriendFragment.PendingFriendRequestViewHolder> firebaseRecyclerAdapterrequest2;
    private FirebaseRecyclerAdapter<User, FriendFragment.FriendListViewHolder> firebaseRecyclerAdapterList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friend, container, false);

        initializeUI(root);
        friendEmpty.setVisibility(View.VISIBLE);
        String userId = FirebaseAuth.getInstance().getUid();

        initializePendingFriendRequest();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String testCount = Long.toString(dataSnapshot.getChildrenCount());
                System.out.println("CURRENT CHILD NUMBER: " + testCount);

                if (dataSnapshot.getChildrenCount() != 0) {
                    friendEmpty.setVisibility(View.GONE);
                } else {
                    friendEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        initializeFriendRequest();
        initializeFriendList();


        //set dest for btn friends
        btnFindFriend.setOnClickListener(view -> {
            Intent viewFindFriend = new Intent(getActivity(), FindFriendActivity.class);
            startActivity(viewFindFriend);
        });

        btnShowQR.setOnClickListener(view -> {
            Intent QRcode = new Intent(getActivity(), QRActivity.class);
            startActivity(QRcode);
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapterrequest.startListening();
        firebaseRecyclerAdapterList.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapterrequest.stopListening();
        firebaseRecyclerAdapterList.stopListening();

    }

    private void initializeUI(View root) {
        btnFindFriend = root.findViewById(R.id.btn_find_more);
        friendRequest = root.findViewById(R.id.friends_request_recyclerview);
        friendList = root.findViewById(R.id.friends_recyclerview);
        friendEmpty = root.findViewById(R.id.friends_empty);
        friendListTxt = root.findViewById(R.id.friend_list_text);
        friendRequestTxt = root.findViewById(R.id.friend_request_text);
        btnShowQR = root.findViewById(R.id.btn_show_qr);
        pendingFriendRequest = root.findViewById(R.id.pending_friends_request_recyclerview);
        pendingFriendTxt = root.findViewById(R.id.pending_friends_request_text);
    }

    private void initializeFriendList() {
        LinearLayoutManager articleLayoutManager = new LinearLayoutManager(FriendFragment.this.getContext(), LinearLayoutManager.VERTICAL, false);
        friendList.setLayoutManager(articleLayoutManager);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(FirebaseAuth.getInstance().getUid());

        Query query = databaseReference.orderByChild(FirebaseAuth.getInstance().getUid()).equalTo(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    friendListTxt.setVisibility(View.GONE);
                } else {
                    friendListTxt.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class).build();


        firebaseRecyclerAdapterList = new FirebaseRecyclerAdapter<User, FriendListViewHolder>(options) {

            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull FriendFragment.FriendListViewHolder friendRequestViewHolder, int i, @NonNull User user) {
                friendRequestViewHolder.addFriend.setText("View Profile");
                DatabaseReference profileReference = FirebaseDatabase.getInstance().getReference().child("Profile").child(user.getId());
                profileReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        friendRequestViewHolder.setName(dataSnapshot.child("name").getValue().toString());
                        friendRequestViewHolder.setGender(dataSnapshot.child("gender").getValue().toString());
                        friendRequestViewHolder.setImage(dataSnapshot.child("image").getValue().toString());
                        String[] birthDate = dataSnapshot.child("birthdate").getValue().toString().split(" ");
                        switch (birthDate[1]) {
                            case "January":
                                birthDate[1] = "0";
                                break;
                            case "February":
                                birthDate[1] = "1";
                                break;
                            case "March":
                                birthDate[1] = "2";
                                break;
                            case "April":
                                birthDate[1] = "3";
                                break;
                            case "Mey":
                                birthDate[1] = "4";
                                break;
                            case "June":
                                birthDate[1] = "5";
                                break;
                            case "July":
                                birthDate[1] = "6";
                                break;
                            case "August":
                                birthDate[1] = "7";
                                break;
                            case "September":
                                birthDate[1] = "8";
                                break;
                            case "October":
                                birthDate[1] = "9";
                                break;
                            case "November":
                                birthDate[1] = "10";
                                break;
                            case "December":
                                birthDate[1] = "11";
                                break;
                        }
                        Calendar calendar = Calendar.getInstance();
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int month = calendar.get(Calendar.MONTH);
                        int year = calendar.get(Calendar.YEAR);

                        int age;
                        if (month > Integer.parseInt(birthDate[1])) {
                            age = year - Integer.parseInt(birthDate[2]);
                        } else if (month == Integer.parseInt(birthDate[1]) && day >= Integer.parseInt(birthDate[0])) {
                            age = year - Integer.parseInt(birthDate[2]);
                        } else {
                            age = year - Integer.parseInt(birthDate[2]) - 1;
                        }
                        friendRequestViewHolder.addFriend.setOnClickListener(view -> {
                            Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                            intent.putExtra("id", user.getId());
                            intent.putExtra("name", dataSnapshot.child("name").getValue().toString());
                            intent.putExtra("gender", dataSnapshot.child("gender").getValue().toString());
                            intent.putExtra("image", dataSnapshot.child("image").getValue().toString());
                            intent.putExtra("age", age + "");
                            intent.putExtra("about", dataSnapshot.child("about").getValue().toString());
                            startActivity(intent);
                        });
                        friendRequestViewHolder.setAge(Integer.toString(age));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public FriendFragment.FriendListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_friend, parent, false);
                return new FriendFragment.FriendListViewHolder(view);
            }
        };
        friendList.setAdapter(firebaseRecyclerAdapterList);
    }

    private void initializeFriendRequest() {
        LinearLayoutManager articleLayoutManager = new LinearLayoutManager(FriendFragment.this.getContext(), LinearLayoutManager.VERTICAL, false);
        friendRequest.setLayoutManager(articleLayoutManager);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(FirebaseAuth.getInstance().getUid());

        Query query = databaseReference.orderByChild(FirebaseAuth.getInstance().getUid()).equalTo(false);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    friendRequestTxt.setVisibility(View.GONE);
                } else {
                    friendRequestTxt.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class).build();

        firebaseRecyclerAdapterrequest = new FirebaseRecyclerAdapter<User, FriendFragment.FriendRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendFragment.FriendRequestViewHolder friendRequestViewHolder, int i, @NonNull User user) {
                DatabaseReference friendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(user.getId()).child(FirebaseAuth.getInstance().getUid());
                friendRequestViewHolder.setName(user.getName());
                friendRequestViewHolder.setImage(user.getImage());
                friendRequestViewHolder.btnAccept.setOnClickListener(view -> {
                    databaseReference.child(user.getId()).child(FirebaseAuth.getInstance().getUid()).setValue(true);
                    friendDatabase.child(user.getId()).setValue(true);
                });
                friendRequestViewHolder.btnDecline.setOnClickListener(view -> {
                    databaseReference.child(user.getId()).removeValue();
                    friendDatabase.removeValue();
                });

            }

            @NonNull
            @Override
            public FriendFragment.FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_friend_request, parent, false);
                return new FriendFragment.FriendRequestViewHolder(view);
            }
        };
        friendRequest.setAdapter(firebaseRecyclerAdapterrequest);
    }

    private void initializePendingFriendRequest() {
        LinearLayoutManager articleLayoutManager = new LinearLayoutManager(FriendFragment.this.getContext(), LinearLayoutManager.VERTICAL, false);
        pendingFriendRequest.setLayoutManager(articleLayoutManager);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Friends");

        Query query = databaseReference.orderByChild(FirebaseAuth.getInstance().getUid()).equalTo(false);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                System.out.println(dataSnapshot);

                if (dataSnapshot.getChildrenCount() == 0) {
                    pendingFriendTxt.setVisibility(View.GONE);
                } else {
                    pendingFriendTxt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public class FriendRequestViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, btnAccept, btnDecline;

        FriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.friend_photo);
            name = itemView.findViewById(R.id.friend_request_name);
            btnAccept = itemView.findViewById(R.id.button_accept);
            btnDecline = itemView.findViewById(R.id.button_decline);
        }

        public void setName(String name) {
            this.name.setText(name);
        }

        public void setImage(String foto) {
            Glide.with(FriendFragment.this)
                    .load(foto)
                    .placeholder(R.drawable.ic_default_avatar)
                    .into(image);
        }
    }

    public class PendingFriendRequestViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, txtPending;

        PendingFriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.friend_photo);
            name = itemView.findViewById(R.id.friend_request_name);
        }

        public void setName(String name) {
            this.name.setText(name);
        }

        public void setImage(String foto) {
            Glide.with(FriendFragment.this)
                    .load(foto)
                    .placeholder(R.drawable.ic_default_avatar)
                    .into(image);
        }
    }

    public class FriendListViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, gender, age, addFriend;


        FriendListViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.friend_photo);
            name = itemView.findViewById(R.id.friend_name);
            gender = itemView.findViewById(R.id.friend_gender);
            age = itemView.findViewById(R.id.friend_age);
            addFriend = itemView.findViewById(R.id.button_add_friend);
        }

        public void setName(String name) {
            this.name.setText(name);
        }

        public void setImage(String foto) {
            Glide.with(FriendFragment.this)
                    .load(foto)
                    .placeholder(R.drawable.ic_default_avatar)
                    .into(image);
        }

        void setAge(String age) {
            this.age.setText(age);
        }

        void setGender(String gender) {
            this.gender.setText(gender);
        }
    }

}
