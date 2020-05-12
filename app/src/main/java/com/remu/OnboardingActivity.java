package com.remu;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.remu.POJO.FragmentChangeListener;

public class OnboardingActivity extends FragmentActivity implements FragmentChangeListener {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        Animatoo.animateSlideLeft(this);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_onboard);
    }

    @Override
    public void replaceFragment(int idAction) {
        switch (idAction) {
            case 1:
                navController.navigate(R.id.action_nav_onboard1_to_nav_onboard23);
                break;
            case 2:
                navController.navigate(R.id.action_nav_onboard1_to_nav_onboard3);
                break;
            case 3:
                navController.navigate(R.id.action_nav_onboard2_to_nav_onboard32);
                break;
        }
    }
}
