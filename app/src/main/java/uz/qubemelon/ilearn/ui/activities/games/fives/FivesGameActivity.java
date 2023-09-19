package uz.qubemelon.ilearn.ui.activities.games.fives;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.models.courses.CourseResponse;
import uz.qubemelon.ilearn.network.ErrorHandler;
import uz.qubemelon.ilearn.network.RetrofitClient;
import uz.qubemelon.ilearn.network.RetrofitInterface;
import uz.qubemelon.ilearn.ui.activities.HomeActivity;
import uz.qubemelon.ilearn.utilities.Utility;

public class FivesGameActivity extends AppCompatActivity implements View.OnClickListener {

    /* all global field instances are here */
    private AppCompatActivity activity;
    private TextView text_best;
    private Button btn_back, btn_play;
    private CourseResponse fives_game_response;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_fives_home);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        // Hide Status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // Init Views
        init_views();

        // Get Best Score
        get_best_score();
    }

    private void get_best_score() {
        Storage storage = new Storage(this);
        ProgressDialog dialog = Utility.show_dialog(this);
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        Call<String> fives_call = retrofit_interface.get_fives_best_score(RetrofitClient.GAMES + "/" + RetrofitClient.FIVES, storage.get_access_token(), Utility.REQUEST_TYPE);
        fives_call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                /*handle error globally */
                ErrorHandler.get_instance().handle_error(response.code(), activity, dialog);
                if (response.isSuccessful()) {
                    /* success true */
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body());
                        boolean isSuccess = jsonObject.getBoolean("success");

                        if (isSuccess) {
                            /* serialize the String response */
                            Gson gson = new Gson();
                            fives_game_response = gson.fromJson(response.body(), CourseResponse.class);

                            /* save the category response for offline uses */
                            storage.save_fives_game_score(fives_game_response.getFivesGame().getFivesGameScore());

                            text_best.setText(String.valueOf(fives_game_response.getFivesGame().getFivesGameScore()));

                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);
                        } else {
                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);
                            /* get all the error messages and show to the user */
                            String message = jsonObject.getString("message");
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Utility.dismiss_dialog(dialog);
                    Toast.makeText(activity, R.string.no_data_found, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {
                /* dismiss the dialog */
                Utility.dismiss_dialog(dialog);
                /* handle network error and notify the user */
                if (throwable instanceof IOException) {
                    Toast.makeText(getApplicationContext(), R.string.connection_timeout, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_play:
                startActivity(new Intent(FivesGameActivity.this, GameBoardActivity.class));
                break;
            case R.id.btn_back:
                startActivity(new Intent(FivesGameActivity.this, HomeActivity.class));
                finish();
                break;
        }
    }

    private void init_views() {
        text_best = findViewById(R.id.text_best_score);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_play = findViewById(R.id.btn_play);
        btn_play.setOnClickListener(this);
    }
}
