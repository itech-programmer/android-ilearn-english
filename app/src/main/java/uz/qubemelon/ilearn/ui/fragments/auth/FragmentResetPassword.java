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

public class FragmentResetPassword extends Fragment {

    private EditText edit_verify_code, edit_password, edit_confirm_password;
    private Button btn_reset;

    public FragmentResetPassword() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init_views();

        btn_reset.setOnClickListener(v -> {
            if (edit_verify_code.getText().toString().length() != 0) {
                if (edit_password.getText().length() >= 8) {
                    if (edit_password.getText().toString().equals(edit_confirm_password.getText().toString())) {
                        reset_password(edit_verify_code.getText().toString().trim(), edit_password.getText().toString().trim(), edit_confirm_password.getText().toString().trim());
                    } else {
                        Toast.makeText(getActivity(), "Confirm Password doesn't match!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Password can't be less than 8 character!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Verify code can't be empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reset_password(String access_code, String password, String confirm_password) {
        final ProgressDialog dialog = Utility.show_dialog(getActivity());
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        final Call<String> reset_password = retrofit_interface.reset_password(access_code, password, confirm_password);
        reset_password.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                ErrorHandler.get_instance().handle_error(response.code(), getActivity(), dialog);
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body());
                        if (jsonObject.getBoolean("success")) {
                            Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            ((AuthActivity) requireActivity()).fragmentTransition(new FragmentSignIn());
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
                if (throwable instanceof SocketTimeoutException) {
                    Utility.dismiss_dialog(dialog);
                    Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init_views() {
        View view = getView();
        if (view != null) {
            edit_verify_code = view.findViewById(R.id.edit_verify_code);
            edit_password = view.findViewById(R.id.edit_password);
            edit_confirm_password = view.findViewById(R.id.edit_confirm_password);
            btn_reset = view.findViewById(R.id.btn_reset);
        }
    }

}
