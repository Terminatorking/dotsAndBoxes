package com.soheil.dotsandboxes.helper;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

@SuppressWarnings("all")
public class FileHelper {
    public static void saveToFile(String path, String content) {
        File file = new File(path);
        try {
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            writer.append(content);
            writer.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            Log.e("Create_Error", e.getMessage(), e);
        }
    }

    public static String loadFromFile(String path) {
        File file = new File(path);
        StringBuilder savedGameBuilder = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                savedGameBuilder.append(line);
                savedGameBuilder.append('\n');
            }
            br.close();
        } catch (IOException e) {
            Log.e("Read_Error", e.getMessage(), e);
        }

        return savedGameBuilder.toString();
    }

}
