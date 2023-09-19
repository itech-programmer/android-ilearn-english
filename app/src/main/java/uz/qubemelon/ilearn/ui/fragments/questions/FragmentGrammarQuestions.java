package uz.qubemelon.ilearn.ui.fragments.questions;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.adapters.courses.questions.GrammarQuestionsAnswersAdapter;
import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.models.courses.questions.QuestionList;
import uz.qubemelon.ilearn.ui.activities.lessons.GrammarLessonActivity;
import uz.qubemelon.ilearn.ui.activities.questions.GrammarQuestionsActivity;
import uz.qubemelon.ilearn.utilities.Utility;

public class FragmentGrammarQuestions extends Fragment {

    /* all global field instances */
    private ImageView image_stopwatch, img_cat_status, image_question_image;
    private TextView text_question_position, text_question_time, text_question_count, text_question_name, text_point, text_total_point;
    private ProgressBar progress_bar_load_questions;
    private FrameLayout layout_question_image;
    private Button btn_question, btn_next;
    private Storage question_storage;
    private QuestionList question_item;
    private GrammarQuestionsAnswersAdapter questions_answers_adapter;
    private RecyclerView answer_recycler_view;
    public int TOTAL_POINT = 0;
    private View view;

    public FragmentGrammarQuestions() {
        // Required empty public constructor
    }

