package uz.qubemelon.ilearn.ui.fragments.auth;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.network.ErrorHandler;
import uz.qubemelon.ilearn.network.RetrofitClient;
import uz.qubemelon.ilearn.network.RetrofitInterface;
import uz.qubemelon.ilearn.ui.activities.AuthActivity;
import uz.qubemelon.ilearn.utilities.Utility;
import uz.qubemelon.ilearn.utilities.Validator;

public class FragmentForgotPassword extends Fragment {

    private EditText edit_email;
    private Button btn_done;

    public FragmentForgotPassword() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init_views();

        btn_done.setOnClickListener(v -> {
            if (edit_email.getText().toString().trim().length() != 0) {
                if (edit_email.getText().toString().matches(Validator.EMAIL_VERIFICATION)) {
                    send_reset_code(edit_email.getText().toString().trim());
                } else {
                    Toast.makeText(getActivity(), "Invalid Email!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Email can't be empty!", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void send_reset_code(String email) {
        final ProgressDialog dialog = Utility.show_dialog(getActivity());
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        Call<String> forgot_password_call = retrofit_interface.forgot_password(email);
        forgot_password_call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                ErrorHandler.get_instance().handle_error(response.code(), getActivity(), dialog);

                if (response.isSuccessful()) {

                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body());
                        if (jsonObject.getBoolean("success")) {
                            if (getActivity() != null) {
                                ((AuthActivity) getActivity()).fragmentTransition(new FragmentResetPassword());
                                Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getActivity(), R.string.no_data_found, Toast.LENGTH_SHORT).show();
                }
                Utility.dismiss_dialog(dialog);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {
                Utility.dismiss_dialog(dialog);
                if (throwable instanceof SocketTimeoutException) {
                    Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init_views() {
        View view = getView();
        if (view != null) {
            edit_email = view.findViewById(R.id.edit_email);
            btn_done = view.findViewById(R.id.btn_done);
        }
    }
}
