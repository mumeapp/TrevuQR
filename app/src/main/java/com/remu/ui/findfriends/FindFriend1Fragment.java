package com.remu.ui.findfriends;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.remu.POJO.FragmentChangeListener;
import com.remu.R;

public class FindFriend1Fragment extends Fragment {

    private boolean isComplete = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_find_friend1, container, false);

        checkIsUserInfoComplete();

        Button buttonStart = root.findViewById(R.id.button_start_now);
        buttonStart.setOnClickListener(v -> {
            if (isComplete) {
                FragmentChangeListener fragmentChangeListener = (FragmentChangeListener) getActivity();
                assert fragmentChangeListener != null;
                fragmentChangeListener.replaceFragment(3);
            } else {
                FragmentChangeListener fragmentChangeListener = (FragmentChangeListener) getActivity();
                assert fragmentChangeListener != null;
                fragmentChangeListener.replaceFragment(2);
            }
        });

        return root;
    }

    private void checkIsUserInfoComplete() {
//        isComplete = true;
    }

}
