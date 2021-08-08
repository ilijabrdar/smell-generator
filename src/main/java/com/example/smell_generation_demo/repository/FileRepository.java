package com.example.smell_generation_demo.repository;

public interface FileRepository {
    String readFile(String filePath);
    void dumpToFile(String filePath, String content);
}
