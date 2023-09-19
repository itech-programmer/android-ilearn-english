package uz.qubemelon.ilearn.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SwitchCompat;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.models.APIResponse;
import uz.qubemelon.ilearn.network.ErrorHandler;
import uz.qubemelon.ilearn.network.RetrofitClient;
import uz.qubemelon.ilearn.network.RetrofitInterface;

public class SettingActivity extends Activity {

    private SwitchCompat sound;
    private LinearLayoutCompat change_language;
    private TextView user_language, text_sound;
    private APIResponse user_setting;
    private String[] lang_setting_in_array;
    private String user_language_setting;
    private LinearLayout btn_privacy_policy, btn_terms_of_condition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init_views();

        /* get the language list from server */
        get_user_setting();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);// SDK21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        final Storage storage = new Storage(this);

//        btn_terms_of_condition.setOnClickListener(view_conditions -> {
//                Intent intent = new Intent(SettingActivity.this, PrivacyPolicyActivity.class);
//                intent.putExtra("URL", "http://");
//                intent.putExtra("TITLE","Terms & Conditions");
//                startActivity(intent);
//        });
//
//        btn_privacy_policy.setOnClickListener(view_policy -> {
//                Intent intent = new Intent(SettingActivity.this, PrivacyPolicyActivity.class);
//                intent.putExtra("URL", "http://");
//                intent.putExtra("TITLE","Privacy & Policy");
//                startActivity(intent);
//        });

        /* check the state of sound and set value upon it the setting */
        if (storage.get_sound_state()) {
            text_sound.setText("ON");
            sound.setChecked(true);
        } else {
            text_sound.setText("OFF");
            sound.setChecked(false);
        }

        change_language.setOnClickListener(view_change_language -> {
            on_change_language();
        });

        if (storage.get_user_language() != null){
            user_language.setText(storage.get_user_language());
        }else {
            user_language.setText("English");
        }

        /*change the state of the sound on user input*/
        sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    text_sound.setText("ON");
                    storage.save_sound_state(true);
                } else {
                    text_sound.setText("OFF");
                    storage.save_sound_state(false);
                }
            }
        });
    }

    /* the language list of setting */
    private void get_user_setting() {
        Storage storage = new Storage(this);
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        Call<String> user_setting_call = retrofit_interface.get_user_setting(storage.get_access_token());
        user_setting_call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                /* handle error globally */
                ErrorHandler.get_instance().handle_error(response.code(), SettingActivity.this, null);

                if (response.isSuccessful()) {
                    /* success true */
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body());
                        boolean isSuccess = jsonObject.getBoolean("success");
                        if (isSuccess) {
                            /* serialize the String response */
                            Gson gson = new Gson();
                            user_setting = gson.fromJson(response.body(), APIResponse.class);
                            storage.save_languages_response(response.body());
                            storage.save_user_language(user_setting.getProfile().getLanguage());

                            user_language.setText(user_setting.getProfile().getLanguage());

                            lang_setting_in_array = new String[user_setting.getLanguages().size()];

                            for (int i = 0; i < user_setting.getLanguages().size(); i++) {
                                lang_setting_in_array[i] = user_setting.getLanguages().get(i).getValue();
                            }

                        } else {
                            /* dismiss the dialog */

                            /* get all the error messages and show to the user */
                            String message = jsonObject.getString("message");
                            Toast.makeText(SettingActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(SettingActivity.this, R.string.no_data_found, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {

                /*handle network error and notify the user*/
                if (throwable instanceof IOException) {
                    Toast.makeText(SettingActivity.this, R.string.connection_timeout, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void on_change_language() {
        if (user_setting == null) {
            /* again call to get the language list */
            get_user_setting();

            final AlertDialog.Builder showNoDataFound = new AlertDialog.Builder(SettingActivity.this);
            showNoDataFound.setMessage("No Language Found");
            showNoDataFound.setIcon(android.R.drawable.stat_sys_warning);
            showNoDataFound.show();
            showNoDataFound.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

        } else {
            //String[] list = new String[]{"Bangladesh", "India", "USA", "Portugal"};
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setItems(lang_setting_in_array, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    post_user_language(user_setting.getLanguages().get(i).getKey());
                    String set_language = user_setting.getLanguages().get(i).getKey();
                    if (set_language.equals("oz")){
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
                        set_language("uz");
                    } else if (set_language.equals("qr")){
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
                        set_language("qr");
                    } else if (set_language.equals("ru")){
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
                        set_language("ru");
                    } else {
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
                        set_language("en");
                    }
                }
            });
            builder.setTitle(R.string.select_your_language);
            builder.show();
        }
    }

    public void set_language(String language) {
        Configuration config = getResources().getConfiguration();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        recreate();
    }

    /* update the language of the app by taking input of language key */
    private void post_user_language(String language_key) {

        Storage storage = new Storage(SettingActivity.this);
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        Call<String> langCall = retrofit_interface.post_user_language(storage.get_access_token(), language_key);
        langCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                /* handle error globally */
                ErrorHandler.get_instance().handle_error(response.code(), SettingActivity.this, null);

                if (response.isSuccessful()) {
                    /* success true */
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body());
                        boolean isSuccess = jsonObject.getBoolean("success");
                        if (isSuccess) {
                            /* dismiss the dialog */
                            get_user_setting();
                            /* get all the error messages and show to the user */
                            String message = jsonObject.getString("message");
                            Toast.makeText(SettingActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            /* dismiss the dialog */
                            /* get all the error messages and show to the user */
                            JSONArray message = jsonObject.getJSONArray("message");
                            Toast.makeText(SettingActivity.this, message.getString(0), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(SettingActivity.this, R.string.no_data_found, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {
                /* handle network error and notify the user */
                if (throwable instanceof IOException) {
                    Toast.makeText(SettingActivity.this, R.string.connection_timeout, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init_views() {
        /* view type casting */
        sound = findViewById(R.id.switch_sound);
        change_language = findViewById(R.id.change_language);
        user_language = findViewById(R.id.user_language);
        text_sound = findViewById(R.id.text_sound);
//        btn_privacy_policy = findViewById(R.id.btn_privacy_policy);
//        btn_terms_of_condition = findViewById(R.id.btn_terms_of_conditions);
    }

    /* when user press the back button take him to the main activity */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
