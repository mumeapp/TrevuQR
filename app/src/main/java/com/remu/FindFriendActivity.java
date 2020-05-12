package com.remu;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.remu.POJO.FragmentChangeListener;
import com.saber.chentianslideback.SlideBackActivity;

public class FindFriendActivity extends SlideBackActivity implements FragmentChangeListener {

    private NavController navController;
    private int currentView = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        Animatoo.animateSlideLeft(this);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_findfriends);

        setSlideBackDirection(SlideBackActivity.LEFT);
    }

    @Override
    protected void slideBackSuccess() {
        if (currentView != 3) {
            super.slideBackSuccess();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (currentView == 1) {
            finish();
        } else if (currentView == 2) {
            navController.navigate(R.id.nav_findfriend1);
        }
    }

    @Override
    public void finish() {
        super.finish();

        Animatoo.animateSlideRight(this);
    }

    @Override
    public void replaceFragment(int idFragment) {
        switch (idFragment) {
            case 1:
                navController.navigate(R.id.nav_findfriend1);
                currentView = 1;
                break;
            case 2:
                navController.navigate(R.id.nav_findfriend2);
                currentView = 2;
                break;
            case 3:
                navController.navigate(R.id.nav_findfriend3);
                currentView = 3;
                break;
        }
    }
}
