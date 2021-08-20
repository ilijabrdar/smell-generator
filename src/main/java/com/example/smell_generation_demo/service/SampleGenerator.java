package com.example.smell_generation_demo.service;

import java.util.List;

public interface SampleGenerator {
    void generatePositiveSamples();
    void setProperties(List<String> dataset, int positives, int negatives, float ratio, int maxIterations, Refactoring refactoring);
}
