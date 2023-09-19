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

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.models.user.UserSignIn;
import uz.qubemelon.ilearn.network.ErrorHandler;
import uz.qubemelon.ilearn.network.RetrofitClient;
import uz.qubemelon.ilearn.network.RetrofitInterface;
import uz.qubemelon.ilearn.ui.activities.AuthActivity;
import uz.qubemelon.ilearn.ui.activities.HomeActivity;
import uz.qubemelon.ilearn.utilities.Utility;
import uz.qubemelon.ilearn.utilities.Validator;

public class FragmentSignIn extends Fragment {

    private Activity activity;
    private TextInputEditText edit_username, edit_password;
    private TextView text_sign_up, btn_sign_in;

    public FragmentSignIn() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getActivity() != null && isAdded()) {
            activity = getActivity();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null && isAdded()) {
            activity = getActivity();
        }

        init_views();

        text_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AuthActivity) requireActivity())
                        .fragmentTransition(new FragmentSignUp());
            }
        });

        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.is_internet_available(activity)) {
                    if (Validator.validate_input_field(new EditText[]{edit_username, edit_password},
                            activity)) {
                        sign_in(Objects.requireNonNull(edit_username.getText()).toString().trim(),
                                Objects.requireNonNull(edit_password.getText()).toString().trim());
                    }
                } else {
                    Toast.makeText(activity,
                            R.string.no_internet,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /* sign in in api call */
    private void sign_in(String username, String password) {
        Storage storage = new Storage(activity);
        ProgressDialog dialog = Utility.show_dialog(activity);
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        Call<String> do_login_call = retrofit_interface.doLogin(username, password);
        do_login_call.enqueue(new Callback<String>() {

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
                            /* serialize the String response */
                            Gson gson = new Gson();
                            UserSignIn user_sign_in = gson.fromJson(response.body(), UserSignIn.class);

                            /* save the user data to local storage */
                            storage.save_access_type(user_sign_in.getAccessType());
                            storage.save_access_token(user_sign_in.getAccessToken());
                            storage.save_sign_in_sate(true);
                            storage.save_full_name(user_sign_in.getUser().getFullName());
                            storage.save_user_total_point(Integer.parseInt(user_sign_in.getTotalPoint()));

                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);
                            /* take the user to the dashboard activity */
                            Intent intent = new Intent(activity, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            activity.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                            activity.finish();

                        }else {

                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);
                            /* get all the error messages and show to the user */
                            String message = jsonObject.getString("message");
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    /* dismiss the dialog */
                    Utility.dismiss_dialog(dialog);
                    Toast.makeText(activity, R.string.no_data_found, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {
                /* dismiss the dialog */
                Utility.dismiss_dialog(dialog);
                /* handle network error and notify the user */
                if (throwable instanceof SocketTimeoutException) {
                    Toast.makeText(activity, R.string.connection_timeout, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /* view type casting */
    private void init_views() {
        View view = getView();
        if (view != null) {
            btn_sign_in = view.findViewById(R.id.btn_sign_in);
            edit_username = view.findViewById(R.id.edit_username);
            edit_username.setTag("Username");
            edit_password = view.findViewById(R.id.edit_password);
            edit_password.setTag("Password");
            text_sign_up = view.findViewById(R.id.text_sign_up);
        }
    }
}
