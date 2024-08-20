package com.example.sudokumaster;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

import com.example.sudokumaster.FileManager.*;
import com.example.sudokumaster.SudokuBoardElements.Board;

public class mainSudoku extends AppCompatActivity {
    TextView newGame, ContinueGame, challenge;
    RelativeLayout menuBackground;
    savingSudoku Sudoku;
    Board board;
    GridLayout gridMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sudoku_home);
        board = new Board();
        gridMain = findViewById(R.id.gridmain);
        ContinueGame = findViewById(R.id.continueGame);
        newGame = findViewById(R.id.NewGame);
        challenge = findViewById(R.id.challenge);
        menuBackground = findViewById(R.id.menuBackground);
        Sudoku = new savingSudoku(getApplicationContext(), "sudoku.txt");
        Random random = new Random();

        //  ASSIGNING THE GRID ELEMENT IN SIDE THE SUDOKU ==========================================
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board.board[i][j].miniGrid = new GridLayout(this);
                board.board[i][j].miniGrid.setRowCount(3);
                board.board[i][j].miniGrid.setColumnCount(3);
                board.board[i][j].miniGrid.setPadding(10, 10, 10, 10);
                board.board[i][j].miniGrid.setBackgroundResource(R.drawable.miniblock);
                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.width = GridLayout.LayoutParams.WRAP_CONTENT;
                param.height = GridLayout.LayoutParams.WRAP_CONTENT;
                gridMain.addView(board.board[i][j].miniGrid, param);
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        String x = board.sudoku[i * 3 + k][j * 3 + l] != 0 ? String.valueOf(board.sudoku[i * 3 + k][j * 3 + l]) : "";
                        board.board[i][j].block[k][l].btn = new TextView(this);
                        board.board[i][j].block[k][l].btn.setTextColor(Color.BLUE);
                        board.board[i][j].block[k][l].btn.setText(x);
                        board.board[i][j].block[k][l].btn.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        board.board[i][j].block[k][l].btn.setTextSize(18);
                        board.board[i][j].block[k][l].btn.setGravity(Gravity.CENTER);
                        board.board[i][j].block[k][l].number = board.sudoku[i * 3 + k][j * 3 + l];
                        board.board[i][j].block[k][l].btn.setPadding(0, 0, 2, 8);
                        board.board[i][j].block[k][l].btn.setBackgroundResource(R.drawable.numberblock);

                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = 80;
                        params.height = 80;
                        params.setMargins(1, 1, 1, 1);
                        board.board[i][j].miniGrid.addView(board.board[i][j].block[k][l].btn, params);
                    }
                }
            }
        }

        board.createSudoku(random.nextInt(10) + 50);
        board.changeToSolution();
        Sudoku.readData();
        if (Sudoku.fileExist) {
            ContinueGame.setVisibility(View.VISIBLE);
            ContinueGame.setOnClickListener(v -> {
                Handler handler = new Handler(Looper.getMainLooper());
                ContinueGame.setBackgroundResource(R.drawable.main_button);
                ContinueGame.setTextColor(Color.WHITE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ContinueGame.setBackgroundResource(R.drawable.reverse_main_button);
                        ContinueGame.setTextColor(Color.rgb(23, 28, 34));
                    }
                }, 200);
                Intent intent = new Intent(getApplicationContext(), Sudoku.class);
                intent.putExtra("difficulty", 0);
                startActivity(intent);
            });
        } else {
            ContinueGame.setVisibility(View.INVISIBLE);
        }


        challenge.setOnClickListener(v -> {
            challenge.setBackgroundResource(R.drawable.reverse_challenge_back);
            challenge.setTextColor(Color.rgb(23, 28, 34));
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                challenge.setBackgroundResource(R.drawable.main_button);
                challenge.setTextColor(Color.WHITE);
            }, 200);

            Intent intent = new Intent(getApplicationContext(),Sudoku.class);
            intent.putExtra("challenge",1);
            startActivity(intent);
        });


        newGame.setOnClickListener(v -> {
            newGame.setBackgroundResource(R.drawable.reverse_main_button);
            newGame.setTextColor(Color.rgb(23, 28, 34));
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                newGame.setBackgroundResource(R.drawable.main_button);
                newGame.setTextColor(Color.WHITE);
            }, 200);
            ShowOptions(random);
        });

    }

    public void ShowOptions(Random random) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.difficlty_select_dialog);
        dialog.show();
        LinearLayout easyMode = (LinearLayout) dialog.findViewById(R.id.easyMode);
        TextView easyText = dialog.findViewById(R.id.easyText);
        LinearLayout mediumMode = (LinearLayout) dialog.findViewById(R.id.mediumMode);
        TextView mediumText = dialog.findViewById(R.id.mediumText);
        LinearLayout hardMode = (LinearLayout) dialog.findViewById(R.id.hardMode);
        TextView hardText = dialog.findViewById(R.id.hardText);
        LinearLayout expertMode = (LinearLayout) dialog.findViewById(R.id.expertMode);
        TextView expertText = dialog.findViewById(R.id.expertText);
        LinearLayout nightmareMode = (LinearLayout) dialog.findViewById(R.id.nightmareMode);
        TextView nightmareText = dialog.findViewById(R.id.nightmareText);

        easyMode.setOnClickListener(v -> {
            easyMode.setBackgroundResource(R.drawable.norml_back);
            easyText.setTextColor(Color.rgb(23, 28, 34));
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                easyMode.setBackgroundResource(0);
                easyText.setTextColor(Color.WHITE);
                dialog.dismiss();
            }, 100);
            Intent intent = new Intent(getApplicationContext(), Sudoku.class);
            intent.putExtra("difficulty", (int) (random.nextInt(10) + 30));
            startActivity(intent);
        });

        mediumMode.setOnClickListener(v -> {
            mediumMode.setBackgroundResource(R.drawable.norml_back);
            mediumText.setTextColor(Color.rgb(23, 28, 34));
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mediumMode.setBackgroundResource(0);
                    mediumText.setTextColor(Color.WHITE);
                    Intent intent = new Intent(getApplicationContext(), Sudoku.class);
                    intent.putExtra("difficulty", random.nextInt(5) + 40);
                    startActivity(intent);
                    dialog.dismiss();
                }
            }, 100);
        });
        hardMode.setOnClickListener(v -> {
            hardMode.setBackgroundResource(R.drawable.norml_back);
            hardText.setTextColor(Color.rgb(23, 28, 34));
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hardMode.setBackgroundResource(0);
                    hardText.setTextColor(Color.WHITE);
                    Intent intent = new Intent(getApplicationContext(), Sudoku.class);
                    intent.putExtra("difficulty", random.nextInt(10) + 45);
                    startActivity(intent);
                    dialog.dismiss();
                }
            }, 100);
        });
        expertMode.setOnClickListener(v -> {
            expertMode.setBackgroundResource(R.drawable.norml_back);
            expertText.setTextColor(Color.rgb(23, 28, 34));
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    expertMode.setBackgroundResource(0);
                    expertText.setTextColor(Color.WHITE);
                    Intent intent = new Intent(getApplicationContext(), Sudoku.class);
                    intent.putExtra("difficulty", random.nextInt(5) + 55);
                    startActivity(intent);
                    dialog.dismiss();
                }
            }, 100);
        });
        nightmareMode.setOnClickListener(v -> {
            nightmareMode.setBackgroundResource(R.drawable.norml_back);
            nightmareText.setTextColor(Color.rgb(23, 28, 34));
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nightmareMode.setBackgroundResource(0);
                    nightmareText.setTextColor(Color.WHITE);
                    Intent intent = new Intent(getApplicationContext(), Sudoku.class);
                    intent.putExtra("difficulty", random.nextInt(6) + 60);
                    startActivity(intent);
                    dialog.dismiss();
                }
            }, 100);
        });
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.AnimationOption;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

}
