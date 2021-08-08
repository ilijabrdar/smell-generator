package com.example.smell_generation_demo.repository.impl;

import com.example.smell_generation_demo.repository.FileRepository;

import java.io.*;

public class FileRepositoryImpl implements FileRepository {

    @Override
    public String readFile(String filePath) {
        String file = null;
        try(BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
            file = in.lines().reduce("", String::concat);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    public void dumpToFile(String filePath, String content) {
        try(PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filePath)))) {
            out.print(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
