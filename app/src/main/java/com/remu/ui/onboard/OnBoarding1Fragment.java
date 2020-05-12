package com.remu.ui.onboard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.remu.POJO.FragmentChangeListener;
import com.remu.R;

import org.jetbrains.annotations.NotNull;

public class OnBoarding1Fragment extends Fragment {

    private TextView skipOption;
    private Button continueTo2;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_onboard1, container, false);

        initializeUI(root);

        skipOption.setOnClickListener(v -> {
            FragmentChangeListener fragmentChangeListener = (FragmentChangeListener) getActivity();
            assert fragmentChangeListener != null;
            fragmentChangeListener.replaceFragment(2);
        });

        continueTo2.setOnClickListener(v -> {
            FragmentChangeListener fragmentChangeListener = (FragmentChangeListener) getActivity();
            assert fragmentChangeListener != null;
            fragmentChangeListener.replaceFragment(1);
        });
        return root;
    }

    private void initializeUI(View root) {
        skipOption = root.findViewById(R.id.option_skip);
        continueTo2 = root.findViewById(R.id.button_1_to_2);
    }

}
