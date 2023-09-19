package uz.qubemelon.ilearn.adapters.courses.lessons;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.models.courses.lessons.LessonList;
import uz.qubemelon.ilearn.ui.activities.lessons.VocabularyLessonActivity;
import uz.qubemelon.ilearn.utilities.Utility;

public class VocabularyLessonsAdapter extends RecyclerView.Adapter<VocabularyLessonsAdapter.LessonViewHolder>{

    /* all global field instances */
    private Activity activity;
    private List<LessonList> lessons_lists;

    public VocabularyLessonsAdapter(Activity activity, List<LessonList> lessons_lists) {
        this.activity = activity;
        this.lessons_lists = lessons_lists;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lessons, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        holder.text_lesson_title.setText(lessons_lists.get(position).getTitle());
        holder.text_chapter_title.setText(lessons_lists.get(position).getChapterTitle());
    }


    // get the count of the list
    @Override
    public int getItemCount() {
        return lessons_lists.size();
    }

    public class LessonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView text_lesson_title, text_chapter_title;

        public LessonViewHolder(@NonNull View item_view) {
            super(item_view);
            itemView.setOnClickListener(this);
            text_lesson_title = item_view.findViewById(R.id.text_lesson_title);
            text_chapter_title = item_view.findViewById(R.id.text_chapter_title);
        }

        @Override
        public void onClick(View view) {
            /* there is no sub category so direct browse to question */
            Storage storage = new Storage(activity);
            Intent intent = new Intent(activity, VocabularyLessonActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Utility.LESSON, lessons_lists.get(getAbsoluteAdapterPosition()).getId());
            storage.save_lesson_id(lessons_lists.get(getAbsoluteAdapterPosition()).getId());
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            activity.finish();
        }
    }
}
