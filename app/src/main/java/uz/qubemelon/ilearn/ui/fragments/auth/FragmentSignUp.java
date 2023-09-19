package uz.qubemelon.ilearn.ui.fragments.auth;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.ui.activities.AuthActivity;
import uz.qubemelon.ilearn.utilities.Utility;
import uz.qubemelon.ilearn.utilities.Validator;

public class FragmentSignUp extends Fragment {

    /* global field instances */
    private Activity activity;
    private TextView text_sign_in, btn_sign_up;
    private EditText edit_full_name, edit_username, edit_password, edit_confirm_password;

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

        text_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null && isAdded())
                    ((AuthActivity) getActivity()).fragmentTransition(new FragmentSignIn());
            }
        });

        //        sign up button call
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                            /*if everything is ok then call the do registration */
//                            sign_up(
//                                    edit_full_name.getText().toString(),
//                                    edit_username.getText().toString(),
//                                    edit_password.getText().toString(),
//                                    edit_confirm_password.getText().toString()
//                            );
                        } else {
                            Toast.makeText(activity, R.string.password_not_match,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(activity, R.string.no_internet,
                            Toast.LENGTH_SHORT).show();
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
            edit_password = view.findViewById(R.id.edit_password);
            edit_password.setTag("Password");
            edit_confirm_password = view.findViewById(R.id.edit_confirm_password);
            edit_confirm_password.setTag("Confirm Password");
            btn_sign_up = view.findViewById(R.id.btn_sign_up);
            text_sign_in = view.findViewById(R.id.text_sign_in);
        }
    }
}
