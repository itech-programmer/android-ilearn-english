package uz.qubemelon.ilearn.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.models.courses.chapters.ChapterList;
import uz.qubemelon.ilearn.ui.activities.lessons.LessonsListActivity;
import uz.qubemelon.ilearn.utilities.Utility;

public class ChaptersAdapter extends RecyclerView.Adapter<ChaptersAdapter.ChapterHolder> {

    /* all global field instances */
    private Activity activity;
    private List<ChapterList> chapter_list_items;

    public ChaptersAdapter(List<ChapterList> chapter_list_items, Activity activity) {
        this.chapter_list_items = chapter_list_items;
        this.activity = activity;
    }

    /* here all row layout get inflated */
    @NonNull
    @Override
    public ChapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, parent, false);
        return new ChapterHolder(view);
    }

    /* here all data binding happens */
    @Override
    public void onBindViewHolder(@NonNull ChapterHolder holder, int position) {
        holder.text_chapter.setText(chapter_list_items.get(position).getTitle());
        Picasso.get().load(chapter_list_items.get(position).getImage()).placeholder(R.drawable.ic_no_image).into(holder.image_chapter);
    }

    // get the count of the list
    @Override
    public int getItemCount() {
        return chapter_list_items.size();
    }

    /* this is the custom view holder class for recycler view */
    class ChapterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView text_chapter;
        ImageView image_chapter;

        ChapterHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            text_chapter = itemView.findViewById(R.id.text_chapter);
            image_chapter = itemView.findViewById(R.id.image_chapter);
        }

//         if user clicks on any category then take him to the activity
        public void onClick(View view) {
                /* there is no sub category so direct browse to question */
            Storage storage = new Storage(activity);
            Intent intent = new Intent(activity, LessonsListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            storage.save_chapter_id(chapter_list_items.get(getAbsoluteAdapterPosition()).getId());
            intent.putExtra(Utility.CHAPTER, chapter_list_items.get(getAbsoluteAdapterPosition()).getId());
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            activity.finish();

        }
    }
}
