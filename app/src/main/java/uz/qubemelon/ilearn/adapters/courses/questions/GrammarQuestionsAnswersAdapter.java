package uz.qubemelon.ilearn.adapters.courses.questions;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
import uz.qubemelon.ilearn.models.courses.questions.Answers;
import uz.qubemelon.ilearn.models.courses.questions.SubmitAnswer;
import uz.qubemelon.ilearn.network.ErrorHandler;
import uz.qubemelon.ilearn.network.RetrofitClient;
import uz.qubemelon.ilearn.network.RetrofitInterface;
import uz.qubemelon.ilearn.ui.activities.questions.GrammarQuestionsActivity;
import uz.qubemelon.ilearn.ui.fragments.questions.FragmentGrammarQuestions;
import uz.qubemelon.ilearn.utilities.Utility;

public class GrammarQuestionsAnswersAdapter extends RecyclerView.Adapter<GrammarQuestionsAnswersAdapter.QuestionAnswersHolder> {

    private List<Answers> answers_item_list;
    private Activity activity;
    private String question_id;
    private int point;
    private int right_id = 0;
    private FragmentGrammarQuestions fragment_questions;
    private TextView text_user_point;
    private ImageView right_wrong;
    private boolean isAnswered = false;

    /* this is the constructor for the leaderboard */
    public GrammarQuestionsAnswersAdapter(FragmentGrammarQuestions fragment_questions, List<Answers> answers_item_list, Activity activity, String question_id, int point, ImageView right_wrong, TextView text_user_point) {
        this.answers_item_list = answers_item_list;
        this.point = point;
        this.activity = activity;
        this.question_id = question_id;
        this.right_wrong = right_wrong;
        text_user_point = text_user_point;
        this.fragment_questions = fragment_questions;
    }


