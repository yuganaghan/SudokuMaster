package com.example.sudokumaster.SudokuBoardElements;


import android.graphics.Color;

import androidx.annotation.NonNull;

import com.example.sudokumaster.R;
import com.example.sudokumaster.SettingElement.*;


import java.util.ArrayList;
import java.util.Random;

public class Board {
    public int sx,sy;
    public boolean GameOver,Loose ;
    public ArrayList<int[]> undoList = new ArrayList<>();
    public int[][] sudoku = {{0, 7, 9, 8, 0, 2, 0, 6, 3},
            {6, 0, 0, 9, 0, 0, 0, 1, 0},
            {8, 0, 3, 0, 7, 0, 0, 0, 2},
            {0, 9, 0, 0, 0, 0, 3, 7, 1},
            {0, 6, 8, 7, 0, 0, 0, 9, 0},
            {0, 3, 1, 0, 2, 0, 5, 8, 0},
            {2, 8, 6, 5, 0, 0, 1, 3, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {9, 0, 4, 3, 0, 0, 8, 2, 7}};

    public boolean [][] ColorMatrix = new boolean[9][9];

    public miniBlock[][] board = new miniBlock[3][3];

    public Board() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = new miniBlock(i * 3, j * 3);
            }
        }
        this.GameOver =false;
        this.Loose = false;
        this.sx=-1;
        this.sy =-1;
    }

    public boolean checkHasAnyZero(@NonNull int[][] tempsudoku) {
        for (int[] i : tempsudoku) {
            for (int j : i) {
                if (j == 0) {
                    return true;
                }
            }
        }
        return false;
    }


    public void checkGameOver() {
        this.GameOver = false;
        if (!this.Loose) {
            this.GameOver = true;
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (sudoku[i][j] == 0) {
                        this.GameOver = false;
                        break;
                    }
                }
                if (!this.GameOver)
                    break;
            }
        }
    }

    public int changeSudoku(int number, VibrationHelper v) {
        int tempMistake = 0;
        int counter = 0;
        if (!checkPosition(sudoku, new int[]{sx, sy}, number)) {
            v.vibrate(200);
            counter += 1;
            tempMistake += 1;
            board[sx / 3][sy / 3].block[sx % 3][sy % 3].btn.setTextColor(Color.RED);
        }
        sudoku[sx][sy] = number;
        if (noIntegrity() && counter == 0) {
            v.vibrate(200);
            tempMistake += 1;
            board[sx / 3][sy / 3].block[sx % 3][sy % 3].btn.setTextColor(Color.RED);
        }

        sudoku[sx][sy] = number;
        undoList.add(new int[]{sx, sy, 0});
        return tempMistake;
    }


    public void synchronize() {
        board[sx / 3][sy / 3].block[sx % 3][sy % 3].number = sudoku[sx][sy];
        board[sx / 3][sy / 3].block[sx % 3][sy % 3].btn.setText(String.valueOf(sudoku[sx][sy]));
    }

    public void normal() {
        sx = -1;
        sy = -1;
        for (int a = 0; a < 3; a++) {
            for (int b = 0; b < 3; b++) {
                for (int c = 0; c < 3; c++) {
                    for (int d = 0; d < 3; d++) {
                        board[a][b].block[c][d].btn.setBackgroundResource(R.drawable.numberblock);

                    }
                }
            }
        }
    }

    public void onlySelectNumbers(int number) {
        this.normal();
        for (miniBlock[] i : board) {
            for (miniBlock j : i) {
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        if (j.block[k][l].number == number) {
                            j.block[k][l].btn.setBackgroundResource(R.drawable.secondaryselection);
                        }
                    }
                }
            }
        }
    }

    public void setSelected() {

        int selectedNumber = board[sx / 3][sy / 3].block[sx % 3][sy % 3].number;
        for (int a = 0; a < 3; a++) {
            for (int b = 0; b < 3; b++) {
                for (int c = 0; c < 3; c++) {
                    for (int d = 0; d < 3; d++) {
                        if (sx == a * 3 + c) {
                            board[a][b].block[c][d].btn.setBackgroundResource(R.drawable.secondaryselection);
                        } else if (sy == b * 3 + d) {
                            board[a][b].block[c][d].btn.setBackgroundResource(R.drawable.secondaryselection);
                        } else
                            board[a][b].block[c][d].btn.setBackgroundResource(R.drawable.numberblock);
                        if (board[a][b].block[c][d].number == selectedNumber && board[a][b].block[c][d].number != 0) {
                            board[a][b].block[c][d].btn.setBackgroundResource(R.drawable.secondaryselection);
                        }
                    }
                }
            }
        }
        board[sx / 3][sy / 3].block[sx % 3][sy % 3].btn.setBackgroundResource(R.drawable.selected);


    }

    public void changeToSolution() {
        int a, b, p, q;
        b = 0;
        for (miniBlock[] i : board) {
            a = 0;
            for (miniBlock j : i) {
                p = b;
                for (int k = 0; k < 3; k++) {
                    q = a;
                    for (int l = 0; l < 3; l++) {
                        j.block[k][l].number = sudoku[p][q];
                        j.block[k][l].btn.setText(sudoku[p][q] != 0 ? String.valueOf(sudoku[p][q]): "");
                        q++;
                    }
                    p++;
                }
                a += 3;
            }
            b += 3;
        }
    }

    public int[] emptyPosition(int[][] tempsudoku) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (tempsudoku[i][j] == 0) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1};
    }

    public boolean checkPosition(int[][] tempsudoku, int[] position, int number) {
        for (int i = 0; i < 9; i++) {
            if (tempsudoku[position[0]][i] == number && position[1] != i) return false;
        }
        for (int i = 0; i < 9; i++) {
            if (tempsudoku[i][position[1]] == number && position[0] != i) return false;
        }
        int a = position[0] / 3;
        int b = position[1] / 3;
        for (int i = a * 3; i < a * 3 + 3; i++) {
            for (int j = b * 3; j < b * 3 + 3; j++) {
                if (tempsudoku[i][j] == number) return false;
            }
        }
        return true;
    }

    public boolean solution(int[][] tempsudoku) {
        int i = emptyPosition(tempsudoku)[0];
        int j = emptyPosition(tempsudoku)[1];
        if (i == -1) {
            return true;
        }
        for (int k = 1; k < 10; k++) {
            if (checkPosition(tempsudoku, new int[]{i, j}, k)) {
                tempsudoku[i][j] = k;
                if (solution(tempsudoku)) return true;
                tempsudoku[i][j] = 0;
            }
        }
        return false;
    }

    public void setZero() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                sudoku[i][j] = 0;
            }
        }
    }

    public static int[] randomize() {
        int[] oneToNine = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Random r = new Random();
        for (int i = 0; i < 9; i++) {
            int x = r.nextInt(8);
            int temp = oneToNine[i];
            oneToNine[i] = oneToNine[x];
            oneToNine[x] = temp;
        }
        return oneToNine;
    }

    public void createSudoku(int mode) {
        setZero();
        Random random = new Random();
        mode = random.nextInt(10) + mode;
        int[] randomArray = randomize();
        generating(randomArray);
        int i = 0;
        while (i < mode) {
            int x = random.nextInt(9);
            int y = random.nextInt(9);
            if (sudoku[x][y] != 0) {
                sudoku[x][y] = 0;
                board[x / 3][y / 3].block[x % 3][y % 3].btn.setTextColor(Color.BLUE);
                board[x / 3][y / 3].block[x % 3][y % 3].permanent = false;
                ColorMatrix[x][y] = false;
                i++;
            }
        }
    }

    public boolean generating(int[] range) {
        int i = emptyPosition(sudoku)[0];
        int j = emptyPosition(sudoku)[1];
        if (i == -1) {
            return true;
        }
        for (int k : range) {
            if (checkPosition(sudoku, new int[]{i, j}, k)) {
                sudoku[i][j] = k;
                board[i / 3][j / 3].block[i % 3][j % 3].permanent = true;
                board[i / 3][j / 3].block[i % 3][j % 3].btn.setTextColor(Color.rgb(0, 21, 97));
                ColorMatrix[i][j] = true;
                if (generating(range)) return true;
                sudoku[i][j] = 0;
            }
        }
        return false;
    }

    public void eraseElement(int i, int i1, int i2, int i3) {
        undoList.add(new int[]{i * 3 + i2, i1 * 3 + i3, sudoku[i * 3 + i2][i1 * 3 + i3]});
        sudoku[i * 3 + i2][i1 * 3 + i3] = 0;
        board[i][i1].block[i2][i3].btn.setTextColor(Color.BLUE);
        board[i][i1].block[i2][i3].number = 0;
        board[i][i1].block[i2][i3].btn.setText("");

    }

    public boolean noIntegrity() {
        int[][] tempsudoku = new int[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(sudoku[i], 0, tempsudoku[i], 0, 9);
        }
        solution(tempsudoku);
        return checkHasAnyZero(tempsudoku);
    }

    public int giveHintAnswer() {
        int[][] tempsudoku = new int[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(sudoku[i], 0, tempsudoku[i], 0, 9);
        }
        solution(tempsudoku);
        return tempsudoku[sx][sy];
    }

    public void hint() {
        if (sx != -1) {
            int temp = giveHintAnswer();
            sudoku[sx][sy] = temp;
            board[sx / 3][sy / 3].block[sx % 3][sy % 3].btn.setText(String.valueOf(temp));
            board[sx / 3][sy / 3].block[sx % 3][sy % 3].number = temp;
            undoList.add(new int[]{sx, sy, 0});
        }
    }
}
