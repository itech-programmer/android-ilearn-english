
package uz.qubemelon.ilearn.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.services.CheckPermissionServices;
import uz.qubemelon.ilearn.ui.fragments.FragmentDictionary;
import uz.qubemelon.ilearn.ui.fragments.FragmentGames;
import uz.qubemelon.ilearn.ui.fragments.FragmentHome;
import uz.qubemelon.ilearn.ui.fragments.FragmentLeaderboard;
import uz.qubemelon.ilearn.ui.fragments.profile.FragmentProfile;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    /* all global field instances are here */
    private BottomNavigationView bottom_navigation_view;
    private Fragment current_fragment = null;
    private CheckPermissionServices permission_services = new CheckPermissionServices(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /* all permission need for this app */
        check_permissions();

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.background_accent_color));

        //* make the status bar transparent if version is above kitkat *//*
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);// SDK21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        /* type casting views */
        init_views();
        fragment_transition(new FragmentHome());
        /* removing shifting animation from bottom view */
//        Utility.remove_shift_mode(bottom_navigation_view);

        //BottomBar design with fragments start --------------------------------------------------------
        /* change the fragment on bottom bar item click */
        bottom_navigation_view.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_navigation_dictionary:
                    rest_mode();
                    /* replace with home Dictionary */
                    if (!(current_fragment instanceof FragmentDictionary)) {
                        current_fragment = new FragmentDictionary();
                    }
                    break;
                case R.id.bottom_navigation_leaderboard:
                    rest_mode();
                    /* replace with home Leaderboard */
                    if (!(current_fragment instanceof FragmentLeaderboard)) {
                        current_fragment = new FragmentLeaderboard();
                    }
                    break;
                case R.id.bottom_navigation_home:
                    rest_mode();
                    /* replace with home fragment */
                    if (!(current_fragment instanceof FragmentHome)) {
                        current_fragment = new FragmentHome();
                    }
                    break;
                case R.id.bottom_navigation_games:
                    rest_mode();
                    /* replace with home Games */
                    if (!(current_fragment instanceof FragmentGames)) {
                        current_fragment = new FragmentGames();
                    }
                    break;
                case R.id.bottom_navigation_profile:
                    profile_mode();
                    /* replace with profile fragment */
                    current_fragment = new FragmentProfile();
                    break;
            }
            /* do the main fragment transition */
            fragment_transition(current_fragment);
            return true;
        });
        //Used to select an item programmatically
        bottom_navigation_view.getMenu().getItem(2).setChecked(true);
        //BottomBar design with fragments end-----------------------------------------------------------
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    /* check if the app have all permissions */
    public static boolean has_permissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /* if user is in profile fragment make the status bar color blue */
    private void profile_mode() {
        getWindow().setStatusBarColor(getResources().getColor(R.color.bottom_navigation_selected_color));// SDK21
    }

    private void rest_mode() {
        /* make the status bar transparent if version is above kitkat */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.bottom_navigation_selected_color));// SDK21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    /* replace the current fragment with new fragment */
    public void fragment_transition(Fragment fragment) {
        this.current_fragment = fragment;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void check_permissions(){

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };

        if (!permission_services.checkPermissionForReadExternalStorage()) {
            permission_services.requestPermissionForReadExternalStorage();
        }

        if (!permission_services.checkPermissionForWriteExternalStorage()){
            permission_services.requestPermissionForWriteExternalStorage();
        }

        /* check if the there is permission that is needed */
        if (!has_permissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    /* type casting all the view */
    private void init_views() {
        bottom_navigation_view = findViewById(R.id.bottom_navigation);
    }
}