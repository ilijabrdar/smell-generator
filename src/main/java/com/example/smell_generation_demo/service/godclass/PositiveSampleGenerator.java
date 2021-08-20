package com.example.smell_generation_demo.service.godclass;

import com.example.smell_generation_demo.repository.FileRepository;
import com.example.smell_generation_demo.repository.impl.FileRepositoryImpl;
import com.example.smell_generation_demo.service.Refactoring;
import com.example.smell_generation_demo.service.SampleGenerator;
import com.example.smell_generation_demo.websockets.WebSocket;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Singleton
public class PositiveSampleGenerator implements SampleGenerator {
    private List<String> currentSamples;
    private int positives;
    private int negatives;
    private float ratio;
    private float maxIterations;
    private Set<String> generatedFiles;
    private Refactoring refactoring;
    @EJB
    private WebSocket socket;

    @Override
    public void generatePositiveSamples() {
        int iterations = 0;
        while ((float) positives / negatives < ratio && currentSamples.size() > 1 && ++iterations < maxIterations) {
            int[] ind = selectRandomIndices();
            String file = refactoring.doRefactoring(currentSamples.get(ind[0]), currentSamples.get(ind[1]));
            if (file != null) {
                if(!generatedFiles.contains(file)) {
                    positives++;
                    generatedFiles.add(file);
                }
            }
            else {
                currentSamples.remove(ind[0]);
                if (ind[1] > ind[0])
                    currentSamples.remove(ind[1] - 1);
                else
                    currentSamples.remove(ind[1]);
            }
        }

        System.out.println("+: " + positives);
        System.out.println("-: " + negatives);
        System.out.println("ratio: " + ratio);
        System.out.println("size: " + currentSamples.size());
        System.out.println("vrti");

        boolean status = iterations != maxIterations;

        if (status)
            socket.message("Augmentation completed successfully");
        else
            socket.message("Augmentation failed");
    }

    @Override
    public void setProperties(List<String> dataset, int positives, int negatives, float ratio, int maxIterations, Refactoring refactoring) {
        this.currentSamples = dataset;
        this.positives = positives;
        this.negatives = negatives;
        this.ratio = ratio;
        this.maxIterations = maxIterations;
        this.refactoring = refactoring;
        this.generatedFiles = new HashSet<>();
    }

    private int[] selectRandomIndices() {
        Random rand = new Random();
        int i = rand.nextInt(currentSamples.size());
        int j;
        do {
            j = rand.nextInt(currentSamples.size());
        } while (i == j);
        return new int[]{i, j};
    }
}
