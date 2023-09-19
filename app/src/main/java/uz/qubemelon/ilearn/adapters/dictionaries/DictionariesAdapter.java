package uz.qubemelon.ilearn.adapters.dictionaries;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.Activity;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.models.dictionaries.DictionaryList;

public class DictionariesAdapter extends RecyclerView.Adapter<DictionariesAdapter.DictionaryHolder> {

    /* all global field instances */
    private Activity activity;
    private List<DictionaryList> dictionary_list_items;

    public DictionariesAdapter(List<DictionaryList> dictionary_list_items, Activity activity) {
        this.dictionary_list_items = dictionary_list_items;
        this.activity = activity;
    }

    // Clean all elements of the recycler
    public void clear() {
        dictionary_list_items.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<DictionaryList> list) {
        dictionary_list_items.addAll(list);
        notifyDataSetChanged();
    }

    // method for filtering our recyclerview items.
    public Filter dictionary_filter_list() {
        return dictionary_filter;
    }

    private Filter dictionary_filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<DictionaryList> filtered_list = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filtered_list.addAll(dictionary_list_items);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (DictionaryList item : dictionary_list_items) {
                    if (item.getEnWord().toLowerCase().contains(filterPattern)) {
                        filtered_list.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filtered_list;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dictionary_list_items.clear();
            dictionary_list_items.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    /* here all row layout get inflated */
    @NonNull
    @Override
    public DictionaryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dictionary, parent, false);
        return new DictionaryHolder(view);
    }

    /* here all data binding happens */
    @Override
    public void onBindViewHolder(@NonNull DictionaryHolder holder, int position) {
        holder.text_word.setText(dictionary_list_items.get(position).getEnWord());
    }

    // get the count of the list
    @Override
    public int getItemCount() {
        return dictionary_list_items.size();
    }

    /* this is the custom view holder class for recycler view */
    class DictionaryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private PopupWindow popup_window;
        private LinearLayoutCompat layout_en, layout_uz, layout_qr, layout_ru;
        private TextView text_en, text_uz, text_qr, text_ru, text_word, text_definition;
        private Button btn_ok;

        DictionaryHolder(View item_view) {
            super(item_view);
            item_view.setOnClickListener(this);
            text_word = item_view.findViewById(R.id.text_word);
        }

