package com.example.sudokumaster.SudokuBoardElements;

import android.widget.TextView;

public class item {
    public TextView btn;
    public boolean permanent = false;
    public int number;
    public String id;

    public item(int i, int j) {
        id = i + "" + j;
    }
}
