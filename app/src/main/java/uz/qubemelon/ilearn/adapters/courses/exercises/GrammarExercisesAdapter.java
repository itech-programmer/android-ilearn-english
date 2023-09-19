package uz.qubemelon.ilearn.adapters.courses.exercises;

import android.app.Activity;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

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
import uz.qubemelon.ilearn.network.ErrorHandler;
import uz.qubemelon.ilearn.network.RetrofitClient;
import uz.qubemelon.ilearn.network.RetrofitInterface;

public class GrammarExercisesAdapter extends RecyclerView.Adapter<GrammarExercisesAdapter.ViewHolder> {

    // Store a member variable for the contacts
    private Activity activity;
    private List<ExerciseList> exercises_lists;
    private String answer = null;

    // Pass in the contact array into the constructor
    public GrammarExercisesAdapter(Activity activity, List<ExerciseList> exercises_lists) {
        this.activity = activity;
        this.exercises_lists = exercises_lists;
    }

    @NonNull
    @Override
    public GrammarExercisesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrammarExercisesAdapter.ViewHolder holder, int position) {
        holder.text_exercise_content.setText(Html.fromHtml(exercises_lists.get(position).getContent()));
        holder.text_exercise_title.setText(exercises_lists.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return exercises_lists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout answer_layout;
        TextView text_exercise_content, text_exercise_title;
        TextInputLayout layout_exercise_answer;
        ImageView image_true, image_false;
        EditText edit_answer;
        ImageButton btn_submit;
        ProgressBar progress_bar_load_exercises;

        public ViewHolder(@NonNull View item_view) {
            super(item_view);
            answer_layout = item_view.findViewById(R.id.answer_layout);
            text_exercise_content = item_view.findViewById(R.id.text_exercise_content);
            text_exercise_title = item_view.findViewById(R.id.text_exercise_title);
            edit_answer = item_view.findViewById(R.id.edit_answer);
            btn_submit = item_view.findViewById(R.id.btn_submit);
            progress_bar_load_exercises = item_view.findViewById(R.id.progress_bar_load_exercises);

            btn_submit.setOnClickListener(view_submit_answer -> {
                answer = String.valueOf(edit_answer.getText());
                if (edit_answer.getText().toString().trim().length() > 0) {
                    submit_answer(answer, getAbsoluteAdapterPosition());
                }else {
                    Toast.makeText(activity, R.string.first_write_your_answer, Toast.LENGTH_SHORT).show();
                }
            });
        }

        /* call this function to submit answer by options id, position, and holder */
        public void submit_answer(String answer, final int position) {
            Storage storage = new Storage(activity);
            RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
            final Call<String> submit_answer_call = retrofit_interface.submit_exercise_answer(storage.get_access_token(), exercises_lists.get(position).getId(), answer);
            submit_answer_call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    /* handle error globally */
                    ErrorHandler.get_instance().handle_error(response.code(), activity, null);
                    if (response.isSuccessful()) {
                        /* success true */
                        try {
                            assert response.body() != null;
                            JSONObject jsonObject = new JSONObject(response.body());
                            boolean isSuccess = jsonObject.getBoolean("success");
                            if (isSuccess){
                            text_exercise_content.setTextColor(activity.getResources().getColor(R.color.text_color_primary));
                                /* if the answer is right */
                                answer_layout.setBackgroundResource(R.drawable.answer_right_background);
                                edit_answer.setTextColor(Color.parseColor("#ffffff"));
                                text_exercise_content.setTextColor(Color.parseColor("#ffffff"));
                                text_exercise_title.setTextColor(Color.parseColor("#ffffff"));
                                btn_submit.setBackgroundResource(R.drawable.btn_disabled_background);
                                edit_answer.setEnabled(false);
                                btn_submit.setEnabled(false);
                            }else {
                                answer_layout.setBackgroundResource(R.drawable.answer_wrong_background);
                                edit_answer.setTextColor(Color.parseColor("#ffffff"));
                                text_exercise_content.setTextColor(Color.parseColor("#ffffff"));
                                text_exercise_title.setTextColor(Color.parseColor("#ffffff"));
                                btn_submit.setBackgroundResource(R.drawable.btn_disabled_background);
                                edit_answer.setEnabled(false);
                                btn_submit.setEnabled(false);
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
