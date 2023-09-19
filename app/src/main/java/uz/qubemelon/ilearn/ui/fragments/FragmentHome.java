package uz.qubemelon.ilearn.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.adapters.courses.GrammarChaptersAdapter;
import uz.qubemelon.ilearn.adapters.courses.VocabularyChaptersAdapter;
import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.models.courses.CourseResponse;
import uz.qubemelon.ilearn.network.ErrorHandler;
import uz.qubemelon.ilearn.network.RetrofitClient;
import uz.qubemelon.ilearn.network.RetrofitInterface;
import uz.qubemelon.ilearn.utilities.Utility;

public class FragmentHome extends Fragment {

    /* all global field instances are here */
    private Activity activity;
    private TextView text_full_name, text_total_point;
    private Storage chapters_storage;
    private VocabularyChaptersAdapter vocabulary_chapters_adapter;
    private GrammarChaptersAdapter grammar_chapters_adapter;
    private RecyclerView vocabulary_chapters_recycler_view, grammar_chapters_recycler_view;
    private AppCompatButton btn_vocabulary, btn_grammar;
    private CourseResponse chapter_list;

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

    chapters_storage = new Storage(activity);
        /* simple data binding for profile section */
        if (chapters_storage.get_user_full_name() != null) {
            text_full_name.setText(chapters_storage.get_user_full_name());
            text_total_point.setText(String.valueOf(chapters_storage.get_user_total_point()));
        }

        /* adding options to category recycler */
        GridLayoutManager vocabulary_grid_layout_manager = new GridLayoutManager(getActivity(), 2);
        vocabulary_chapters_recycler_view.setHasFixedSize(true);
        vocabulary_chapters_recycler_view.setLayoutManager(vocabulary_grid_layout_manager);

        GridLayoutManager grammar_grid_layout_manager = new GridLayoutManager(getActivity(), 2);
        grammar_chapters_recycler_view.setHasFixedSize(true);
        grammar_chapters_recycler_view.setLayoutManager(grammar_grid_layout_manager);
        /* do api call here and set data to the recycler view */

        /* if there is internet call get data from server, otherwise load for local response */
        if (Utility.is_internet_available(activity)) {
            get_vocabulary_chapters();
        }else {

            if (chapters_storage.get_vocabulary_chapters_response() != null) {
                /* load data from offline */
                /* serialize the String response */
                Gson gson = new Gson();
                chapter_list = gson.fromJson(chapters_storage.get_vocabulary_chapters_response(), CourseResponse.class);
                /* add the data to the recycler view */
                if (chapter_list.getVocabularyChapterList() != null)
                    vocabulary_chapters_adapter = new VocabularyChaptersAdapter(chapter_list.getVocabularyChapterList(), activity);
                vocabulary_chapters_recycler_view.setAdapter(vocabulary_chapters_adapter);
            }

            if (chapters_storage.get_grammar_chapters_response() != null) {
                /* load data from offline */
                /* serialize the String response */
                Gson gson = new Gson();
                chapter_list = gson.fromJson(chapters_storage.get_grammar_chapters_response(), CourseResponse.class);
                /* add the data to the recycler view */
                if (chapter_list.getGrammarChapterList() != null)
                    grammar_chapters_adapter = new GrammarChaptersAdapter(chapter_list.getGrammarChapterList(), activity);
                grammar_chapters_recycler_view.setAdapter(grammar_chapters_adapter);
            }
        }
            btn_vocabulary.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View v) {
                    if (Utility.is_internet_available(activity)) {
                        btn_vocabulary.setTextColor(getResources().getColor(R.color.text_color_white));
                        btn_vocabulary.setBackgroundResource(R.drawable.tab_active_leaderboard);
                        btn_grammar.setTextColor(getResources().getColor(R.color.text_color_primary));
                        btn_grammar.setBackgroundResource(getResources().getColor(R.color.transparent));

                        get_vocabulary_chapters();

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

                        get_grammar_chapters();

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
                            chapter_list = gson.fromJson(response.body(), CourseResponse.class);
                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);
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
                            chapter_list = gson.fromJson(response.body(), CourseResponse.class);
                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);
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
            text_full_name = view.findViewById(R.id.text_full_name);
            text_total_point = view.findViewById(R.id.text_total_point);
            vocabulary_chapters_recycler_view = view.findViewById(R.id.vocabulary_chapters_recycler_view);
            grammar_chapters_recycler_view = view.findViewById(R.id.grammar_chapters_recycler_view);
            btn_vocabulary = view.findViewById(R.id.btn_vocabulary);
            btn_grammar = view.findViewById(R.id.btn_grammar);
        }
    }
}
