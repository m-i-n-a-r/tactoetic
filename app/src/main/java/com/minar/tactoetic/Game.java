package com.minar.tactoetic;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
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
    private int[][] boardValues;

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
                boardValues = new int[4][4];
                inflater.inflate(R.layout.ttt_four, gridSection);
                break;
            case 5:
                board = new ImageView[5][5];
                boardValues = new int[5][5];
                inflater.inflate(R.layout.ttt_five, gridSection);
                break;
            case 6:
                board = new ImageView[6][6];
                boardValues = new int[6][6];
                inflater.inflate(R.layout.ttt_six, gridSection);
                break;
            default:
                board = new ImageView[3][3];
                boardValues = new int[3][3];
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

    // Set the board in a parametric way and manage the click actions
    private void setBoard(View v, ImageView[][] board) {
        Activity act = getActivity();
        TextView result = v.findViewById(R.id.resultGame);
        for (int i = 0; i < board[0].length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                String id = "i" + i + j;
                int resId = getResources().getIdentifier(id, "id", requireActivity().getPackageName());
                board[i][j] = v.findViewById(resId);
                board[i][j].setImageResource(R.drawable.animated_to_circle);
                // Get the accent and set the correct color filter
                assert act != null;
                int accent = ((MainActivity) act).getAccent(requireContext());
                board[i][j].setColorFilter(accent);
                boardValues[i][j] = 0;
                int finalJ = j;
                int finalI = i;
                board[i][j].setOnClickListener((View view) -> {
                    // Increase the turn number and disable the click on the selected element
                    turnNumber++;
                    board[finalI][finalJ].setOnClickListener(null);
                    if(turnNumber % 2 == 0) {
                        // Vibrate
                        ((MainActivity) act).vibrate();
                        // Update the board
                        boardValues[finalI][finalJ] = -1;
                        ImageView img = (ImageView)view;
                        img.setImageResource(R.drawable.animated_to_circle);
                        Drawable imgDrawable = img.getDrawable();
                        ((Animatable) imgDrawable).start();
                        boolean over = checkWin(board, finalI, finalJ);
                        // Player 2 turn if nobody won yet
                        if(!over) animateText(requireActivity().getString(R.string.player_two_turn), result);
                        else ((MainActivity) act).vibrate();
                    }
                    else {
                        // Vibrate
                        ((MainActivity) act).vibrate();
                        // Update the board
                        boardValues[finalI][finalJ] = 1;
                        ImageView img = (ImageView)view;
                        img.setImageResource(R.drawable.animated_to_cross);
                        Drawable imgDrawable = img.getDrawable();
                        ((Animatable) imgDrawable).start();
                        boolean over = checkWin(board, finalI, finalJ);
                        // Player 1 turn if nobody won yet
                        if(!over) animateText(requireActivity().getString(R.string.player_one_turn), result);
                        else ((MainActivity) act).vibrate();
                    }
                });
            }
        }

    }

    // Check if there's a winner or a draw
    private boolean checkWin(ImageView[][] board, int row, int column) {
        int newValue = boardValues[row][column];
        int tableSize = boardValues[0].length;
        ImageView[] moves = new ImageView[tableSize];

        // Check column
        for(int i = 0; i < tableSize; i++){
            moves[i] = board[row][i];
            if(boardValues[row][i] != newValue) break;
            if(i == tableSize - 1) {
                highlightMove(moves);
                declareWinner(newValue);
                disableBoard(board);
                return true;
            }
        }
        // Check row
        for(int i = 0; i < tableSize; i++) {
            moves[i] = board[i][column];
            if(boardValues[i][column] != newValue) break;
            if(i == tableSize - 1) {
                highlightMove(moves);
                declareWinner(newValue);
                disableBoard(board);
                return true;
            }
        }
        // Check diagonal
        if(row == column) {
            for(int i = 0; i < tableSize; i++) {
                moves[i] = board[i][i];
                if(boardValues[i][i] != newValue) break;
                if(i == tableSize - 1) {
                    highlightMove(moves);
                    declareWinner(newValue);
                    disableBoard(board);
                    return true;
                }
            }
        }
        // Check anti diagonal
        if(row + column == tableSize - 1) {
            for(int i = 0; i < tableSize; i++) {
                moves[i] = board[i][(tableSize - 1) - i];
                if(boardValues[i][(tableSize - 1) - i] != newValue) break;
                if(i == tableSize - 1) {
                    highlightMove(moves);
                    declareWinner(newValue);
                    disableBoard(board);
                    return true;
                }
            }
        }
        for (int i = 0; i < tableSize; i++) {
            for (int j = 0; j < tableSize; j++) {
                if(boardValues[i][j] == 0) return false;
            }
        }
        declareDraw();
        blinkBoard(board);
        return true;
    }

    // Reinitialize the game, the texts and the data structures
    private void newGame(ImageView[][] board) {
        // Reset and blink the board to get the click actions back
        blinkAndResetBoard(board);
        turnNumber = 1;
        // Restore the default placeholder
        TextView result = requireView().findViewById(R.id.resultGame);
        animateText(requireActivity().getString(R.string.result_placeholder), result);
    }

    // Highlight the squares where the winning set is
    private void blinkBoard(ImageView[][] views) {
            // Create the animations
            final Animation animIn = new AlphaAnimation(1.0f, 0.0f);
            animIn.setDuration(550);
            final Animation animOut = new AlphaAnimation(0.0f, 1.0f);
            animOut.setDuration(550);
            for (int i = 0; i < views[0].length; i++) {
                for (ImageView view : views[i]) {
                    view.startAnimation(animIn);
                    // Delay the execution
                    requireView().postDelayed(() -> {
                        view.setSelected(true);
                        view.startAnimation(animOut);
                    }, 550);
                }
            }
    }

    // Same as above, but also reset the board touch actions and images
    private void blinkAndResetBoard(ImageView[][] views) {
        // Create the animations
        final Animation animIn = new AlphaAnimation(1.0f, 0.0f);
        animIn.setDuration(550);
        final Animation animOut = new AlphaAnimation(0.0f, 1.0f);
        animOut.setDuration(550);
        for (int i = 0; i < views[0].length; i++) {
            for (ImageView view : views[i]) {
                view.startAnimation(animIn);
                // Delay the execution
                requireView().postDelayed(() -> {
                    view.setSelected(true);
                    setBoard(requireView(), board);
                    view.startAnimation(animOut);
                }, 550);
            }
        }
    }

    // Disable every touch action on the board, when the game is over
    private void disableBoard(ImageView[][] board) {
        for (int i = 0; i < board[0].length; i++) {
            for (ImageView view : board[i]) {
                view.setOnClickListener(null);
            }
        }
    }

    // Declare the winner setting the proper text
    private void declareWinner(int playerMove) {
        String oneWins = requireActivity().getString(R.string.player_one_wins);
        String twoWins = requireActivity().getString(R.string.player_two_wins);
        TextView result = requireActivity().findViewById(R.id.resultGame);
        if (playerMove == 1) animateText(twoWins, result);
        else animateText(oneWins, result);
    }

    // Declare a draw setting the proper text
    private void declareDraw() {
        String draw = requireActivity().getString(R.string.draw);
        TextView result = requireActivity().findViewById(R.id.resultGame);
        animateText(draw, result);
    }

    // Animate the text to disappear and reappear smoothly
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

    // Highlight the winning moves shifting them to white or black depending on the theme
    private void highlightMove(ImageView[] moves) {
        if (moves.length == 0) return;
        Activity act = getActivity();
        assert act != null;
        int accent = ((MainActivity) act).getAccent(requireContext());
        for (ImageView view : moves) {
            // Animate from the current accent color to white
            ValueAnimator anim = ValueAnimator.ofArgb(accent, requireActivity().getColor(R.color.goodGray));
            anim.addUpdateListener(animation -> view.setColorFilter(requireActivity().getColor(R.color.goodGray)));

            anim.setDuration(1000);
            anim.setInterpolator(new FastOutSlowInInterpolator());
            anim.start();
        }
    }
}
