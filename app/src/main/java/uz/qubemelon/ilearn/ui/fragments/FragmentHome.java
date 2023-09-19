package uz.qubemelon.ilearn.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.adapters.courses.GrammarChaptersAdapter;
import uz.qubemelon.ilearn.adapters.courses.VocabularyChaptersAdapter;
import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.models.APIResponse;
import uz.qubemelon.ilearn.network.ErrorHandler;
import uz.qubemelon.ilearn.network.RetrofitClient;
import uz.qubemelon.ilearn.network.RetrofitInterface;
import uz.qubemelon.ilearn.ui.activities.AuthActivity;
import uz.qubemelon.ilearn.utilities.Utility;

public class FragmentHome extends Fragment {

    /* all global field instances are here */
    private Activity activity;
    private TextView text_hello, text_full_name, total_point;
    private TextView oz_language, qr_language, ru_language, en_language;
    private ImageView image_logout;
    private FloatingActionButton fab_language;
    private Storage languages_storage;
    private Storage chapters_storage;
    private RelativeLayout layout_home;
    private VocabularyChaptersAdapter vocabulary_chapters_adapter;
    private GrammarChaptersAdapter grammar_chapters_adapter;
    private RecyclerView vocabulary_chapters_recycler_view, grammar_chapters_recycler_view;
    private AppCompatButton btn_vocabulary, btn_grammar;
    private APIResponse chapter_list;
    private APIResponse user_language;
    private String[] lang_setting_in_array;

