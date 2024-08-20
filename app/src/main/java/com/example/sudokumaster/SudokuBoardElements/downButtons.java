package com.example.sudokumaster.SudokuBoardElements;

import android.graphics.Color;

import com.example.sudokumaster.R;

public class downButtons {
    public item[] button = new item[9];
    public int selected = 0;
    int[] frequency = new int[9];

    public downButtons() {
        for (int i = 0; i < 9; i++) {
            button[i] = new item(i + 1, 0);
            frequency[i] =0;
        }
    }

    public void normal() {
        selected = 0;
        for (int i = 0; i < 9; i++) {
            button[i].btn.setBackgroundResource(R.drawable.dropdown_norma_button);
            button[i].btn.setTextColor(Color.WHITE);
        }
        changeToFrequency();
    }


    public void changeSelected(int ids) {
        normal();
        selected = ids;
        button[selected-1].btn.setTextColor(Color.rgb(43,43,43));
        button[selected-1].btn.setBackgroundResource(R.drawable.reverse_dropdownbutton);
        changeToFrequency();
    }

    void zeroFrequency(){
        for (int i = 0; i < 9; i++) {
            frequency[i]=0;
        }
    }
    public void checkFrequency(int [][] sudoku){
        zeroFrequency();
        for (int i = 0; i <9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudoku[i][j] !=0){
                    frequency[(sudoku[i][j])-1] +=1;
                }
            }
        }
    }


    public void changeToFrequency(){
        for (int i=0;i<9;i++ ) {
            if (frequency[i] == 9){
                button[i].btn.setTextColor(Color.rgb(93,93,93));
                button[i].btn.setBackgroundResource(R.drawable.dropdown_norma_button);
            }
            if (selected !=0 && (selected-1) == i){
                button[i].btn.setBackgroundResource(R.drawable.reverse_dropdownbutton);
            }
        }
    }
    public void changeFrequency(boolean change){
        if (change)
            frequency[selected-1] +=1;
        changeToFrequency();
    }
}