        //if user clicks on any category then take him to the activity
        public void onClick(View view) {
            // inflate the layout of the popup window
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
            View popup_view = inflater.inflate(R.layout.popup_dictionary, null);

            // create the popup window
            // Initialize a new instance of popup window
            popup_window = new PopupWindow(
                    popup_view,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );

            // Set an elevation value for popup window
                popup_window.setElevation(5.0f);
                popup_window.setFocusable(true);
                popup_window.setOutsideTouchable(false);

            // show the popup window
            // which view you pass in doesn't matter, it is only used for the window token
            popup_window.showAtLocation(view, Gravity.CENTER,0,0);
            // dismiss the popup window when touched

            layout_en = popup_view.findViewById(R.id.layout_en);
            layout_uz = popup_view.findViewById(R.id.layout_uz);
            layout_qr = popup_view.findViewById(R.id.layout_qr);
            layout_ru = popup_view.findViewById(R.id.layout_ru);
            text_en = popup_view.findViewById(R.id.text_en);
            text_uz = popup_view.findViewById(R.id.text_uz);
            text_qr = popup_view.findViewById(R.id.text_qr);
            text_ru = popup_view.findViewById(R.id.text_ru);
            text_word = popup_view.findViewById(R.id.text_word);
            text_definition = popup_view.findViewById(R.id.text_definition);
            btn_ok = popup_view.findViewById(R.id.btn_ok);

            text_word.setText(dictionary_list_items.get(getAbsoluteAdapterPosition()).getEnWord());
            Spanned spanned = Html.fromHtml(dictionary_list_items.get(getAbsoluteAdapterPosition()).getEnDefinition());
            text_definition.setText(spanned);

            layout_en.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    layout_en.setBackgroundResource(R.drawable.tab_active_leaderboard);
                    layout_uz.setBackgroundResource(R.drawable.tab_disable_leaderboard);
                    layout_qr.setBackgroundResource(R.drawable.tab_disable_leaderboard);
                    layout_ru.setBackgroundResource(R.drawable.tab_disable_leaderboard);

                    /* change the color of the selected item text */
                    text_en.setTextColor(activity.getResources().getColor(R.color.text_color_white));

                    /* change other text color */
                    text_uz.setTextColor(activity.getResources().getColor(R.color.text_color_grey));
                    text_qr.setTextColor(activity.getResources().getColor(R.color.text_color_grey));
                    text_ru.setTextColor(activity.getResources().getColor(R.color.text_color_grey));

                    text_word.setText(dictionary_list_items.get(getAbsoluteAdapterPosition()).getEnWord());
                    Spanned spanned = Html.fromHtml(dictionary_list_items.get(getAbsoluteAdapterPosition()).getEnDefinition());
                    text_definition.setText(spanned);
                }
            });

            layout_uz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    layout_en.setBackgroundResource(R.drawable.tab_disable_leaderboard);
                    layout_uz.setBackgroundResource(R.drawable.tab_active_leaderboard);
                    layout_qr.setBackgroundResource(R.drawable.tab_disable_leaderboard);
                    layout_ru.setBackgroundResource(R.drawable.tab_disable_leaderboard);

                    /* change the color of the selected item text */
                    text_uz.setTextColor(activity.getResources().getColor(R.color.text_color_white));

                    /* change other text color */
                    text_en.setTextColor(activity.getResources().getColor(R.color.text_color_grey));
                    text_qr.setTextColor(activity.getResources().getColor(R.color.text_color_grey));
                    text_ru.setTextColor(activity.getResources().getColor(R.color.text_color_grey));

                    text_word.setText(dictionary_list_items.get(getAbsoluteAdapterPosition()).getUzWord());
                    Spanned spanned = Html.fromHtml(dictionary_list_items.get(getAbsoluteAdapterPosition()).getUzDefinition());
                    text_definition.setText(spanned);
                }
            });

            layout_qr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    layout_en.setBackgroundResource(R.drawable.tab_disable_leaderboard);
                    layout_uz.setBackgroundResource(R.drawable.tab_disable_leaderboard);
                    layout_qr.setBackgroundResource(R.drawable.tab_active_leaderboard);
                    layout_ru.setBackgroundResource(R.drawable.tab_disable_leaderboard);

                    /* change the color of the selected item text */
                    text_qr.setTextColor(activity.getResources().getColor(R.color.text_color_white));

                    /* change other text color */
                    text_uz.setTextColor(activity.getResources().getColor(R.color.text_color_grey));
                    text_en.setTextColor(activity.getResources().getColor(R.color.text_color_grey));
                    text_ru.setTextColor(activity.getResources().getColor(R.color.text_color_grey));

                    text_word.setText(dictionary_list_items.get(getAbsoluteAdapterPosition()).getQrWord());
                    Spanned spanned = Html.fromHtml(dictionary_list_items.get(getAbsoluteAdapterPosition()).getQrDefinition());
                    text_definition.setText(spanned);
                }
            });

            layout_ru.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    layout_en.setBackgroundResource(R.drawable.tab_disable_leaderboard);
                    layout_uz.setBackgroundResource(R.drawable.tab_disable_leaderboard);
                    layout_qr.setBackgroundResource(R.drawable.tab_disable_leaderboard);
                    layout_ru.setBackgroundResource(R.drawable.tab_active_leaderboard);

                    /* change the color of the selected item text */
                    text_ru.setTextColor(activity.getResources().getColor(R.color.text_color_white));

                    /* change other text color */
                    text_uz.setTextColor(activity.getResources().getColor(R.color.text_color_grey));
                    text_qr.setTextColor(activity.getResources().getColor(R.color.text_color_grey));
                    text_en.setTextColor(activity.getResources().getColor(R.color.text_color_grey));

                    text_word.setText(dictionary_list_items.get(getAbsoluteAdapterPosition()).getRuWord());
                    Spanned spanned = Html.fromHtml(dictionary_list_items.get(getAbsoluteAdapterPosition()).getRuDefinition());
                    text_definition.setText(spanned);
                }
            });

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup_window.dismiss();
                }
            });
        }
    }
}
