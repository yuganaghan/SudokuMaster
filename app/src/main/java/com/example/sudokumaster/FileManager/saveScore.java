package com.example.sudokumaster.FileManager;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Objects;

public class saveScore {
    Context context;
    String FileName;
    public boolean fileExist = true;
    public long[] scores = new long[5];

    public saveScore(Context context, String filename) {
        this.context = context;
        this.FileName = filename;
    }

    public void saveData(long[] scores) {
        System.arraycopy(scores, 0, this.scores, 0, 5);
        save();
    }

    private void save() {
        try {
            FileOutputStream fos = context.openFileOutput(this.FileName, Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            for (int i = 0; i < 5; i++) {
                writer.write(scores[i] + " ");
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
                String line = reader.readLine();
                if (line != null) {
                    String[] firstLine = line.split(" ");
                    for (int i = 0; i < 5; i++) {
                        scores[i] = Long.parseLong(firstLine[i]);
                    }
                }
                else
                    this.fileExist= false;
                reader.close();
                input.close();
            } else
                this.fileExist = false;
        } catch (Exception e) {
            Toast.makeText(context, e.getClass().getName(), Toast.LENGTH_SHORT).show();
        }
    }

}

