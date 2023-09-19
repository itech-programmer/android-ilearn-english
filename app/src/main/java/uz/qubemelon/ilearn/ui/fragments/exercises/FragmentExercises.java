package uz.qubemelon.ilearn.ui.fragments.exercises;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
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
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.adapters.courses.exercises.ExercisesAdapter;
import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.models.courses.CourseResponse;
import uz.qubemelon.ilearn.models.courses.exercises.ExerciseList;
import uz.qubemelon.ilearn.models.courses.questions.QuestionList;
import uz.qubemelon.ilearn.ui.activities.exercises.ExerciseActivity;
import uz.qubemelon.ilearn.ui.activities.questions.QuestionsActivity;
import uz.qubemelon.ilearn.ui.fragments.questions.FragmentQuestions;
import uz.qubemelon.ilearn.utilities.Utility;

public class FragmentExercises extends Fragment {

    /* all global field instances */
    private ImageView image_stopwatch, img_cat_status, image_exercise_image;
    private TextView text_exercise_position, text_exercise_time, text_exercise_count, text_exercise_name, text_point, text_total_point;
    private ProgressBar progress_bar_load_exercises;
    private FrameLayout layout_exercise_image;
    private Button btn_exercise, btn_next;
    private Storage exercise_storage;
    private ExerciseList exercise_item;
    private CourseResponse exercise_list;
    private ExercisesAdapter exercises_adapter;
    private RecyclerView exercises_recycler_view;
    public int TOTAL_POINT = 0;
    private View view;

    public FragmentExercises() {
        // Required empty public constructor
    }

    public static FragmentQuestions new_instance(QuestionList item) {
        Bundle bundleGot = new Bundle();
        bundleGot.putSerializable(Utility.EXERCISE, item);
        FragmentQuestions fragment = new FragmentQuestions();
        fragment.setArguments(bundleGot);
        return fragment;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {

        /* inflate the fragment layout */
        view = inflater.inflate(R.layout.fragment_exercise, container, false);

        init_views();

        exercise_storage = new Storage(getActivity());

        if (getActivity() != null && isAdded()) {
            /* make the counter value to default */
            image_stopwatch.setImageResource(R.drawable.ic_watch);
            text_exercise_position.setText(String.format("%d", ((ExerciseActivity) getActivity()).x));
            text_exercise_time.setText(String.format("%s:%s", "0", "00"));
        }

        if (getArguments() != null) {
            exercise_item = (ExerciseList) getArguments().getSerializable(Utility.EXERCISE);
            if (exercise_item != null) {

                /* start the count by getting the total time */
                time_count(Utility.get_milliseconds_from_minutes(exercise_item.getTimeLimit()));
                Log.e("EXERCISES", String.valueOf(exercise_list.getExerciseList().size()));
                text_exercise_count.setText(String.format("/%d", ((ExerciseActivity)  getActivity()).exercise_list.getExerciseList().size()));
                /* make the option recycler view */
//                exercises_adapter = new ExercisesAdapter(FragmentExercises.this, getActivity(), exercise_list.getExerciseList(), text_point);
//                exercises_recycler_view.setAdapter(exercises_adapter);

                /* check if this question has a image if has then show the image */
                progress_bar_load_exercises.setVisibility(View.VISIBLE);
                /* take total user coin, point and hint point */

                TOTAL_POINT = getArguments().getInt("TOTAL_POINT");
                text_point.setText(String.valueOf(TOTAL_POINT));
            }

        }

        // Inflate the layout for this fragment
        return view;
    }

    /* start the count down timer */
    private void time_count(long milliseconds) {
        if (getActivity() != null)
            QuestionsActivity.count_down_timer = new CountDownTimer(milliseconds, 1000) {
                @Override
                public void onTick(long l) {
                    if ((l / 1000) <= 10) {
                        do_blink_animation(text_exercise_time);
                        image_stopwatch.setImageResource(R.drawable.ic_frown);
                    }
                    set_exercise_time(Utility.get_time_from_millisecond(l));
                }

                @Override
                public void onFinish() {
                    text_exercise_time.setText(String.format("%s:%s", "0", "00"));
                    remove_blink_animation(text_exercise_time);
                    image_stopwatch.setImageResource(R.drawable.ic_watch);
                    cancel();
                }
            };
        QuestionsActivity.count_down_timer.start();
    }

    // set data to the timer
    @SuppressLint("DefaultLocale")
    private void set_exercise_time(HashMap<String, Integer> timeFromMillisecond) {
        if (getActivity() != null && isAdded()) {
            text_exercise_time.setText(String.format("%s:%s",
                    String.valueOf(timeFromMillisecond.get("min")),
                    String.valueOf(timeFromMillisecond.get("sec"))));
        }
    }

    private void init_views() {
        if (view != null) {
            progress_bar_load_exercises = view.findViewById(R.id.progress_bar_load_exercises);
            btn_next = view.findViewById(R.id.btn_next);
            text_point = view.findViewById(R.id.text_point);
            text_exercise_time = view.findViewById(R.id.text_exercise_time);
            text_exercise_count = view.findViewById(R.id.text_exercise_count);
            text_exercise_position = view.findViewById(R.id.text_exercise_position);
            image_stopwatch = view.findViewById(R.id.image_stopwatch);
            exercises_recycler_view = view.findViewById(R.id.exercises_recycler_view);
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
            if (QuestionsActivity.count_down_timer != null) {
                QuestionsActivity.count_down_timer.cancel();
            }
        super.onDestroyView();
    }

}
