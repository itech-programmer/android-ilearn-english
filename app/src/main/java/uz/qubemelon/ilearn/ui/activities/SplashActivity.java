package uz.qubemelon.ilearn.ui.activities;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.rd.PageIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.adapters.ImageSliderAdapter;
import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.models.APIResponse;
import uz.qubemelon.ilearn.network.ErrorHandler;
import uz.qubemelon.ilearn.network.RetrofitClient;
import uz.qubemelon.ilearn.network.RetrofitInterface;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    /* all global field instances are here */
    private TextView text_splash_skip, text_splash_title, text_splash_description;
    private ViewPager view_pager_splash;
    private PageIndicatorView page_indicator_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //*make the status bar transparent if version is above kitkat *//

        /*make the status bar transparent*/

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);// SDK21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        Storage storage = new Storage(this);
        if(storage.get_is_first_time()){
            skipping_works();
        }

        /* view type casting */
        init_views();

        /*make list of on boarding image here*/

        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.image_splash_blue);
        imageList.add(R.drawable.image_splash_purple);
        imageList.add(R.drawable.image_splash_orange);

        /* build the image adapter for splash view pager */
        ImageSliderAdapter image_slider_adapter = new ImageSliderAdapter(imageList, this);

        /* attach the adapter to the view pager */
        view_pager_splash.setAdapter(image_slider_adapter);

        page_indicator_view.setViewPager(view_pager_splash);

        /* set count viewpager indicator */
        page_indicator_view.setCount(3);

        /* set indicator the current position from the viewpager */
        page_indicator_view.setSelection(view_pager_splash.getCurrentItem());

        /*view pager page change listener*/
        view_pager_splash.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                page_indicator_view.setSelection(position);
                switch (position) {
                    case 0:
                        /* animate the text */
                        set_text_splash_animator();
                        break;
                    case 1:
                        /* animate the text */
                        set_text_splash_animator();
                        break;
                    case 2:
                        /* animate the text */
                        set_text_splash_animator();
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        /*if user press the skip button take him to the next activity*/
        text_splash_skip.setOnClickListener(view -> {
            skipping_works();
        });
    }

    /* do the animation of text of splash screens */
    private void set_text_splash_animator() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(2500);
        valueAnimator.addUpdateListener(animation -> {
            float alpha = (float) animation.getAnimatedValue();
            text_splash_title.setAlpha(alpha);
            text_splash_description.setAlpha(alpha);
        });
        valueAnimator.start();
    }

    private void skipping_works() {
        /*if user is previously logged in take him to the MainActivity*/
        Storage storage = new Storage(SplashActivity.this);
        if (storage.get_sign_in_state()) {
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            SplashActivity.this.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            finish();
        } else {
            /* if user is previously not logged in take him to the AuthActivity */
            Intent intent = new Intent(SplashActivity.this, AuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            SplashActivity.this.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            finish();
        }
    }

    /* type casting of views */
    private void init_views() {
        text_splash_skip = findViewById(R.id.text_splash_skip);
        text_splash_title = findViewById(R.id.text_splash_title);
        text_splash_description = findViewById(R.id.text_splash_description);
        view_pager_splash = findViewById(R.id.view_pager_splash);
        page_indicator_view = findViewById(R.id.page_indicator_view);
    }
}
