package com.remu;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.remu.POJO.User;
import com.remu.adapter.FriendAdapter;
import com.saber.chentianslideback.SlideBackActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FindFriendResultActivity extends SlideBackActivity {

    private LinearLayout friendEmpty;
    private RecyclerView friendList;
    private String getFriendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend_result);

        Animatoo.animateSlideLeft(this);

        setSlideBackDirection(SlideBackActivity.LEFT);

        initializeUI();

        if (getFriendList == null) {
            friendEmpty.setVisibility(View.VISIBLE);
        }
        else{
            Gson gson = new Gson();
            Type type = new TypeToken<List<User>>() {
            }.getType();
            ArrayList<User> userList = gson.fromJson(getFriendList, type);
            initializeFriendList(userList);
        }

    }

    private void initializeUI() {
        friendEmpty = findViewById(R.id.findfriends_empty);
        friendList = findViewById(R.id.friends_recyclerview);
        getFriendList = getIntent().getStringExtra("friendList");
    }

    private void initializeFriendList(ArrayList<User> userList) {
        LinearLayoutManager friendLayoutManager = new LinearLayoutManager(FindFriendResultActivity.this, LinearLayoutManager.VERTICAL, false);
        friendList.setLayoutManager(friendLayoutManager);
        RecyclerView.Adapter friendAdapter = new FriendAdapter(this.getApplication(), userList);
        friendList.setAdapter(friendAdapter);
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
}
