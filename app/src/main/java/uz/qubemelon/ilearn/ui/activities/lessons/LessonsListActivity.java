package uz.qubemelon.ilearn.ui.activities.lessons;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.adapters.ChaptersAdapter;
import uz.qubemelon.ilearn.adapters.LessonsAdapter;
import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.models.courses.CourseResponse;
import uz.qubemelon.ilearn.network.ErrorHandler;
import uz.qubemelon.ilearn.network.RetrofitClient;
import uz.qubemelon.ilearn.network.RetrofitInterface;
import uz.qubemelon.ilearn.ui.activities.HomeActivity;
import uz.qubemelon.ilearn.utilities.Utility;

public class LessonsListActivity extends AppCompatActivity {

    private Context context;
    private Storage lessons_storage;
    private int CHAPTER_ID;
    private TextView text_total_point;
    private ImageView image_back, image_total_point;
    public CourseResponse lessons_list;
    private LessonsAdapter lessons_adapter;
    private RecyclerView lessons_recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* add the layout to the activity */
        setContentView(R.layout.activity_lessons_list);

        init_view();

        lessons_storage = new Storage(this);

        /* adding options to category recycler */

        /* do api call here and set data to the recycler view */

        /* get the question id from intent and pass it to the getQuestionList function to do the api call for question list */
        CHAPTER_ID = getIntent().getIntExtra(Utility.CHAPTER, lessons_storage.get_chapter_id());

            /* if there is internet call get data from server, otherwise load for local response */
            if (Utility.is_internet_available(LessonsListActivity.this)) {
                getLessonsList(CHAPTER_ID);
            } else {
                if (lessons_storage.get_lessons_response() != null) {
                    /* load data from offline */
                    /* serialize the String response */
                    Gson gson = new Gson();
                    CourseResponse lesson_list = gson.fromJson(lessons_storage.get_lessons_response(), CourseResponse.class);
                    /* add the data to the recycler view */
                    if (lesson_list.getLessonList() != null)
                        lessons_adapter = new LessonsAdapter(LessonsListActivity.this, lesson_list.getLessonList());
                    lessons_recyclerview.setLayoutManager(new LinearLayoutManager(context));
                    lessons_recyclerview.setItemAnimator(new DefaultItemAnimator());
                    lessons_recyclerview.setAdapter(lessons_adapter);
                }
            }
    }

    /* this is the api call to get data from the server we passes question id to get the question list */
    private void getLessonsList(int CHAPTER_ID) {
        Storage storage = new Storage(this);
        ProgressDialog dialog = Utility.show_dialog(this);
        RetrofitInterface retrofitInterface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        Call<String> lesson_list_call = retrofitInterface.getLessonsList(RetrofitClient.LESSONS_URL + "/" + CHAPTER_ID, storage.get_access_token());
        lesson_list_call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                /* handle http error globally */
                ErrorHandler.get_instance().handle_error(response.code(), LessonsListActivity.this, dialog);
                if (response.isSuccessful()) {
                    /* success true */
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body());
                        boolean isSuccess = jsonObject.getBoolean("success");
                        if (isSuccess) {
                            /* save the lesson response for offline uses */
                            storage.save_lessons_response(response.body());
                            /* serialize the String response */
                            Gson gson = new Gson();
                            lessons_list = gson.fromJson(response.body(), CourseResponse.class);

                            /* add the data to the recycler view */
                            lessons_adapter = new LessonsAdapter(LessonsListActivity.this, lessons_list.getLessonList());
                            lessons_recyclerview.setLayoutManager(new LinearLayoutManager(context));
                            lessons_recyclerview.setItemAnimator(new DefaultItemAnimator());
                            lessons_recyclerview.setAdapter(lessons_adapter);

                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);
                        } else {
                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);
                            /* get all the error messages and show to the user */
                            String message = jsonObject.getString("message");
                            Toast.makeText(LessonsListActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Utility.dismiss_dialog(dialog);
                    Toast.makeText(LessonsListActivity.this, R.string.no_data_found, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {
                /* dismiss the dialog */
                Utility.dismiss_dialog(dialog);
                /*handle network error and notify the user*/
                if (throwable instanceof IOException) {
                    Toast.makeText(LessonsListActivity.this, R.string.connection_timeout, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init_view() {
        text_total_point = findViewById(R.id.text_total_point);
        image_total_point = findViewById(R.id.image_total_point);
        image_back = findViewById(R.id.image_back);
        lessons_recyclerview = findViewById(R.id.recycler_lessons);
    }

    /* this is called when user press back button */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LessonsListActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
