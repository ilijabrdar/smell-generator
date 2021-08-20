package com.example.smell_generation_demo.service.godclass;

import com.example.smell_generation_demo.repository.FileRepository;
import com.example.smell_generation_demo.service.Refactoring;

import java.io.FileNotFoundException;

public class LongMethodRefactoring implements Refactoring {
    private final String destination;
    private final FileRepository repository;

    public LongMethodRefactoring(String destination, FileRepository repository) {
        this.destination = destination;
        this.repository = repository;
    }

    @Override
    public String doRefactoring(String firstClassPath, String secondClassPath) {
        return null;
    }
}
