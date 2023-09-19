package uz.qubemelon.ilearn.ui.fragments.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.models.APIResponse;
import uz.qubemelon.ilearn.network.ErrorHandler;
import uz.qubemelon.ilearn.network.RetrofitClient;
import uz.qubemelon.ilearn.network.RetrofitInterface;
import uz.qubemelon.ilearn.services.CheckPermissionServices;
import uz.qubemelon.ilearn.ui.activities.AuthActivity;
import uz.qubemelon.ilearn.ui.activities.HomeActivity;
import uz.qubemelon.ilearn.ui.activities.SettingActivity;
import uz.qubemelon.ilearn.utilities.ImageFilePath;
import uz.qubemelon.ilearn.utilities.Utility;

public class FragmentProfile extends Fragment {

    /* all global field instances are here */
    private Activity activity;
    private ImageView image_avatar;
    private Button btn_logout;
    private LineChart graph_report;
    private ArrayList<Entry> line_chart_data;
    private APIResponse user_profile;
    private TextView text_user_total_point, total_point, text_full_name, text_ranking, text_challenges;
    private ImageView image_logout;
    private FloatingActionButton floating_action_button, fab_edit_profile, fab_edit_avatar, fab_settings;
    private View fab_background_layout;
    private LinearLayout fab_settings_layout, fab_edit_avatar_layout, fab_edit_profile_layout, fab_layout;
    private String[] labels;
    private boolean is_fab_open = false;
    private File avatar;

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

        Storage storage = new Storage(activity);
        if (storage.get_user_total_point() != 0){
            total_point.setText(String.valueOf(storage.get_user_total_point()));
        }

        line_chart_data = new ArrayList<>();

        get_profile_data();

        fab_settings.setOnClickListener(view_edit_avatar -> {
            Intent intent = new Intent(getActivity(), SettingActivity.class);
            startActivity(intent);
            if (getActivity() != null)
                getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            getActivity().finish();
        });

