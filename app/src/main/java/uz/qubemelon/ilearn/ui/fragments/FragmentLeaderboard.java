package uz.qubemelon.ilearn.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.adapters.LeaderboardAdapter;
import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.models.courses.CourseResponse;
import uz.qubemelon.ilearn.network.ErrorHandler;
import uz.qubemelon.ilearn.network.RetrofitClient;
import uz.qubemelon.ilearn.network.RetrofitInterface;
import uz.qubemelon.ilearn.utilities.Utility;

public class FragmentLeaderboard extends Fragment implements View.OnClickListener {

    /*all global field instances are here*/
    private Activity activity;
    private static final int ALL = 1;
    private static final int DAILY = 2;
    private static final int WEEKLY = 3;
    private LeaderboardAdapter leaderboard_adapter;
    private LinearLayoutCompat layout_all, layout_daily, layout_weekly;
    private RelativeLayout layout_message;
    private TextView text_all, text_all_user , text_daily, text_daily_top, text_weekly, text_weekly_top;
    private RecyclerView leaderboard_recycler_view;
    private CourseResponse leaderboard_list;

    public FragmentLeaderboard() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        if (getActivity() != null) {
            activity = getActivity();
        }
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init_views();
//        Storage storage = new Storage(activity);
//        text_total_point.setText(String.valueOf(storage.get_user_total_point()));
        get_leader_board_data(ALL);
    }

    private void get_leader_board_data(int type) {

        ProgressDialog dialog = Utility.show_dialog(activity);
        Storage storage = new Storage(activity);
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        Call<String> leaderboard_call = retrofit_interface.get_leaderboard_list(RetrofitClient.USERS + "/" + type + "/" + RetrofitClient.LEADERBOARD_URL, storage.get_access_token());
        leaderboard_call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                /* handle error globally */
                ErrorHandler.get_instance().handle_error(response.code(), activity, dialog);
                if (response.isSuccessful()) {
                    /* success true */
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body());
                        boolean isSuccess = jsonObject.getBoolean("success");
                        if (isSuccess) {

                            /* save leaderboard response to local storage */
                            storage.save_leaderboard_response(response.body());
                            layout_message.setVisibility(View.GONE);
                            leaderboard_recycler_view.setVisibility(View.VISIBLE);
                            /* serialize the String response */
                            Gson gson = new Gson();
                            leaderboard_list = gson.fromJson(response.body(), CourseResponse.class);
                            leaderboard_adapter = new LeaderboardAdapter(leaderboard_list.getLeaderboardList(), activity);
                            leaderboard_recycler_view.setAdapter(leaderboard_adapter);
                            Utility.dismiss_dialog(dialog);

                        } else {

                            layout_message.setVisibility(View.VISIBLE);
                            leaderboard_recycler_view.setVisibility(View.GONE);
                            /*dismiss the dialog*/
                            Utility.dismiss_dialog(dialog);
                            /*get all the error messages and show to the user*/
                            String message = jsonObject.getString("message");
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    /*dismiss the dialog*/
                    Utility.dismiss_dialog(dialog);
                    Toast.makeText(activity, R.string.no_data_found, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {
                /* dismiss the dialog */
                Utility.dismiss_dialog(dialog);
                /* handle network error and notify the user */
                if (throwable instanceof IOException) {
                    Toast.makeText(activity, R.string.connection_timeout, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.layout_all:

                get_leader_board_data(ALL);

                layout_all.setBackgroundResource(R.drawable.tab_active_leaderboard);
                layout_daily.setBackgroundResource(R.drawable.tab_disable_leaderboard);
                layout_weekly.setBackgroundResource(R.drawable.tab_disable_leaderboard);

                /* change the color of the selected item text */
                text_all.setTextColor(activity.getResources().getColor(R.color.text_color_white));
                text_all_user.setTextColor(activity.getResources().getColor(R.color.text_color_white));

                /* change other text color */
                text_daily.setTextColor(activity.getResources().getColor(R.color.text_color_grey));
                text_daily_top.setTextColor(activity.getResources().getColor(R.color.text_color_grey));
                text_weekly.setTextColor(activity.getResources().getColor(R.color.text_color_grey));
                text_weekly_top.setTextColor(activity.getResources().getColor(R.color.text_color_grey));

                break;

            case R.id.layout_daily:

                get_leader_board_data(DAILY);

                layout_all.setBackgroundResource(R.drawable.tab_disable_leaderboard);
                layout_daily.setBackgroundResource(R.drawable.tab_active_leaderboard);
                layout_weekly.setBackgroundResource(R.drawable.tab_disable_leaderboard);

                /* change the color of the selected item text */
                text_daily.setTextColor(activity.getResources().getColor(R.color.text_color_white));
                text_daily_top.setTextColor(activity.getResources().getColor(R.color.text_color_white));

                /* change other text color */
                text_all.setTextColor(activity.getResources().getColor(R.color.text_color_grey));
                text_all_user.setTextColor(activity.getResources().getColor(R.color.text_color_grey));
                text_weekly.setTextColor(activity.getResources().getColor(R.color.text_color_grey));
                text_weekly_top.setTextColor(activity.getResources().getColor(R.color.text_color_grey));

                break;

            case R.id.layout_weekly:

                get_leader_board_data(WEEKLY);

                layout_all.setBackgroundResource(R.drawable.tab_disable_leaderboard);
                layout_daily.setBackgroundResource(R.drawable.tab_disable_leaderboard);
                layout_weekly.setBackgroundResource(R.drawable.tab_active_leaderboard);

                /* change the color of the selected item text */
                text_weekly.setTextColor(activity.getResources().getColor(R.color.text_color_white));
                text_weekly_top.setTextColor(activity.getResources().getColor(R.color.text_color_white));

                /* change other text color */
                text_all.setTextColor(activity.getResources().getColor(R.color.text_color_grey));
                text_all_user.setTextColor(activity.getResources().getColor(R.color.text_color_grey));
                text_daily.setTextColor(activity.getResources().getColor(R.color.text_color_grey));
                text_daily_top.setTextColor(activity.getResources().getColor(R.color.text_color_grey));

                break;
        }
    }

    private void init_views() {
        View view = getView();
        if (view != null) {
            layout_all = view.findViewById(R.id.layout_all);
            layout_all.setOnClickListener(this);
            layout_daily = view.findViewById(R.id.layout_daily);
            layout_daily.setOnClickListener(this);
            layout_weekly = view.findViewById(R.id.layout_weekly);
            layout_weekly.setOnClickListener(this);
            text_all = view.findViewById(R.id.text_all);
            text_all_user = view.findViewById(R.id.text_all_user);
            text_daily = view.findViewById(R.id.text_daily);
            text_daily_top = view.findViewById(R.id.text_daily_top);
            text_weekly = view.findViewById(R.id.text_weekly);
            text_weekly_top = view.findViewById(R.id.text_weekly_top);
            leaderboard_recycler_view = view.findViewById(R.id.leaderboard_recycler_view);
            layout_message = view.findViewById(R.id.layout_message);
        }
    }

}