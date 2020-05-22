package com.minar.tactoetic;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.navigation.Navigation;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class Game extends androidx.fragment.app.Fragment {
    private int turnNumber = 1;
    private ImageView[][] board;

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
                board = new ImageView[4][4];
                inflater.inflate(R.layout.ttt_four, gridSection);
                break;
            case 5:
                board = new ImageView[5][5];
                inflater.inflate(R.layout.ttt_five, gridSection);
                break;
            default:
                board = new ImageView[3][3];
                inflater.inflate(R.layout.ttt_three, gridSection);
                break;
        }
        setBoard(v, board);

        v.findViewById(R.id.settingsBtn).setOnClickListener(View -> {
            Navigation.findNavController(v).navigate(R.id.action_game_to_settings);
            Activity act = getActivity();
            if (act instanceof MainActivity) ((MainActivity) act).vibrate();
        });

        v.findViewById(R.id.newGameBtn).setOnClickListener(View -> {
            Activity act = getActivity();
            if (act instanceof MainActivity) ((MainActivity) act).vibrate();
            newGame(board);
        });

        return v;
    }

    private void setBoard(View v, ImageView[][] board) {
        TextView result = v.findViewById(R.id.resultGame);
        for (int i = 0; i < board[0].length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                String id = "i" + i + j;
                int resId = getResources().getIdentifier(id, "id", requireActivity().getPackageName());
                board[i][j] = v.findViewById(resId);
                board[i][j].setOnClickListener((View view) -> {
                    turnNumber++;
                    if(turnNumber % 2 == 0) {
                        // Player 2 turn
                        animateText(requireActivity().getString(R.string.player_two_turn), result);
                        // Vibrate
                        Activity act = getActivity();
                        if (act instanceof MainActivity) ((MainActivity) act).vibrate();
                        ImageView img = (ImageView)view;
                        img.setImageResource(R.drawable.animated_to_circle);
                        Drawable imgDrawable = img.getDrawable();
                        ((Animatable) imgDrawable).start();
                        checkWin(board);
                        // Delay the execution
                        requireView().postDelayed(() -> {
                        }, 1200);
                    }
                    else {
                        // Player 1 turn
                        animateText(requireActivity().getString(R.string.player_one_turn), result);
                        // Vibrate
                        Activity act = getActivity();
                        if (act instanceof MainActivity) ((MainActivity) act).vibrate();
                        ImageView img = (ImageView)view;
                        img.setImageResource(R.drawable.animated_to_cross);
                        Drawable imgDrawable = img.getDrawable();
                        ((Animatable) imgDrawable).start();
                        checkWin(board);
                        // Delay the execution
                        requireView().postDelayed(() -> {
                        }, 1200);
                    }
                });
            }
        }

    }

    private void checkWin(ImageView[][] board) {

    }

    private void newGame(ImageView[][] board) {
        for (int i = 0; i < board[0].length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                String id = "i" + i + j;
                int resId = getResources().getIdentifier(id, "id", requireActivity().getPackageName());
                board[i][j] = requireView().findViewById(resId);
                board[i][j].setImageDrawable(null);
            }
        }

        // Restore the default placeholder
        TextView result = requireView().findViewById(R.id.resultGame);
        animateText(requireActivity().getString(R.string.result_placeholder), result);
    }

    private void animateText(String text, TextView tw) {
        // Create the animations
        final Animation animIn = new AlphaAnimation(1.0f, 0.0f);
        animIn.setDuration(550);
        tw.startAnimation(animIn);
        final Animation animOut = new AlphaAnimation(0.0f, 1.0f);
        animOut.setDuration(550);

        // Delay the execution
        requireView().postDelayed(() -> {
            tw.setText(text);
            tw.setSelected(true);
            tw.startAnimation(animOut);
        }, 550);
    }
}
