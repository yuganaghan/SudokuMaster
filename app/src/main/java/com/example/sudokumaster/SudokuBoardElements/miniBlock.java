package com.example.sudokumaster.SudokuBoardElements;

import android.widget.GridLayout;

public class miniBlock {
    public item[][] block = new item[3][3];
    public GridLayout miniGrid;

    public miniBlock(int i, int j) {
        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 3; l++) {
                block[k][l] = new item(i + k, j + l);
            }
        }
    }
}