    public static FragmentGrammarQuestions new_instance(QuestionList item) {
        Bundle bundleGot = new Bundle();
        bundleGot.putSerializable(Utility.QUESTION, item);
        FragmentGrammarQuestions fragment = new FragmentGrammarQuestions();
        fragment.setArguments(bundleGot);
        return fragment;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {

        /* inflate the fragment layout */
        view = inflater.inflate(R.layout.fragment_question, container, false);

        init_views();

        question_storage = new Storage(getActivity());

        if (getActivity() != null && isAdded()) {
            /* make the counter value to default */
            image_stopwatch.setImageResource(R.drawable.ic_watch);
            text_question_position.setText(String.format("%d", ((GrammarQuestionsActivity) getActivity()).x));
            text_question_time.setText(String.format("%s:%s", "0", "00"));
        }

        build_option_recycler_view();

        if (getArguments() != null) {
            question_item = (QuestionList) getArguments().getSerializable(Utility.QUESTION);
            if (question_item != null) {

                /* start the count by getting the total time */
                time_count(Utility.get_milliseconds_from_minutes(question_item.getTimeLimit()));
                text_question_count.setText(String.format("/%d", ((GrammarQuestionsActivity)  getActivity()).question_list.getQuestionList().size()));
                /*make the option recycler view*/
                text_question_name.setText(question_item.getTitle());
                questions_answers_adapter = new GrammarQuestionsAnswersAdapter(this, Utility.remove_empty_fields(question_item.getAnswers()), getActivity(), question_item.getId(), question_item.getPoint(), img_cat_status, text_point);
                answer_recycler_view.setAdapter(questions_answers_adapter);

                /* check if this question has a image if has then show the image */
                if (question_item.getHasImage() == 1) {
                    layout_question_image.setVisibility(View.VISIBLE);
                    if (question_item.getTitle() == null) {
                        text_question_name.setVisibility(View.GONE);
                    } else {
                        text_question_name.setVisibility(View.VISIBLE);
                    }

                    progress_bar_load_questions.setVisibility(View.VISIBLE);
                    Picasso.get().load(question_item.getImage()).placeholder(R.drawable.background_no_data_found).into(image_question_image);
                } else if (question_item.getHasImage() == 0) {
                    layout_question_image.setVisibility(View.GONE);
                }

                /* take total user coin, point and hint point */

                TOTAL_POINT = getArguments().getInt("TOTAL_POINT");
                text_point.setText(String.valueOf(TOTAL_POINT));
            }

        }


        btn_next.setOnClickListener(view -> {
            /* if user clicks the next button check if the question is played and then take the user to the next question */
            try {
                if (GrammarQuestionsActivity.isPlayed.get(question_item.getId())) {
                    go_to_next_question();
                } else {
                    Toast.makeText(getActivity(), "sorry, Play first!", Toast.LENGTH_SHORT).show();
                }
            } catch (NullPointerException e) {
                Toast.makeText(getActivity(), "sorry, Play first!", Toast.LENGTH_SHORT).show();
            }

        });

        // Inflate the layout for this fragment
        return view;
    }


    /* function for go to next question */
    private void go_to_next_question() {
        /* take the user to the next question if all question are finished then show the result dialog */
        if (getActivity() != null && isAdded()) {
            if (((GrammarQuestionsActivity) getActivity()).question_list != null) {
                if (((GrammarQuestionsActivity) getActivity()).x
                        < ((GrammarQuestionsActivity) getActivity()).question_list.getQuestionList().size()) {
                    FragmentGrammarQuestions fragment_questions = new FragmentGrammarQuestions();
                    Bundle bundle = new Bundle();
                    if (getActivity() != null)
                        bundle.putSerializable(Utility.QUESTION, ((GrammarQuestionsActivity) getActivity()).question_list.getQuestionList().get(((GrammarQuestionsActivity) getActivity()).x++));
                    bundle.putInt("TOTAL_POINT", TOTAL_POINT);
                    fragment_questions.setArguments(bundle);

                    ((GrammarQuestionsActivity) getActivity()).fragment_transition(fragment_questions);

                } else {

                    /* take the user to the result dialog */
                    Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen);
                    dialog.setContentView(R.layout.layout_congrats_dialog);
                    TextView earnedPoint = dialog.findViewById(R.id.text_earned_point);
                    btn_question = dialog.findViewById(R.id.btn_start);

                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            Intent intent = new Intent(getActivity(), GrammarLessonActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    });
                    btn_question.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), GrammarLessonActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    });
                    earnedPoint.setText(String.valueOf(Utility.TOTAL_POINT));
                    dialog.show();
                }
            }
        }

    }

    /* start the count down timer */
    private void time_count(long milliseconds) {
        if (getActivity() != null)
            GrammarQuestionsActivity.count_down_timer = new CountDownTimer(milliseconds, 1000) {
                @Override
                public void onTick(long l) {
                    if ((l / 1000) <= 10) {
                        do_blink_animation(text_question_time);
                        image_stopwatch.setImageResource(R.drawable.ic_frown);
                    }
                    set_question_time(Utility.get_time_from_millisecond(l));
                }

                @Override
                public void onFinish() {
                    text_question_time.setText(String.format("%s:%s", "0", "00"));
                    remove_blink_animation(text_question_time);
                    image_stopwatch.setImageResource(R.drawable.ic_watch);
                    go_to_next_question();
                    cancel();
                }
            };
        GrammarQuestionsActivity.count_down_timer.start();
    }

    // set data to the timer
    @SuppressLint("DefaultLocale")
    private void set_question_time(HashMap<String, Integer> timeFromMillisecond) {
        if (getActivity() != null && isAdded()) {
            text_question_time.setText(String.format("%s:%s",
                    String.valueOf(timeFromMillisecond.get("min")),
                    String.valueOf(timeFromMillisecond.get("sec"))));
        }
    }

    /* make the */
    private void build_option_recycler_view() {
        answer_recycler_view.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        answer_recycler_view.setLayoutManager(gridLayoutManager);
    }

    private void init_views() {
        if (view != null) {
            layout_question_image = view.findViewById(R.id.layout_question_image);
            progress_bar_load_questions = view.findViewById(R.id.progress_bar_load_questions);
            image_question_image = view.findViewById(R.id.image_question_image);
            btn_next = view.findViewById(R.id.btn_next);
            text_point = view.findViewById(R.id.text_point);
            img_cat_status = view.findViewById(R.id.img_cat_status);
            text_question_time = view.findViewById(R.id.text_question_time);
            text_question_count = view.findViewById(R.id.text_question_count);
            text_question_position = view.findViewById(R.id.text_question_position);
            image_stopwatch = view.findViewById(R.id.image_stopwatch);
            text_question_name = view.findViewById(R.id.text_question_name);
            answer_recycler_view = view.findViewById(R.id.recyclerview_question_answer);
        }
    }

    // do the animation
    private void do_blink_animation(TextView textView) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        textView.startAnimation(anim);
    }

    /* remove the animation */
    private void remove_blink_animation(TextView textView) {
        textView.clearAnimation();
    }

    /* destroy the view */
    @Override
    public void onDestroyView() {
        if (getActivity() != null)
            if (GrammarQuestionsActivity.count_down_timer != null) {
                GrammarQuestionsActivity.count_down_timer.cancel();
            }
        super.onDestroyView();
    }

}
