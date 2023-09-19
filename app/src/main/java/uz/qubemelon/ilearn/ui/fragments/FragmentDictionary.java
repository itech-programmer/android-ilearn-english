package uz.qubemelon.ilearn.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.adapters.dictionaries.DictionariesAdapter;
import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.models.APIResponse;
import uz.qubemelon.ilearn.models.dictionaries.DictionaryList;
import uz.qubemelon.ilearn.network.ErrorHandler;
import uz.qubemelon.ilearn.network.RetrofitClient;
import uz.qubemelon.ilearn.network.RetrofitInterface;
import uz.qubemelon.ilearn.ui.activities.AuthActivity;
import uz.qubemelon.ilearn.ui.activities.HomeActivity;
import uz.qubemelon.ilearn.utilities.Utility;

public class FragmentDictionary extends Fragment {

    /* all global field instances are here */
    private Activity activity;
    private DictionariesAdapter dictionaries_adapter;
    private RecyclerView dictionary_recycler_view;
    private APIResponse dictionary_list;
    private List<DictionaryList> dictionary_array_list;
    private ImageView image, image_logout;
    private SearchView search_bar;
    private TextView text_info, total_point;

    public FragmentDictionary() {
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
        return inflater.inflate(R.layout.fragment_dictionary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init_views();
        Storage storage = new Storage(activity);
        if (storage.get_user_total_point() != 0){
            total_point.setText(String.valueOf(storage.get_user_total_point()));
        }

        /* if there is internet call get data from server, otherwise load for local response */
        if (Utility.is_internet_available(activity)) {
            get_dictionary_list();
        } else {
            Storage dictionary_storage = new Storage(activity);
            if (dictionary_storage.get_dictionaries_response() != null) {
                /* load data from offline */
                /* serialize the String response */
                Gson gson = new Gson();
                dictionary_list = gson.fromJson(dictionary_storage.get_dictionaries_response(), APIResponse.class);
                /* add the data to the recycler view */
                if (dictionary_list.getDictionaryList() != null)
                    dictionaries_adapter = new DictionariesAdapter(dictionary_list.getDictionaryList(), activity);
                dictionary_recycler_view.setAdapter(dictionaries_adapter);
            }else {
                image.setVisibility(View.VISIBLE);
                search_bar.setVisibility(View.GONE);
                text_info.setVisibility(View.VISIBLE);
                dictionary_recycler_view.setVisibility(View.GONE);
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

        search_bar.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_bar.setQueryHint(getResources().getString(R.string.type_here_search_request));
            }
        });
        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query_text) {
                if (dictionaries_adapter.dictionary_filter_list() == null){
                    Toast.makeText(activity, R.string.no_data_found, Toast.LENGTH_SHORT).show();
                }else {
                    dictionaries_adapter.dictionary_filter_list().filter(query_text);
                }
                return false;
            }
        });
        search_bar.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Storage dictionary_storage = new Storage(activity);
                if (dictionary_storage.get_dictionaries_response() != null) {
                    /* load data from offline */
                    /* serialize the String response */
                    Gson gson = new Gson();
                    dictionary_list = gson.fromJson(dictionary_storage.get_dictionaries_response(), APIResponse.class);
                    /* add the data to the recycler view */
                    if (dictionary_list.getDictionaryList() != null)
                        dictionaries_adapter = new DictionariesAdapter(dictionary_list.getDictionaryList(), activity);
                    dictionary_recycler_view.setAdapter(dictionaries_adapter);
                }else {
                    image.setVisibility(View.VISIBLE);
                    search_bar.setVisibility(View.GONE);
                    text_info.setVisibility(View.VISIBLE);
                    dictionary_recycler_view.setVisibility(View.GONE);
                }
                return false;
            }
        });
    }

    private void get_dictionary_list() {

        Storage storage = new Storage(activity);
        ProgressDialog dialog = Utility.show_dialog(activity);
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        Call<String> dictionary_call = retrofit_interface.get_dictionaries_list(storage.get_access_token(), Utility.REQUEST_TYPE);
        dictionary_call.enqueue(new Callback<String>() {

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
                            /* save the category response for offline uses */
                            storage.save_dictionaries_response(response.body());

                            /* serialize the String response */
                            Gson gson = new Gson();
                            dictionary_list = gson.fromJson(response.body(), APIResponse.class);
                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);
                            storage.save_user_total_point(dictionary_list.getTotalPoint());
                            total_point.setText(String.valueOf(dictionary_list.getTotalPoint()));
                            /* add the data to the recycler view */
                            dictionaries_adapter = new DictionariesAdapter(dictionary_list.getDictionaryList(), activity);
                            dictionary_recycler_view.setAdapter(dictionaries_adapter);
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

    private void init_views() {
        View view = getView();
        if (view != null) {
            dictionary_recycler_view = view.findViewById(R.id.dictionary_recycler_view);
            total_point = view.findViewById(R.id.text_total_point);
            image_logout = view.findViewById(R.id.image_logout);
            image = view.findViewById(R.id.image);
            search_bar = view.findViewById(R.id.search_bar);
            text_info = view.findViewById(R.id.text_info);
        }
    }
}
