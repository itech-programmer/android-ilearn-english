package uz.qubemelon.ilearn.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import uz.qubemelon.ilearn.R;
import uz.qubemelon.ilearn.ui.activities.games.fives.FivesGameActivity;

public class FragmentGames extends Fragment implements View.OnClickListener {

    /* all global field instances are here */
    private Activity activity;
    private TextView text_total_point;
    private RelativeLayout layout_fives_game;

    public FragmentGames() {
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
        return inflater.inflate(R.layout.fragment_games, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init_views();
//        Storage storage = new Storage(activity);
//        text_total_point.setText(String.valueOf(storage.get_user_total_point()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.layout_fives_game:
                Intent fives_game_intent = new Intent(getActivity(), FivesGameActivity.class);
                startActivity(fives_game_intent);
            break;
        }
    }

    private void init_views() {
        View view = getView();
        if (view != null) {
            text_total_point = view.findViewById(R.id.text_total_point);
            layout_fives_game = view.findViewById(R.id.layout_fives_game);
            layout_fives_game.setOnClickListener(this);
        }
    }
}