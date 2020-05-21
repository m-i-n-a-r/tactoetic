package com.minar.tactoetic;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.navigation.Navigation;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


public class Game extends androidx.fragment.app.Fragment {

    public Game() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game, container, false);
        // Get the placeholder where the correct layout will be inflated
        RelativeLayout gridSection = v.findViewById(R.id.tttSection);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        int gridSize = Integer.parseInt(sp.getString("grid_size", "3"));
        switch (gridSize) {
            case 4:
                inflater.inflate(R.layout.ttt_four, gridSection);
                //LinearLayout tttZone4 = v.findViewById(R.id.tttZone);
                break;
            case 5:
                inflater.inflate(R.layout.ttt_five, gridSection);
                //LinearLayout tttZone5 = v.findViewById(R.id.tttZone);
                break;
            default:
                inflater.inflate(R.layout.ttt_three, gridSection);
                //LinearLayout tttZone3 = v.findViewById(R.id.tttZone);
                break;
        }

        v.findViewById(R.id.settingsBtn).setOnClickListener(View -> {
                Navigation.findNavController(v).navigate(R.id.action_game_to_settings);
                Activity act = getActivity();
                if (act instanceof MainActivity) ((MainActivity) act).vibrate();
        });

        return v;
    }
}
