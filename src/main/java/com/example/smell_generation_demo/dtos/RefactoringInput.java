package com.example.smell_generation_demo.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class RefactoringInput {
    private Collection<String> dataset;
    private int totalPositive;
    private int totalNegative;
    private float samplingRatio;
    private String destinationPath;
    private int maxIterations;
    private String smellType;
}
