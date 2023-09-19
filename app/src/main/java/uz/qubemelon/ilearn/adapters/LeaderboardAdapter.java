package uz.qubemelon.ilearn.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.models.user.LeaderboardList;

public class LeaderboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // global field instances
    private List<LeaderboardList> leaderboard_list_items;
    private Activity activity;
    private int HEADER = 1;
    private int REGULAR = 2;

    /* this is the constructor for the leaderboard */
    public LeaderboardAdapter(List<LeaderboardList> leaderboard_list_items, Activity activity) {
        this.leaderboard_list_items = leaderboard_list_items;
        leaderboard_list_items.add(0, null);
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int view_type) {
        if (view_type == HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_header, parent, false);
            return new LeaderBoardHeader(view);
        } else if (view_type == REGULAR) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
            return new LeaderboardHolder(view);

        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LeaderboardHolder) {
            LeaderboardHolder leaderboard_holder = (LeaderboardHolder) holder;
            Picasso.get().load(leaderboard_list_items.get(position).getAvatar()).placeholder(R.drawable.ic_boy).into(leaderboard_holder.image_avatar);
            leaderboard_holder.text_leaderboard_full_name.setText(leaderboard_list_items.get(position).getFullName());
            leaderboard_holder.text_leaderboard_rank.setText(String.valueOf(leaderboard_list_items.get(position).getUserRank()));
            leaderboard_holder.text_leaderboard_point.setText(leaderboard_list_items.get(position).getUserScore());
            leaderboard_holder.text_leaderboard_point.setTextColor(activity.getResources().getColor(R.color.text_color_primary));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else {
            return REGULAR;
        }
    }

    @Override
    public int getItemCount() {
        return leaderboard_list_items.size();
    }

    public class LeaderboardHolder extends RecyclerView.ViewHolder {

        private ImageView image_avatar;
        private TextView text_leaderboard_full_name, text_leaderboard_rank, text_leaderboard_point;

        public LeaderboardHolder(@NonNull View item_view) {
            super(item_view);
            image_avatar = itemView.findViewById(R.id.image_avatar);
            text_leaderboard_full_name = itemView.findViewById(R.id.text_leaderboard_full_name);
            text_leaderboard_rank = itemView.findViewById(R.id.text_leaderboard_rank);
            text_leaderboard_point = itemView.findViewById(R.id.text_leaderboard_point);
        }
    }

    public class LeaderBoardHeader extends RecyclerView.ViewHolder {

        public LeaderBoardHeader(View itemView) {
            super(itemView);
        }
    }

}
