package uz.qubemelon.ilearn.ui.fragments.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;

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
import uz.qubemelon.ilearn.ui.activities.HomeActivity;
import uz.qubemelon.ilearn.utilities.Utility;

public class FragmentEditProfile extends Fragment {

    /* all global field instances are here */
    private Activity activity;
    private ImageView image_avatar;
    private APIResponse user_profile;
    private TextView text_total_point, text_full_name, text_ranking, text_challenges;
    private TextInputEditText edit_full_name, edit_username;
    private Button btn_save;

    public FragmentEditProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init_views();
        get_profile_data();

        btn_save.setOnClickListener(view_save -> {
            if (edit_full_name.getText().toString().length() != 0) {
                if (edit_username.getText().toString().length() != 0) {
                        set_profile_data(edit_full_name.getText().toString(), edit_username.getText().toString());
                        ((HomeActivity) requireActivity()).fragment_transition(new FragmentProfile());
                } else {
                    Toast.makeText(getActivity(), "Username Can't be empty!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Full Name Can't be empty!", Toast.LENGTH_SHORT).show();
            }
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
                            edit_full_name.setText(user_profile.getProfile().getFullName());
                            edit_username.setText(user_profile.getProfile().getUsername());
                            text_ranking.setText(String.valueOf(user_profile.getProfile().getRanking()));
                            text_challenges.setText(String.valueOf(user_profile.getProfile().getParticipatedQuestions()));
                            Picasso.get().load(user_profile.getProfile().getAvatar()).placeholder(R.drawable.ic_boy).into(image_avatar);

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

    private void set_profile_data(String full_name, String username){
        final ProgressDialog dialog = Utility.show_dialog(getActivity());
        Storage storage = new Storage(getActivity());
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        Call<String> update_call = retrofit_interface.update_profile(storage.get_access_token(), full_name, username);
        update_call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                /* handle error globally */
                ErrorHandler.get_instance().handle_error(response.code(), getActivity(), dialog);
                if (response.isSuccessful()) {
                    /* success true */
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
                            /* get all the error messages and show to the user */
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
                /* dismiss the dialog */
                Utility.dismiss_dialog(dialog);
                if (throwable instanceof IOException) {
                    Log.e("ERROR", throwable.getMessage());
                }
                /* handle network error and notify the user */
                if (throwable instanceof SocketTimeoutException) {
                    if (getActivity() != null && isAdded())
                        Toast.makeText(getActivity(), R.string.connection_timeout, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private File get_file_from_uri(Uri uri) {
        String filePath = null;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        cursor = requireActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        int columnIndex = 0;
        if (cursor != null) {
            columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        }
        if (cursor != null) {

            filePath = cursor.getString(columnIndex);
        }
        if (cursor != null) {
            cursor.close();
        }

        File file = null;
        if (filePath != null) {
            file = new File(filePath);
        }

        return file;

    }

    private void init_views() {
        View view = getView();
        if (view != null) {
            text_total_point = view.findViewById(R.id.text_total_point);
            text_full_name = view.findViewById(R.id.text_full_name);
            text_challenges = view.findViewById(R.id.text_challenges);
            text_ranking = view.findViewById(R.id.text_ranking);
            image_avatar = view.findViewById(R.id.image_avatar);

            edit_full_name = view.findViewById(R.id.edit_full_name);
            edit_username = view.findViewById(R.id.edit_username);
            btn_save = view.findViewById(R.id.btn_save);
        }
    }
}
