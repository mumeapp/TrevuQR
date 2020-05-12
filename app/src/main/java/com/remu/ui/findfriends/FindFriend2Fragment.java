package com.remu.ui.findfriends;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.annotations.NotNull;
import com.remu.ChangeProfileActivity;
import com.remu.POJO.FragmentChangeListener;
import com.remu.R;

public class FindFriend2Fragment extends Fragment {

    private boolean isComplete = true;

    private FragmentActivity mActivity;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            mActivity = (FragmentActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_find_friend2, container, false);

        Button buttonComplete = root.findViewById(R.id.button_complete_profile);
        buttonComplete.setOnClickListener(v -> {
//            Intent intent = new Intent(mActivity, ChangeProfileActivity.class);
//            startActivity(intent);

            checkIsUserInfoComplete();
        });

        return root;
    }

    private void checkIsUserInfoComplete() {
        if (isComplete) {
            FragmentChangeListener fragmentChangeListener = (FragmentChangeListener) getActivity();
            assert fragmentChangeListener != null;
            fragmentChangeListener.replaceFragment(3);
        }
    }

}
