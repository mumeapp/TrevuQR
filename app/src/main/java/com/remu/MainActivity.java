package com.remu;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ID_HOME = 0;
    private static final int ID_SAVED = 1;
    private static final int ID_FRIENDS = 2;
    private static final int ID_PROFILE = 3;

    private NavController navController;
    private BubbleNavigationConstraintView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Animatoo.animateSlideLeft(this);

        navController = Navigation.findNavController(this, R.id.main_nav_host_fragment);

        navView = findViewById(R.id.nav_view);
        navView.setTypeface(ResourcesCompat.getFont(this, R.font.geomanistregular));
        navView.setNavigationChangeListener((view, position) -> {
            switch (position) {
                case ID_HOME:
                    navController.navigate(R.id.navigation_home);
                    break;
                case ID_SAVED:
                    navController.navigate(R.id.navigation_saved);
                    break;
                case ID_FRIENDS:
                    navController.navigate(R.id.navigation_friends);
                    break;
                case ID_PROFILE:
                    navController.navigate(R.id.navigation_profile);
                    break;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (navView.getCurrentActiveItemPosition() == ID_HOME) {
            finish();
        } else {
            navView.setCurrentActiveItem(ID_HOME);
        }
    }
}
