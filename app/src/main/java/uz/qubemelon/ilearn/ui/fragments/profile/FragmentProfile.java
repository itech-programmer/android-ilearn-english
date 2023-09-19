package uz.qubemelon.ilearn.ui.fragments.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.models.courses.CourseResponse;
import uz.qubemelon.ilearn.network.ErrorHandler;
import uz.qubemelon.ilearn.network.RetrofitClient;
import uz.qubemelon.ilearn.network.RetrofitInterface;
import uz.qubemelon.ilearn.utilities.Utility;

public class FragmentProfile extends Fragment {

    /* all global field instances are here */
    private Activity activity;
    private ImageView image_avatar;
    private Button btn_logout;
    private LineChart graph_report;
    private ArrayList<Entry> line_chart_data;
    private CourseResponse user_profile;
    private TextView text_total_point, text_full_name, text_ranking, text_challenges;
    private String[] labels;

    public FragmentProfile() {
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init_views();

        line_chart_data = new ArrayList<>();
//        Storage storage = new Storage(activity);
//        text_total_point.setText(String.valueOf(storage.get_user_total_point()));

        get_profile_data();
    }

    private void get_profile_data() {
        ProgressDialog dialog = Utility.show_dialog(getActivity());
        Storage storage = new Storage(getActivity());
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        final Call<String> profileCall = retrofit_interface.get_profile(storage.get_access_token());
        profileCall.enqueue(new Callback<String>() {

            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                /* handle error globally */
                ErrorHandler.get_instance().handle_error(response.code(), getActivity(), dialog);
                if (response.isSuccessful()) {
                    /*success true*/
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body());
                        boolean isSuccess = jsonObject.getBoolean("success");
                        if (isSuccess) {
                            /* serialize the String response */
                            Gson gson = new Gson();
                            user_profile = gson.fromJson(response.body(), CourseResponse.class);
                            storage.save_user_full_name(user_profile.getProfile().getFullName());
                            /* simple data binding for profile section */
                            text_full_name.setText(user_profile.getProfile().getFullName());
                            text_total_point.setText(user_profile.getProfile().getPoint());
                            text_ranking.setText(String.valueOf(user_profile.getProfile().getRanking()));
                            text_challenges.setText(String.valueOf(user_profile.getProfile().getParticipatedQuestions()));
                            Picasso.get().load(user_profile.getProfile().getAvatar()).placeholder(R.drawable.ic_boy).into(image_avatar);

                            labels = new String[user_profile.getProfile().getDailyScore().size()];

                            /* make the chart */
                            for (int i = 0; i < user_profile.getProfile().getDailyScore().size(); i++) {
                                line_chart_data.add(new Entry((float) user_profile.getProfile().getDailyScore().get(i).getScorePercentage(), i));
                                labels[i] = Utility.get_formatted_date(user_profile.getProfile().getDailyScore().get(i).getDate());

                            }
                            if (labels.length != 0 && line_chart_data.size() != 0) {
                                chart_builder(labels, line_chart_data);
                            }

                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);

                        } else {
                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);
                            /* get all the error messages and show to the user */
                            String message = jsonObject.getString("message");
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private void chart_builder(String[] labels, ArrayList<Entry> line_chart_data) {
        LineDataSet set_data = new LineDataSet(line_chart_data, ""); // add entries to dataset
        set_data.setFillDrawable(getResources().getDrawable(R.drawable.gradient_graph));
        set_data.setDrawCircleHole(false);

        set_data.setDrawCircles(false);
        set_data.setDrawHighlightIndicators(false);
        set_data.setDrawValues(false);
        set_data.setDrawFilled(true);
        set_data.setDrawCubic(true);


        LineData lineData = new LineData(labels, set_data);
        graph_report.setData(lineData);
        graph_report.invalidate();


        graph_report.getLegend().setEnabled(false);
        graph_report.getAxisRight().setEnabled(false);
        graph_report.getAxisLeft().setDrawGridLines(false);
        graph_report.getXAxis().setDrawGridLines(false);



        XAxis xAxis = graph_report.getXAxis();
        xAxis.setXOffset(0.2f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    private void init_views() {
        View view = getView();
        if (view != null) {
            text_total_point = view.findViewById(R.id.text_total_point);
            text_full_name = view.findViewById(R.id.text_full_name);
            text_challenges = view.findViewById(R.id.text_challenges);
            text_ranking = view.findViewById(R.id.text_ranking);
            image_avatar = view.findViewById(R.id.image_avatar);
            graph_report = view.findViewById(R.id.graph_report);
        }
    }
}