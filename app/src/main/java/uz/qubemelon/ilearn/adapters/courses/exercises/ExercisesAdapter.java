package uz.qubemelon.ilearn.adapters.courses.exercises;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.models.courses.exercises.ExerciseList;
import uz.qubemelon.ilearn.models.courses.lessons.LessonList;
import uz.qubemelon.ilearn.models.courses.questions.SubmitAnswer;
import uz.qubemelon.ilearn.network.ErrorHandler;
import uz.qubemelon.ilearn.network.RetrofitClient;
import uz.qubemelon.ilearn.network.RetrofitInterface;
import uz.qubemelon.ilearn.ui.activities.exercises.ExerciseActivity;
import uz.qubemelon.ilearn.ui.activities.questions.QuestionsActivity;
import uz.qubemelon.ilearn.ui.fragments.exercises.FragmentExercises;
import uz.qubemelon.ilearn.utilities.Utility;

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ViewHolder> {

    // Store a member variable for the contacts
    private boolean isAnswered = false;
    private int point;
    private Activity activity;
    private int exercise_id;
    private int right_id = 0;
    private List<ExerciseList> exercises_lists;
    private FragmentExercises fragment_exercises;
    private TextView text_user_point;
    private String answer;

    // Pass in the contact array into the constructor
    public ExercisesAdapter(FragmentExercises fragment_exercises, Activity activity, List<ExerciseList> exercises_lists, TextView text_user_point) {
        this.fragment_exercises = fragment_exercises;
        this.activity = activity;
        this.exercises_lists = exercises_lists;
        text_user_point = text_user_point;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int view_type) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Toast.makeText(activity, exercises_lists.get(position).getId(), Toast.LENGTH_LONG).show();
//        holder.text_exercise_content.setText(exercises_lists.get(position).getContent());
//        holder.text_exercise_title.setText(exercises_lists.get(position).getTitle());
//        answer = String.valueOf(holder.edit_answer.getText());
//
//        Picasso.get().load(chapter_list_items.get(position).getImage()).placeholder(R.drawable.ic_no_image).into(holder.image_chapter);
//        Picasso.get().load(chapter_list_items.get(position).getImage()).placeholder(R.drawable.ic_no_image).into(holder.image_chapter);

    }

    @Override
    public int getItemCount() {
        return exercises_lists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView text_exercise_content, text_exercise_title;
        ImageView image_true, image_false;
        EditText edit_answer;
        Button btn_submit;
        ProgressBar progress_bar_load_exercises;

        public ViewHolder(@NonNull View item_view) {
            super(item_view);
            text_exercise_content = item_view.findViewById(R.id.text_exercise_content);
            text_exercise_title = item_view.findViewById(R.id.text_exercise_title);
            image_true = item_view.findViewById(R.id.image_true);
            image_false = item_view.findViewById(R.id.image_false);
            edit_answer = item_view.findViewById(R.id.edit_answer);
            btn_submit = item_view.findViewById(R.id.btn_submit);
            progress_bar_load_exercises = item_view.findViewById(R.id.progress_bar_load_exercises);
        }

        @Override
        public void onClick(View v) {
            /* if user has not tap on any options, do the api call of submit answer */
            if (!isAnswered) {
                submit_answer(String.valueOf(exercise_id), getAbsoluteAdapterPosition());
            }
        }

        /* call this function to submit answer by options id, position, and holder */
        public void submit_answer(String answer_id, final int position) {
            Storage storage = new Storage(activity);
            RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
            final Call<String> submitAnswerCall = retrofit_interface.submit_answer(RetrofitClient.SUBMIT_ANSWER_ULR + "/" + answer_id, storage.get_access_token(), exercises_lists.get(position).getId());
            submitAnswerCall.enqueue(new Callback<String>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    /*handle error globally */
                    ErrorHandler.get_instance().handle_error(response.code(), activity, null);
                    if (response.isSuccessful()) {
                        /* success true */
                        try {
                            assert response.body() != null;
                            JSONObject jsonObject = new JSONObject(response.body());
                            boolean isSuccess = jsonObject.getBoolean("success");
                            text_exercise_content.setTextColor(activity.getResources().getColor(R.color.text_color_primary));
                            answer = String.valueOf(edit_answer.getText());
                            if (exercises_lists.get(position).getAnswer().equals(answer)) {
                                isAnswered = true;
                                /* if the answer is right */
                                /* make global value isPlayed to true */
                                QuestionsActivity.isPlayed.put(answer_id, true);
                                String total_point = jsonObject.getString("total_point");
                                /* set global quiz point */
                                Utility.QUESTION_POINT = Integer.parseInt(total_point);
                                /* update total point */
                                Utility.TOTAL_POINT = Utility.TOTAL_POINT + point;
                                /* increase user total point and coin */
                                fragment_exercises.TOTAL_POINT = fragment_exercises.TOTAL_POINT + point;

                                image_false.setVisibility(GONE);
                                image_true.setBackgroundResource(R.drawable.answer_right);

                                /*if sound options available then play the sound*/
                                if (storage.get_sound_state()) {
                                    Utility.play_right_music(activity);
                                }

                            } else {
                                isAnswered = true;

                                /* if answer is not right */
                                QuestionsActivity.isPlayed.put(answer_id, true);

                                /* if sound is available then play sound */
                                if (storage.get_sound_state()) {
                                    Utility.play_wrong_music(activity);
                                    Utility.vibrate_phone(900, activity);
                                    /* serialize the String response  */
                                }

                                image_true.setVisibility(VISIBLE);
                                image_false.setBackgroundResource(R.drawable.answer_wrong);

                                Gson gson = new Gson();
                                SubmitAnswer submit_answer = gson.fromJson(response.body(), SubmitAnswer.class);
                                right_id = submit_answer.getRightAnswer().getId();
                                notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(activity, R.string.no_data_found, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {
                    /*handle network error and notify the user*/
                    if (throwable instanceof IOException) {
                        Toast.makeText(activity, R.string.connection_timeout, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}
