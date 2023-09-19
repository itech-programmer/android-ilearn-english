package uz.qubemelon.ilearn.ui.activities.questions;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.models.courses.CourseResponse;
import uz.qubemelon.ilearn.network.ErrorHandler;
import uz.qubemelon.ilearn.network.RetrofitClient;
import uz.qubemelon.ilearn.network.RetrofitInterface;
import uz.qubemelon.ilearn.ui.activities.lessons.LessonActivity;
import uz.qubemelon.ilearn.ui.fragments.questions.FragmentQuestions;
import uz.qubemelon.ilearn.utilities.Utility;

public class QuestionsActivity extends AppCompatActivity {

    /* all global field instances are here */
    private TextView text_total_point;
    private int LESSON_ID;
    private String LESSON_NAME;
    private boolean isStarted = false;
    public CourseResponse question_list;
    private Fragment current_fragment;
    private Storage question_storage;
    public static CountDownTimer count_down_timer;
    public static HashMap<String, Boolean> isPlayed;
    public int x = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

//        init_views();
        question_storage = new Storage(QuestionsActivity.this);
        Utility.TOTAL_POINT = 0;
        isStarted = false;
        isPlayed = new HashMap<>();
        isPlayed.clear();
        LESSON_ID = question_storage.get_lesson_id();
        Intent intent = getIntent();
        LESSON_NAME = intent.getStringExtra(Utility.LESSON);
        get_question_list(LESSON_ID);
    }

    private void get_question_list(int LESSON_ID) {

        Storage storage = new Storage(this);
        ProgressDialog dialog = Utility.show_dialog(this);
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        final Call<String> question_call = retrofit_interface.get_questions_list(RetrofitClient.LESSON_VIEW_URL + "/" + LESSON_ID + "/" + RetrofitClient.QUESTIONS_URL, storage.get_access_token());

        question_call.enqueue(new Callback<String>() {

            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                /* handle http error globally */
                ErrorHandler.get_instance().handle_error(response.code(), QuestionsActivity.this, dialog);
                if (response.isSuccessful()) {
                    /* success true */

                    try {

                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body());
                        final boolean isSuccess = jsonObject.getBoolean("success");
                        if (isSuccess) {
                            /* serialize the String response */

                            Gson gson = new Gson();
                            question_list = gson.fromJson(response.body(), CourseResponse.class);

                            if (question_list != null) {
                                if (question_list.getQuestionList().size() != 0) {

                                    /* show overview dialog first */
                                    final Dialog overview_dialog = new Dialog(QuestionsActivity.this);
                                    overview_dialog.setContentView(R.layout.dialog_questions_overview);
                                    overview_dialog.getWindow().getAttributes().windowAnimations = R.style.alert_dialog;
                                    overview_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                    TextView lesson_name = overview_dialog.findViewById(R.id.text_which_lesson);
                                    TextView total_questions = overview_dialog.findViewById(R.id.text_total_question);
                                    TextView total_point = overview_dialog.findViewById(R.id.text_total_point);


                                    lesson_name.setText(String.format("In %s Lesson", LESSON_NAME));
                                    total_questions.setText(String.format("%d", question_list.getTotalQuestion()));
                                    total_point.setText(String.format("%d", question_list.getTotalPoint()));

                                    Button let_start = overview_dialog.findViewById(R.id.btn_lets_start);
                                    Button cancel = overview_dialog.findViewById(R.id.btn_cancel);

                                    let_start.setOnClickListener(view -> {
                                        if (overview_dialog.isShowing()) {
                                            overview_dialog.dismiss();
                                        }

                                        isStarted = true;

                                        FragmentQuestions question_fragment = new FragmentQuestions();
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable(Utility.QUESTION, question_list.getQuestionList().get(0));
                                        bundle.putInt("TOTAL_POINT", question_list.getUserAvailablePoint());

                                        question_fragment.setArguments(bundle);
                                        fragment_transition(question_fragment);
                                    });

                                    cancel.setOnClickListener(view -> {
                                        if (overview_dialog.isShowing()) {
                                            overview_dialog.dismiss();
                                        }

                                    });

                                    overview_dialog.show();

                                    overview_dialog.setOnDismissListener(dialogInterface -> {
                                        if (!isStarted) {
                                            Intent intent = new Intent(QuestionsActivity.this, LessonActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        }

                                    });

                                }else {
                                    Toast.makeText(QuestionsActivity.this, "NO Question Found!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);
                        } else {
                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);
                            /* get all the error messages and show to the user */
                            String message = jsonObject.getString("message");
                            Toast.makeText(QuestionsActivity.this, message, Toast.LENGTH_SHORT).show();

                            Button get_back = findViewById(R.id.get_back);
                            CardView no_found_data = findViewById(R.id.no_found_data);
                            no_found_data.setVisibility(VISIBLE);
                            get_back.setOnClickListener(view -> {
                                Intent intent = new Intent(QuestionsActivity.this, LessonActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Utility.dismiss_dialog(dialog);
                    Toast.makeText(QuestionsActivity.this, R.string.no_data_found, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {
                /*dismiss the dialog*/
                Utility.dismiss_dialog(dialog);
                /*handle network error and notify the user*/
                if (throwable instanceof IOException) {
                    Toast.makeText(QuestionsActivity.this, R.string.connection_timeout, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    private void init_views() {
//        text_total_point = findViewById(R.id.text_total_point);
//    }

    /*replace the current fragment with new fragment*/
    public void fragment_transition(Fragment fragment) {
        this.current_fragment = fragment;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.fragment_questions, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /* this is called when user press back button */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LessonActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}