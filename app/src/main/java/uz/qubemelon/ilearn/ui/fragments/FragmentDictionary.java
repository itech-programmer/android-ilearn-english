package uz.qubemelon.ilearn.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.adapters.dictionaries.DictionariesAdapter;
import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.models.courses.CourseResponse;
import uz.qubemelon.ilearn.models.dictionaries.DictionaryList;
import uz.qubemelon.ilearn.network.ErrorHandler;
import uz.qubemelon.ilearn.network.RetrofitClient;
import uz.qubemelon.ilearn.network.RetrofitInterface;
import uz.qubemelon.ilearn.utilities.Utility;

public class FragmentDictionary extends Fragment {

    /* all global field instances are here */
    private Activity activity;
    private DictionariesAdapter dictionaries_adapter;
    private RecyclerView dictionary_recycler_view;
    private CourseResponse dictionary_list;

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

        /* if there is internet call get data from server, otherwise load for local response */
        if (Utility.is_internet_available(activity)) {
            get_dictionary_list();
        } else {
            Storage dictionary_storage = new Storage(activity);
            if (dictionary_storage.get_dictionaries_response() != null) {
                /* load data from offline */
                /* serialize the String response */
                Gson gson = new Gson();
                dictionary_list = gson.fromJson(dictionary_storage.get_dictionaries_response(), CourseResponse.class);
                /* add the data to the recycler view */
                if (dictionary_list.getDictionaryList() != null)
                    dictionaries_adapter = new DictionariesAdapter(dictionary_list.getDictionaryList(), activity);
                dictionary_recycler_view.setAdapter(dictionaries_adapter);
            }
        }
    }

    private void get_dictionary_list() {

        Storage storage = new Storage(activity);
        ProgressDialog dialog = Utility.show_dialog(activity);
        RetrofitInterface retrofit_interface = RetrofitClient.get_retrofit().create(RetrofitInterface.class);
        Call<String> dictionary_call = retrofit_interface.get_dictionaries_list(storage.get_access_token(), Utility.REQUEST_TYPE);
        dictionary_call.enqueue(new Callback<String>() {

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
                            storage.save_dictionaries_response(response.body());

                            /* serialize the String response */
                            Gson gson = new Gson();
                            dictionary_list = gson.fromJson(response.body(), CourseResponse.class);
                            /* dismiss the dialog */
                            Utility.dismiss_dialog(dialog);
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
        }
    }
}
