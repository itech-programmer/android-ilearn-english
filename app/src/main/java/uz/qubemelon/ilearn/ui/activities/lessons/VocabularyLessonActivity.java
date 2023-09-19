package uz.qubemelon.ilearn.ui.activities.lessons;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.models.courses.lessons.Lesson;
import uz.qubemelon.ilearn.network.ErrorHandler;
import uz.qubemelon.ilearn.network.RetrofitClient;
import uz.qubemelon.ilearn.network.RetrofitInterface;
import uz.qubemelon.ilearn.network.services.LoadImageService;
import uz.qubemelon.ilearn.ui.activities.exercises.VocabularyExerciseActivity;
import uz.qubemelon.ilearn.ui.activities.lessons.vocabulary.VocabularyLessonsListActivity;
import uz.qubemelon.ilearn.ui.activities.questions.VocabularyQuestionsActivity;
import uz.qubemelon.ilearn.utilities.Utility;

public class VocabularyLessonActivity extends AppCompatActivity implements Html.ImageGetter {

    private Context context;
    private Fragment current_fragment;
    private Storage lesson_storage;
    private int LESSON_ID;
    private Lesson lesson;
    private LinearLayoutCompat layout_exercises, layout_question;
    private TextView text_total_point, text_lesson_title, text_teacher_name, text_lesson_context;
    private ImageView image_back, image_total_point, chapter_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* add the layout to the activity */
        setContentView(R.layout.activity_vocabulary_lessons);

        init_view();

        lesson_storage = new Storage(this);
        text_total_point.setText(String.valueOf(lesson_storage.get_user_total_point()));
        /* get the question id from intent and pass it to the getQuestionList function to do the api call for question list */
        LESSON_ID = getIntent().getIntExtra(Utility.LESSON, lesson_storage.get_lesson_id());

        if (LESSON_ID != 0) {
            /* if there is internet call get data from server, otherwise load for local response */
            if (Utility.is_internet_available(VocabularyLessonActivity.this)) {
                get_lesson(LESSON_ID);
            }
        }
    }

    @Override
    public Drawable getDrawable(String source) {
        LevelListDrawable drawable = new LevelListDrawable();
        Drawable empty = getResources().getDrawable(R.drawable.ic_no_image);
        drawable.addLevel(0, 0, empty);
        drawable.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

        new LoadImageService().execute(source, drawable);

        return drawable;
    }

    /*this is the api call to get data from the server we passes question id to get the question list */
    private void get_lesson(int LESSON_ID) {

        Storage storage = new Storage(this);
        ProgressDialog dialog = Utility.show_dialog(this);
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        final Call<String> lesson_call = retrofit_interface.get_lesson(RetrofitClient.LESSON_VIEW_URL + "/" + LESSON_ID, storage.get_access_token());

        lesson_call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                /* handle http error globally */
                ErrorHandler.get_instance().handle_error(response.code(), VocabularyLessonActivity.this, dialog);
                if (response.isSuccessful()) {
                    /* success true */
                    try {

                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body());
                        boolean isSuccess = jsonObject.getBoolean("success");
                        if (isSuccess) {
                            /* serialize the String response */
                            Gson gson = new Gson();
                            lesson = gson.fromJson(response.body(), Lesson.class);
                            storage.save_lesson_id(lesson.getId());
                            if (lesson != null) {

                                Picasso.get().load(lesson.getImage()).placeholder(R.drawable.ic_no_image).into(chapter_image);
                                text_lesson_title.setText(lesson.getTitle());
                                text_teacher_name.setText(lesson.getTeacher());
                                Spanned spanned = Html.fromHtml(lesson.getContent());
                                text_lesson_context.setText(spanned);
                                /* if user clicks on the back button take user to HomeActivity */
                                layout_exercises.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(VocabularyLessonActivity.this, VocabularyExerciseActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra(Utility.LESSON, lesson.getTitle());
                                        startActivity(intent);
                                        VocabularyLessonActivity.this.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                                        finish();

                                    }
                                });
                                layout_question.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(VocabularyLessonActivity.this, VocabularyQuestionsActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra(Utility.LESSON, lesson.getTitle());
                                        startActivity(intent);
                                        VocabularyLessonActivity.this.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                                        finish();

                                    }
                                });
                            } else {
                                Toast.makeText(VocabularyLessonActivity.this, "NO Lesson Found!", Toast.LENGTH_SHORT).show();
                            }
                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);
                        } else {
                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);
                            /* get all the error messages and show to the user */
                            String message = jsonObject.getString("message");
                            Toast.makeText(VocabularyLessonActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Utility.dismiss_dialog(dialog);
                    Toast.makeText(VocabularyLessonActivity.this, R.string.no_data_found, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {
                /* dismiss the dialog */
                Utility.dismiss_dialog(dialog);
                /*handle network error and notify the user*/
                if (throwable instanceof IOException) {
                    Toast.makeText(VocabularyLessonActivity.this, R.string.connection_timeout, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init_view() {
        text_total_point = findViewById(R.id.text_total_point);
        image_total_point = findViewById(R.id.image_total_point);
        layout_exercises = findViewById(R.id.layout_exercises);
        layout_question = findViewById(R.id.layout_question);
        text_lesson_title = findViewById(R.id.text_lesson_title);
        text_teacher_name = findViewById(R.id.text_teacher_name);
        text_lesson_context = findViewById(R.id.text_lesson_context);
        chapter_image = findViewById(R.id.chapter_image);

    }

    /* this is called when user press back button */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(VocabularyLessonActivity.this, VocabularyLessonsListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
