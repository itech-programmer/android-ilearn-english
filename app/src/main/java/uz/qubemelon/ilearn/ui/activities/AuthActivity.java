package uz.qubemelon.ilearn.ui.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import uz.qubemelon.ilearn.R;

public class AuthActivity extends AppCompatActivity {

    /* all global field instances here */
    Fragment current_fragment;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);// SDK21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        /* the first is opened for first time */
//        Storage storage = new Storage(this);
//        storage.save_is_first_time(true);
//
//        /* make log in fragment as default */
//        fragmentTransition(new FragmentSignIn());
    }

    /* do fragment replace with new fragment */
    public void fragmentTransition(Fragment fragment) {
        this.current_fragment = fragment;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.auth_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
//        if(current_fragment instanceof FragmentSignUp) {
//            this.fragmentTransition(new FragmentSignIn());
//        } else if (current_fragment instanceof FragmentSignIn) {
//            count++;
//            if (count == 1) {
//                Toast.makeText(this, "Press again to exit!", Toast.LENGTH_SHORT).show();
//            } else if (count == 2) {
//                super.onBackPressed();
//            }
//        }
    }
}
