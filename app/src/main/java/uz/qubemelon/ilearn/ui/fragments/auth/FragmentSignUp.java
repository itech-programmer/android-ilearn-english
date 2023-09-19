package uz.qubemelon.ilearn.ui.fragments.auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.models.auth.AuthResponse;
import uz.qubemelon.ilearn.network.ErrorHandler;
import uz.qubemelon.ilearn.network.RetrofitClient;
import uz.qubemelon.ilearn.network.RetrofitInterface;
import uz.qubemelon.ilearn.ui.activities.AuthActivity;
import uz.qubemelon.ilearn.ui.activities.HomeActivity;
import uz.qubemelon.ilearn.utilities.Utility;
import uz.qubemelon.ilearn.utilities.Validator;

public class FragmentSignUp extends Fragment {

    /* global field instances */
    private Activity activity;
    private TextView text_sign_in, btn_sign_up;
    private EditText edit_full_name, edit_username, edit_email, edit_password, edit_confirm_password;

    public FragmentSignUp() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getActivity() != null && isAdded()) {
            activity = getActivity();
        }
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null && isAdded()) {
            activity = getActivity();
        }

        init_views();

        text_sign_in.setOnClickListener(view_sign_in -> {
            if (getActivity() != null && isAdded())
                ((AuthActivity) getActivity()).fragmentTransition(new FragmentSignIn());
        });

        //sign up button call
        btn_sign_up.setOnClickListener(view_sign_up -> {
            if (Utility.is_internet_available(activity)) {
                boolean isValid = Validator.validate_input_field(new EditText[]{
                                edit_full_name,
                                edit_username,
                                edit_password,
                                edit_confirm_password
                        },
                        activity);
                if (isValid) {
                    if (edit_confirm_password.getText().toString().equals(edit_password.getText().toString())) {
                        /* if everything is ok then call the do registration */
                            sign_up(
                                    edit_full_name.getText().toString(),
                                    edit_username.getText().toString(),
                                    edit_email.getText().toString(),
                                    edit_password.getText().toString(),
                                    edit_confirm_password.getText().toString()
                            );
                    } else {
                        Toast.makeText(activity, R.string.password_not_match,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(activity, R.string.no_internet,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*send the firebase token to the server*/
    private void add_user_to_firebase() {
        Storage storage = new Storage(activity);
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        Call<String> token_call = retrofit_interface.add_user_to_firebase(RetrofitClient.FIREBASE_ENDPOINT + "/" + storage.get_user_id() + "/" + storage.get_firebase_token(), storage.get_access_token());
        token_call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(activity, "Firebase success!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "Failed to add you in Firebase!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {
                /*handle network error and notify the user*/
                if (throwable instanceof SocketTimeoutException) {
                    Toast.makeText(activity, R.string.connection_timeout, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /* registration call */
    private void sign_up(String full_name, String username, String email, String password, String confirm_password) {
        final ProgressDialog dialog = Utility.show_dialog(activity);
        final Storage storage = new Storage(activity);
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        Call<String> do_registration_call = retrofit_interface.do_registration(full_name, username, email, password, confirm_password);
        do_registration_call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                /* globally handle error */
                ErrorHandler.get_instance().handle_error(response.code(), activity, dialog);
                if (response.body() != null && response.isSuccessful()) {
                    /*success true*/
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        boolean isSuccess = jsonObject.getBoolean("success");
                        if (isSuccess) {
                            /* serialize the String response  */
                            Gson gson = new Gson();
                            AuthResponse user_registration = gson.fromJson(response.body(), AuthResponse.class);
                            /* save the user data to local storage */
                            storage.save_user_id(user_registration.getUser().getId());
                            storage.save_full_name(user_registration.getUser().getFullName());
                            storage.save_access_token(user_registration.getAccessToken());
                            storage.save_access_type(user_registration.getAccessType());
                            storage.save_sign_in_sate(true);
                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);
                            /* show success message to user */
                            Toast.makeText(activity, "Registration success!", Toast.LENGTH_SHORT).show();

                            add_user_to_firebase();

                            /* take the user to the dashboard activity */
                            Intent intent = new Intent(activity, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            activity.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                            activity.finish();
                        } else {
                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);
                            /* get all the error messages and show to the user */
                            JSONArray jsonArray = jsonObject.getJSONArray("message");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Toast.makeText(activity, jsonArray.getString(i), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    /* dismiss the dialog */
                    Utility.dismiss_dialog(dialog);
                    Toast.makeText(activity, R.string.no_data_found, Toast.LENGTH_SHORT).show();
                }
            }

            /* handle error when you have any error */
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {
                /* dismiss the dialog */
                Utility.dismiss_dialog(dialog);
                /*handle network error and notify the user*/
                if (throwable instanceof SocketTimeoutException) {
                    Toast.makeText(activity, R.string.connection_timeout, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void init_views() {
        View view = getView();
        if (view != null) {
            edit_full_name = view.findViewById(R.id.edit_full_name);
            edit_full_name.setTag("Full Name");
            edit_username = view.findViewById(R.id.edit_username);
            edit_username.setTag("Username");
            edit_email = view.findViewById(R.id.edit_email);
            edit_username.setTag("Email");
            edit_password = view.findViewById(R.id.edit_password);
            edit_password.setTag("Password");
            edit_confirm_password = view.findViewById(R.id.edit_confirm_password);
            edit_confirm_password.setTag("Confirm Password");
            btn_sign_up = view.findViewById(R.id.btn_sign_up);
            text_sign_in = view.findViewById(R.id.text_sign_in);
        }
    }
}
