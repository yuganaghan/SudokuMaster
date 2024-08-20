package com.example.sudokumaster.FileManager;

import android.content.Context;
import android.widget.Toast;

import java.io.*;
import java.util.*;

public class saveSetting {
    Context context;
    String FileName;
    public boolean fileExist = true;
    public boolean[] setting = new boolean[4];

    public int hints =0;

    public saveSetting(Context context, String filename) {
        this.context = context;
        this.FileName = filename;
    }


    public void saveData(boolean[] setting,int hints) {
        System.arraycopy(setting, 0, this.setting, 0, 4);
        this.hints = hints;
        save();
    }

    private void save() {
        try {
            FileOutputStream fos = context.openFileOutput(this.FileName, Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            for (int i = 0; i < 4; i++) {
                writer.write(setting[i]?"1 ":"0 ");
            }
            writer.newLine();
            writer.write(String.valueOf(this.hints));
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
                if (firstLine !=null){

                    int j=0;
                    for (String  i : firstLine.split(" ")) {
                        setting[j] = Objects.equals(i,"1");
                        j+=1;
                    }
                    hints = Integer.parseInt(reader.readLine());
                }
                else
                    this.fileExist = false;
                reader.close();
                input.close();
            }
            else
                this.fileExist = false;
        } catch (Exception e) {
            Toast.makeText(context, e.getClass().getName(), Toast.LENGTH_SHORT).show();
        }
    }

}
