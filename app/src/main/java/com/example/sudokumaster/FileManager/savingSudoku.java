package com.example.sudokumaster.FileManager;

import android.content.Context;
import android.widget.Toast;


import java.io.*;
import java.util.Objects;

public class savingSudoku {
    Context context;
    String FileName;
    public int[][] matrix = new int[9][9];
    public boolean[][] ColorsMatrix = new boolean[9][9];
    public boolean fileExist = true;
    public int mistake;
    public long cTime;

    public savingSudoku(Context context, String filename) {
        this.context = context;
        this.FileName = filename;
    }


    public void saveData(int[][] matrix, long cTime, int mistake, boolean[][] colorsMatrix) {
        for (int i = 0; i < 9; i++) {
            System.arraycopy(matrix[i], 0, this.matrix[i], 0, 9);
            System.arraycopy(colorsMatrix[i], 0, this.ColorsMatrix[i], 0, 9);
        }
        this.cTime = cTime;
        this.mistake = mistake;
        save();

    }

    private void save() {
        try {
            FileOutputStream fos = context.openFileOutput(this.FileName, Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(cTime + " " + mistake);
            writer.newLine();
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    writer.write(this.matrix[i][j] + (this.ColorsMatrix[i][j] ? "1" : "0") + " ");
                }
                writer.newLine();
            }
            writer.flush();
            writer.close();
            fos.close();
        } catch (Exception e) {
            Toast.makeText(context, e.getClass().getName(), Toast.LENGTH_SHORT).show();
        }
    }

    public void readData() {
        try {
            File file = new File(context.getFilesDir(), this.FileName);
            if (file.exists()) {
                FileInputStream input = context.openFileInput(this.FileName);
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String firstLine = reader.readLine();
                if (firstLine != null) {
                    this.cTime = Long.parseLong(firstLine.split(" ")[0]);
                    this.mistake = Integer.parseInt(firstLine.split(" ")[1]);

                    for (int i = 0; i < 9; i++) {
                        String[] matLine = reader.readLine().split(" ");
                        for (int j = 0; j < 9; j++) {
                            matrix[i][j] = Integer.parseInt(String.valueOf(matLine[j].charAt(0)));
                            ColorsMatrix[i][j] = Objects.equals("1", String.valueOf(matLine[j].charAt(1)));
                        }
                    }
                } else
                    this.fileExist = false;
                reader.close();
                input.close();
            } else
                this.fileExist = false;
        } catch (Exception e) {
            Toast.makeText(context, e.getClass().getName(), Toast.LENGTH_SHORT).show();
        }
    }

    public void emptySave(){
        try {
            FileOutputStream fos = context.openFileOutput(this.FileName, Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write("");
            writer.flush();
            writer.close();
            fos.close();
        } catch (Exception e) {
            Toast.makeText(context, e.getClass().getName(), Toast.LENGTH_SHORT).show();
        }
    }

}