    /* this is the function where every row layout inflate for the recycler view */
    @NonNull
    @Override
    public QuestionAnswersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer, parent, false);
        return new QuestionAnswersHolder(view);
    }

    /*this is the place for data binding for recycler view */
    @Override
    public void onBindViewHolder(@NonNull final QuestionAnswersHolder holder, final int position) {
        /*check if user has not selected right answer*/
        if (right_id != 0) {
            if (answers_item_list.get(position).getId() == right_id) {
                /* if not then set the background green for the right answer */
                holder.question_answer_name.setTextColor(activity.getResources().getColor(R.color.text_color_primary));
                if (answers_item_list.get(position).getAnswerType() == 0) {
                    holder.img_question_overlap.setVisibility(GONE);
                    holder.img_question_answer.setBackgroundResource(R.drawable.answer_right_background);
                } else if (answers_item_list.get(position).getAnswerType() == 1) {
                    holder.img_question_overlap.setVisibility(VISIBLE);
                    holder.img_question_overlap.setBackgroundResource(R.drawable.answer_right);
                }
                right_id = 0;
            }
        }
        /* set the option name */
        if (answers_item_list.get(position).getAnswer() != null) {
            if (answers_item_list.get(position).getAnswerType() == 0) {
                holder.question_answer_name.setVisibility(VISIBLE);
                holder.question_answer_name.setText(Utility.convert_un_capitalized(answers_item_list.get(position).getAnswer()));
            } else if (answers_item_list.get(position).getAnswerType() == 1) {
                holder.question_answer_name.setVisibility(GONE);
                holder.progress_bar_answers.setVisibility(VISIBLE);
                Picasso.get().load(answers_item_list.get(position).getAnswer()).into(holder.img_question_answer);
            }
        }

        holder.card_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if user has not tap on any options, do the api call of submit answer */
                if (!isAnswered) {
                    submit_answer(question_id, holder.getAbsoluteAdapterPosition(), holder);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return answers_item_list.size();
    }

    /*quiz holder for the recycler view*/
    static class QuestionAnswersHolder extends RecyclerView.ViewHolder {
        ProgressBar progress_bar_answers;
        ImageView img_question_overlap, img_question_answer;
        CardView card_answer;
        TextView question_answer_name;

        public QuestionAnswersHolder(View itemView) {
            super(itemView);
            progress_bar_answers = itemView.findViewById(R.id.progress_bar_answers);
            img_question_answer = itemView.findViewById(R.id.img_question_answer);
            img_question_overlap = itemView.findViewById(R.id.img_question_overlap);
            card_answer = itemView.findViewById(R.id.card_answer);
            question_answer_name = itemView.findViewById(R.id.text_question_answer);
        }
    }

    /* call this function to submit answer by options id, position, and holder */
    public void submit_answer(String answer_id, final int position, final QuestionAnswersHolder holder) {
        Storage storage = new Storage(activity);
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        final Call<String> submitAnswerCall = retrofit_interface.submit_question_answer(RetrofitClient.SUBMIT_QUESTION_ANSWER_ULR + "/" + answer_id, storage.get_access_token(), answers_item_list.get(position).getId());
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
                        if (!isSuccess) {
                            isAnswered = true;
                            right_wrong.setImageResource(R.drawable.ic_cat_worng);
                            //  holder.quizOptionBg.setImageResource(R.drawable.quiz_option_wrong);
                            /* if answer is not right */
                            GrammarQuestionsActivity.isPlayed.put(answer_id, true);
                            // try {

                            /* if sound is available then play sound */
                            if (storage.get_sound_state()) {
                                Utility.play_wrong_music(activity);
                                Utility.vibrate_phone(900, activity);
                                /* serialize the String response  */
                            }

                            holder.question_answer_name.setTextColor(activity.getResources().getColor(R.color.text_color_primary));
                            if (answers_item_list.get(position).getAnswerType() == 0) {
                                holder.img_question_overlap.setVisibility(GONE);
                                holder.img_question_answer.setBackgroundResource(R.drawable.answer_wrong_background);
                            } else if (answers_item_list.get(position).getAnswerType() == 1) {
                                holder.img_question_overlap.setVisibility(VISIBLE);
                                holder.img_question_overlap.setBackgroundResource(R.drawable.answer_wrong);
                                Picasso.get().load(answers_item_list.get(position).getAnswer()).into(holder.img_question_overlap);
                            }

                            Gson gson = new Gson();
                            SubmitAnswer submit_answer = gson.fromJson(response.body(), SubmitAnswer.class);
                            right_id = submit_answer.getRightAnswer().getId();
                            notifyDataSetChanged();

                        } else {

                            /* if the answer is right */
                            /* make global value isPlayed to true */
                            GrammarQuestionsActivity.isPlayed.put(answer_id, true);
                            String total_point = jsonObject.getString("total_point");
                            /* set global quiz point */
                            Utility.QUESTION_POINT = Integer.parseInt(total_point);

                            right_wrong.setImageResource(R.drawable.ic_cat);
                            /* update total point */
                            Utility.TOTAL_POINT = Utility.TOTAL_POINT + point;

                            /* increase user total point and coin */
                            fragment_questions.TOTAL_POINT = fragment_questions.TOTAL_POINT + point;

                            /*if sound options available then play the sound*/
                            if (storage.get_sound_state()) {
                                Utility.play_right_music(activity);
                            }
                            isAnswered = true;
                            holder.question_answer_name.setTextColor(activity.getResources().getColor(R.color.text_color_primary));

                            if (answers_item_list.get(position).getAnswerType() == 0) {
                                holder.img_question_overlap.setVisibility(GONE);
                                holder.img_question_answer.setBackgroundResource(R.drawable.answer_right_background);
                            } else if (answers_item_list.get(position).getAnswerType() == 1) {
                                holder.img_question_overlap.setVisibility(VISIBLE);
                                holder.img_question_overlap.setBackgroundResource(R.drawable.answer_right);
//                                Picasso.get().load(answers_item_list.get(position).getAnswer()).into(holder.img_question_overlap);
                            }
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
