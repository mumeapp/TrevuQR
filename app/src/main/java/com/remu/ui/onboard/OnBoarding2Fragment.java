package com.remu.ui.onboard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.remu.POJO.FragmentChangeListener;
import com.remu.R;

public class OnBoarding2Fragment extends Fragment {

    private Button continueTo3;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_onboard2, container, false);

        initializeUI(root);

        continueTo3.setOnClickListener(v -> {
            FragmentChangeListener fragmentChangeListener = (FragmentChangeListener) getActivity();
            assert fragmentChangeListener != null;
            fragmentChangeListener.replaceFragment(3);
        });
        return root;
    }

    private void initializeUI(View root) {
        continueTo3 = root.findViewById(R.id.button_2_to_3);
    }

}
