package com.example.smell_generation_demo.service.godclass;

import com.example.smell_generation_demo.repository.FileRepository;
import com.example.smell_generation_demo.repository.impl.FileRepositoryImpl;
import com.example.smell_generation_demo.service.SampleGenerator;
import com.example.smell_generation_demo.websockets.WebSocket;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Singleton
public class GodClassSampleGenerator implements SampleGenerator {
    private List<String> currentSamples;
    private int positives;
    private int negatives;
    private float ratio;
    private float maxIterations;
    private String destination;
    private Set<String> generatedFiles;
    @EJB
    private WebSocket socket;

    @Override
    public void generatePositiveSamples() {
//        try {
            FileRepository repository = new FileRepositoryImpl();
            int iterations = 0;
            while ((float) positives / negatives < ratio && currentSamples.size() > 1 && ++iterations < maxIterations) {
                int[] ind = selectRandomIndices();
                GodClassRefactoring refactoring = new GodClassRefactoring(currentSamples.get(ind[0]), currentSamples.get(ind[1]), destination, repository);
                String file = refactoring.doRefactoring();
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

//        } catch (Exception e) {
//            System.out.println("SRANJE");
//        }
    }

    @Override
    public void setProperties(List<String> dataset, int positives, int negatives, float ratio, int maxIterations, String destination) {
        this.currentSamples = dataset;
        this.positives = positives;
        this.negatives = negatives;
        this.ratio = ratio;
        this.maxIterations = maxIterations;
        this.destination = destination;
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