        fab_edit_avatar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                show_type_dialog();
            }
        });

        fab_edit_profile.setOnClickListener(view_edit_profile -> {
            ((HomeActivity) requireActivity()).fragment_transition(new FragmentEditProfile());
            close_fab_menu();
        });

        floating_action_button.setOnClickListener(view_fab -> {
            if (!is_fab_open) {
                show_fab_menu();
            } else {
                close_fab_menu();
            }
        });

        fab_background_layout.setOnClickListener(view_fab_background -> close_fab_menu());

        image_logout.setOnClickListener(view_logout -> {
            /* change the state of the logged in to log out for the current user */
            storage.save_sign_in_sate(false);
            /* take the current user to the log in page */
            Intent intent = new Intent(activity, AuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
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
                            user_profile = gson.fromJson(response.body(), APIResponse.class);
                            storage.save_user_full_name(user_profile.getProfile().getFullName());
                            /* simple data binding for profile section */
                            text_full_name.setText(user_profile.getProfile().getFullName());
                            storage.save_user_total_point(user_profile.getTotalPoint());

                            total_point.setText(String.valueOf(user_profile.getProfile().getTotalPoint()));
                            text_user_total_point.setText(String.valueOf(user_profile.getProfile().getTotalPoint()));

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

    private void show_type_dialog(){
        Intent camera_intent = new Intent();
        camera_intent.setType("image/*");
        camera_intent.setAction(Intent.ACTION_GET_CONTENT);
        activity_result.launch(camera_intent);
    }

    /*get the returned image*/
    protected ActivityResultLauncher<Intent> activity_result = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        avatar = new File(ImageFilePath.getPath(getActivity(), data.getData()));
                        if (avatar != null) {
                            Picasso.get().load(data.getData()).into(image_avatar);
                            update_avatar(avatar);
                            get_profile_data();
                        }
                    }
                }
            }
    );

    private void update_avatar(File file) {
        final ProgressDialog dialog = Utility.show_dialog(getActivity());
        final MediaType MEDIA_TYPE = MediaType.parse("image/*");
        Storage storage = new Storage(getActivity());
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        MultipartBody.Part image_part = MultipartBody.Part.createFormData("avatar",
                file.getName(), RequestBody.create(file, MEDIA_TYPE));
        Call<String> update_avatar_call = retrofit_interface.update_avatar(storage.get_access_token(), image_part);
        update_avatar_call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                /* handle error globally */
                ErrorHandler.get_instance().handle_error(response.code(), getActivity(), dialog);
                if (response.isSuccessful()) {
                    /* success true*/
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body());
                        boolean isSuccess = jsonObject.getBoolean("success");
                        if (isSuccess) {
                            /* serialize the String response */
                            String success_message = jsonObject.getString("message");

                            Toast.makeText(getActivity(), success_message, Toast.LENGTH_SHORT).show();
                            Utility.dismiss_dialog(dialog);

                            if (getActivity() != null && isAdded())
                                ((HomeActivity) getActivity()).fragment_transition(new FragmentProfile());

                        } else {
                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);
                            /*get all the error messages and show to the user*/
                            JSONArray messageArray = jsonObject.getJSONArray("message");
                            Toast.makeText(getActivity(), messageArray.getString(0), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    /* dismiss the dialog */
                    Utility.dismiss_dialog(dialog);
                    Toast.makeText(getActivity(), R.string.no_data_found, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {
                /*dismiss the dialog*/
                Utility.dismiss_dialog(dialog);
                if (throwable instanceof IOException) {
                    Log.e("MKIO", throwable.getMessage());
                }
                /*handle network error and notify the user*/
                if (throwable instanceof SocketTimeoutException) {
                    if (getActivity() != null && isAdded())
                        Toast.makeText(getActivity(), R.string.connection_timeout, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void show_fab_menu() {
        is_fab_open = true;
        fab_layout.setVisibility(View.VISIBLE);
        fab_settings_layout.setVisibility(View.VISIBLE);
        fab_edit_avatar_layout.setVisibility(View.VISIBLE);
        fab_edit_profile_layout.setVisibility(View.VISIBLE);
        fab_background_layout.setVisibility(View.VISIBLE);
        floating_action_button.animate().rotationBy(225);
        fab_settings_layout.animate().translationY(getResources().getDimension(R.dimen.standard_settings_layout));
        fab_edit_avatar_layout.animate().translationY(getResources().getDimension(R.dimen.standard_avatar_layout));
        fab_edit_profile_layout.animate().translationY(getResources().getDimension(R.dimen.standard_profile_layout));
    }

    private void close_fab_menu() {
        is_fab_open = false;
        fab_settings_layout.setVisibility(View.GONE);
        fab_edit_avatar_layout.setVisibility(View.GONE);
        fab_edit_profile_layout.setVisibility(View.GONE);
        fab_background_layout.setVisibility(View.GONE);
        floating_action_button.animate().rotationBy(225);
        fab_layout.animate().rotation(0);
        fab_edit_profile_layout.animate().translationY(getResources().getDimension(R.dimen.standard_0));
        fab_edit_avatar_layout.animate().translationY(getResources().getDimension(R.dimen.standard_0));
        fab_settings_layout.animate().translationY(getResources().getDimension(R.dimen.standard_0));
    }

    private void init_views() {
        View view = getView();
        if (view != null) {
            text_user_total_point = view.findViewById(R.id.text_user_total_point);
            total_point = view.findViewById(R.id.text_total_point);
            image_logout = view.findViewById(R.id.image_logout);
            text_full_name = view.findViewById(R.id.text_full_name);
            text_challenges = view.findViewById(R.id.text_challenges);
            text_ranking = view.findViewById(R.id.text_ranking);
            image_avatar = view.findViewById(R.id.image_avatar);
            graph_report = view.findViewById(R.id.graph_report);

            floating_action_button = view.findViewById(R.id.floating_action_button);
            fab_settings = view.findViewById(R.id.fab_settings);
            fab_edit_avatar = view.findViewById(R.id.fab_edit_avatar);
            fab_edit_profile = view.findViewById(R.id.fab_edit_profile);
            fab_background_layout = view.findViewById(R.id.fab_background_layout);
            fab_layout = view.findViewById(R.id.edit_layout);
            fab_settings_layout = view.findViewById(R.id.settings_layout);
            fab_edit_avatar_layout = view.findViewById(R.id.edit_avatar_layout);
            fab_edit_profile_layout = view.findViewById(R.id.edit_profile_layout);
        }
    }
}