    public FragmentHome() {
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    init_views();

        Storage storage = new Storage(activity);
        if (storage.get_user_total_point() != 0){
            total_point.setText(String.valueOf(storage.get_user_total_point()));
        }

    chapters_storage = new Storage(activity);
        /* simple data binding for profile section */
        if (chapters_storage.get_user_full_name() != null) {
            text_hello.setText(String.valueOf("Welcome back"));
            text_full_name.setText(chapters_storage.get_user_full_name());
        }

        /* adding options to category recycler */
        GridLayoutManager vocabulary_grid_layout_manager = new GridLayoutManager(getActivity(), 2);
        vocabulary_chapters_recycler_view.setHasFixedSize(true);
        vocabulary_chapters_recycler_view.setLayoutManager(vocabulary_grid_layout_manager);

        /* if there is internet call get data from server, otherwise load for local response */
        if (Utility.is_internet_available(activity)) {
            get_vocabulary_chapters();
            get_user_language();
        }else {
            if (chapters_storage.get_vocabulary_chapters_response() != null) {
                /* load data from offline */
                /* serialize the String response */
                Gson gson = new Gson();
                chapter_list = gson.fromJson(chapters_storage.get_vocabulary_chapters_response(), APIResponse.class);
                /* add the data to the recycler view */
                if (chapter_list.getVocabularyChapterList() != null) {
                    vocabulary_chapters_adapter = new VocabularyChaptersAdapter(chapter_list.getVocabularyChapterList(), activity);
                    vocabulary_chapters_recycler_view.setAdapter(vocabulary_chapters_adapter);
                }
            }
        }
        image_logout.setOnClickListener(view_logout -> {
            /* change the state of the logged in to log out for the current user */
            storage.save_sign_in_sate(false);
            /* take the current user to the log in page */
            Intent intent = new Intent(activity, AuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
            btn_vocabulary.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View v) {
                    if (Utility.is_internet_available(activity)) {
                        btn_vocabulary.setTextColor(getResources().getColor(R.color.text_color_white));
                        btn_vocabulary.setBackgroundResource(R.drawable.tab_active_leaderboard);
                        btn_grammar.setTextColor(getResources().getColor(R.color.text_color_primary));
                        btn_grammar.setBackgroundResource(getResources().getColor(R.color.transparent));

                        /* if there is internet call get data from server, otherwise load for local response */
                        if (Utility.is_internet_available(activity)) {
                            get_vocabulary_chapters();
                        }else {

                            if (chapters_storage.get_vocabulary_chapters_response() != null) {
                                /* load data from offline */
                                /* serialize the String response */
                                Gson gson = new Gson();
                                chapter_list = gson.fromJson(chapters_storage.get_vocabulary_chapters_response(), APIResponse.class);
                                /* add the data to the recycler view */
                                if (chapter_list.getVocabularyChapterList() != null) {
                                    vocabulary_chapters_adapter = new VocabularyChaptersAdapter(chapter_list.getVocabularyChapterList(), activity);
                                    vocabulary_chapters_recycler_view.setAdapter(vocabulary_chapters_adapter);
                                }
                            }
                        }

                        vocabulary_chapters_recycler_view.setVisibility(View.VISIBLE);
                        grammar_chapters_recycler_view.setVisibility(View.GONE);
                    } else {
                        btn_vocabulary.setTextColor(getResources().getColor(R.color.text_color_white));
                        btn_vocabulary.setBackgroundResource(R.drawable.tab_active_leaderboard);
                        btn_grammar.setTextColor(getResources().getColor(R.color.text_color_primary));
                        btn_grammar.setBackgroundResource(getResources().getColor(R.color.transparent));

                       vocabulary_chapters_recycler_view.setVisibility(View.VISIBLE);
                       grammar_chapters_recycler_view.setVisibility(View.GONE);
                    }
                }
            });

            btn_grammar.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View v) {
                    if (Utility.is_internet_available(activity)) {
                        btn_vocabulary.setTextColor(getResources().getColor(R.color.text_color_primary));
                        btn_vocabulary.setBackgroundResource(getResources().getColor(R.color.transparent));
                        btn_grammar.setTextColor(getResources().getColor(R.color.text_color_white));
                        btn_grammar.setBackgroundResource(R.drawable.tab_active_leaderboard);

                        /* if there is internet call get data from server, otherwise load for local response */
                        if (Utility.is_internet_available(activity)) {
                            get_grammar_chapters();
                        }else {
                            if (chapters_storage.get_grammar_chapters_response() != null) {
                                /* load data from offline */
                                /* serialize the String response */
                                Gson gson = new Gson();
                                chapter_list = gson.fromJson(chapters_storage.get_grammar_chapters_response(), APIResponse.class);
                                /* add the data to the recycler view */
                                if (chapter_list.getGrammarChapterList() != null) {
                                    grammar_chapters_adapter = new GrammarChaptersAdapter(chapter_list.getGrammarChapterList(), activity);
                                    grammar_chapters_recycler_view.setAdapter(grammar_chapters_adapter);
                                }
                            }
                        }
                        vocabulary_chapters_recycler_view.setVisibility(View.GONE);
                        grammar_chapters_recycler_view.setVisibility(View.VISIBLE);
                    } else {
                        btn_vocabulary.setTextColor(getResources().getColor(R.color.text_color_primary));
                        btn_vocabulary.setBackgroundResource(getResources().getColor(R.color.transparent));
                        btn_grammar.setTextColor(getResources().getColor(R.color.text_color_white));
                        btn_grammar.setBackgroundResource(R.drawable.tab_active_leaderboard);

                        vocabulary_chapters_recycler_view.setVisibility(View.GONE);
                        grammar_chapters_recycler_view.setVisibility(View.VISIBLE);
                    }
                }
            });

        fab_language.setOnClickListener(view_language ->{
            /* if there is internet call get data from server, otherwise load for local response */
            if (Utility.is_internet_available(activity)) {
                on_change_language();
            } else {
                Toast.makeText(activity, R.string.connection_timeout, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* the language list of setting */
    private void get_user_language() {
        Storage storage = new Storage(activity);
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        Call<String> user_language_call = retrofit_interface.get_user_setting(storage.get_access_token());
        user_language_call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                /* handle error globally */
                ErrorHandler.get_instance().handle_error(response.code(), activity, null);

                if (response.isSuccessful()) {
                    /* success true */
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body());
                        boolean isSuccess = jsonObject.getBoolean("success");
                        if (isSuccess) {
                            /* serialize the String response */
                            Gson gson = new Gson();
                            user_language = gson.fromJson(response.body(), APIResponse.class);
                            storage.save_languages_response(response.body());
                            storage.save_user_language(user_language.getProfile().getLanguage());

                            lang_setting_in_array = new String[user_language.getLanguages().size()];

                            for (int i = 0; i < user_language.getLanguages().size(); i++) {
                                lang_setting_in_array[i] = user_language.getLanguages().get(i).getValue();
                            }

                        } else {
                            /* dismiss the dialog */

                            /* get all the error messages and show to the user */
                            String message = jsonObject.getString("message");
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(activity, R.string.no_data_found, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {

                /*handle network error and notify the user*/
                if (throwable instanceof IOException) {
                    Toast.makeText(activity, R.string.connection_timeout, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void on_change_language() {
        if (user_language == null) {
            /* again call to get the language list */
            get_user_language();

            final AlertDialog.Builder showNoDataFound = new AlertDialog.Builder(activity);
            showNoDataFound.setMessage(R.string.connection_timeout);
            showNoDataFound.setIcon(android.R.drawable.stat_sys_warning);
            showNoDataFound.show();
            showNoDataFound.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            Toast.makeText(activity, R.string.connection_timeout, Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setItems(lang_setting_in_array, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    post_user_language(user_language.getLanguages().get(i).getKey());
                    String set_language = user_language.getLanguages().get(i).getKey();
                    if (set_language.equals("oz")){
                        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString("LANG", "en").commit();
                        set_language("uz");
                    } else if (set_language.equals("qr")){
                        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString("LANG", "en").commit();
                        set_language("kaa");
                    } else {
                        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString("LANG", "en").commit();
                        set_language("en");
                    }
                }
            });
            builder.setTitle(R.string.select_your_language);
            builder.show();
        }
    }

    public void set_language(String language) {
        Configuration config = activity.getResources().getConfiguration();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        config.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(config, activity.getBaseContext().getResources().getDisplayMetrics());
        activity.recreate();
    }

    /* update the language of the app by taking input of language key */
    private void post_user_language(String language_key) {

        Storage storage = new Storage(activity);
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        Call<String> langCall = retrofit_interface.post_user_language(storage.get_access_token(), language_key);
        langCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                /* handle error globally */
                ErrorHandler.get_instance().handle_error(response.code(), activity, null);

                if (response.isSuccessful()) {
                    /* success true */
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body());
                        boolean isSuccess = jsonObject.getBoolean("success");
                        if (isSuccess) {
                            /* dismiss the dialog */
                            get_user_language();
                            /* get all the error messages and show to the user */
                            String message = jsonObject.getString("message");
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        } else {
                            /* dismiss the dialog */
                            /* get all the error messages and show to the user */
                            JSONArray message = jsonObject.getJSONArray("message");
                            Toast.makeText(activity, message.getString(0), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(activity, R.string.no_data_found, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {
                /* handle network error and notify the user */
                if (throwable instanceof IOException) {
                    Toast.makeText(activity, R.string.connection_timeout, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void get_vocabulary_chapters() {

        Storage storage = new Storage(activity);
        ProgressDialog dialog = Utility.show_dialog(activity);
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        Call<String> chapter_call = retrofit_interface.get_vocabulary_chapters_list(storage.get_access_token(), Utility.REQUEST_TYPE);
        chapter_call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                /*handle error globally */
                ErrorHandler.get_instance().handle_error(response.code(), activity, dialog);

                if (response.isSuccessful()) {
                    /* success true */
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body());
                        boolean isSuccess = jsonObject.getBoolean("success");
                        if (isSuccess) {
                            /* save the category response for offline uses */
                            storage.save_vocabulary_chapters_response(response.body());

                            /* serialize the String response */
                            Gson gson = new Gson();
                            chapter_list = gson.fromJson(response.body(), APIResponse.class);
                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);
                            storage.save_user_total_point(chapter_list.getTotalPoint());
                            total_point.setText(String.valueOf(chapter_list.getTotalPoint()));
                            /* add the data to the recycler view */
                            vocabulary_chapters_adapter = new VocabularyChaptersAdapter(chapter_list.getVocabularyChapterList(), activity);
                            vocabulary_chapters_recycler_view.setAdapter(vocabulary_chapters_adapter);

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

    private void get_grammar_chapters() {

        Storage storage = new Storage(activity);
        ProgressDialog dialog = Utility.show_dialog(activity);
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        Call<String> chapter_call = retrofit_interface.get_grammar_chapters_list(storage.get_access_token(), Utility.REQUEST_TYPE);
        chapter_call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                /*handle error globally */
                ErrorHandler.get_instance().handle_error(response.code(), activity, dialog);

                if (response.isSuccessful()) {
                    /* success true */
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body());
                        boolean isSuccess = jsonObject.getBoolean("success");
                        if (isSuccess) {
                            /* save the category response for offline uses */
                            storage.save_grammar_chapters_response(response.body());

                            /* serialize the String response */
                            Gson gson = new Gson();
                            chapter_list = gson.fromJson(response.body(), APIResponse.class);
                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);
                            storage.save_user_total_point(chapter_list.getTotalPoint());
                            total_point.setText(String.valueOf(chapter_list.getTotalPoint()));
                            /* add the data to the recycler view */
                            grammar_chapters_adapter = new GrammarChaptersAdapter(chapter_list.getGrammarChapterList(), activity);
                            grammar_chapters_recycler_view.setAdapter(grammar_chapters_adapter);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /* view type casting */
    private void init_views(){

        View view = getView();
        if (view != null) {
            layout_home = view.findViewById(R.id.layout_home);
            fab_language = view.findViewById(R.id.fab_language);
//            oz_language = view.findViewById(R.id.oz_language);
//            qr_language = view.findViewById(R.id.qr_language);
//            ru_language = view.findViewById(R.id.ru_language);
//            en_language = view.findViewById(R.id.en_language);

            text_hello = view.findViewById(R.id.text_hello);
            text_full_name = view.findViewById(R.id.text_full_name);
            total_point = view.findViewById(R.id.text_total_point);
            image_logout = view.findViewById(R.id.image_logout);
            vocabulary_chapters_recycler_view = view.findViewById(R.id.vocabulary_chapters_recycler_view);
            grammar_chapters_recycler_view = view.findViewById(R.id.grammar_chapters_recycler_view);
            btn_vocabulary = view.findViewById(R.id.btn_vocabulary);
            btn_grammar = view.findViewById(R.id.btn_grammar);
        }
    }

}
