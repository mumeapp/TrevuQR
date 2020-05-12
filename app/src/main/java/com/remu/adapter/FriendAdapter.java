package com.remu.adapter;

import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.remu.POJO.User;
import com.remu.R;

import java.util.ArrayList;
import java.util.Calendar;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private ArrayList<User> mDataset;
    private Application app;

    public FriendAdapter(Application app, ArrayList<User> mDataset) {
        this.app = app;
        this.mDataset = mDataset;
    }

    @NonNull
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_friend, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FriendAdapter.ViewHolder holder, int position) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(FirebaseAuth.getInstance().getUid()).child(mDataset.get(position).getId()).child("id");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    holder.addFriend.setVisibility(View.GONE);
                }
                else{
                    holder.addFriend.setOnClickListener(view -> {
                        DatabaseReference friendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mDataset.get(position).getId()).child(FirebaseAuth.getInstance().getUid());
                        friendDatabase.child(mDataset.get(position).getId()).setValue(false);
                        friendDatabase.child("id").setValue(FirebaseAuth.getInstance().getUid());
                        friendDatabase.child("image").setValue(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
                        friendDatabase.child("name").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                        DatabaseReference yourDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(FirebaseAuth.getInstance().getUid()).child(mDataset.get(position).getId());
                        yourDatabase.child("id").setValue(mDataset.get(position).getId());
                        yourDatabase.child("image").setValue(mDataset.get(position).getImage());
                        yourDatabase.child("name").setValue(mDataset.get(position).getName());

                        holder.addFriend.setText("Request sent");
                        holder.addFriend.setTextColor(Color.GRAY);
                        holder.addFriend.setOnClickListener(view1 -> {

                        });
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.name.setText(mDataset.get(position).getName());

        Glide.with(app.getApplicationContext())
                .load(mDataset.get(position).getImage())
                .placeholder(R.drawable.ic_default_avatar)
                .into(holder.image);
        String[] birthDate = mDataset.get(position).getTanggal().split(" ");
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
            case "May":
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
        holder.age.setText(Integer.toString(age));
        holder.gender.setText(mDataset.get(position).getGender());

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, gender, age, addFriend;


        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.friend_photo);
            name = itemView.findViewById(R.id.friend_name);
            gender = itemView.findViewById(R.id.friend_gender);
            age = itemView.findViewById(R.id.friend_age);
            addFriend = itemView.findViewById(R.id.button_add_friend);
        }
    }

}
