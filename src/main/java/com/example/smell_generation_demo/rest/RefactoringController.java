package com.example.smell_generation_demo.rest;

import com.example.smell_generation_demo.dtos.RefactoringInput;
import com.example.smell_generation_demo.repository.FileRepository;
import com.example.smell_generation_demo.repository.impl.FileRepositoryImpl;
import com.example.smell_generation_demo.service.Refactoring;
import com.example.smell_generation_demo.service.SampleGenerator;
import com.example.smell_generation_demo.service.godclass.GodClassRefactoring;
import com.example.smell_generation_demo.service.godclass.LongMethodRefactoring;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.util.List;

@Path("/refactor")
@Stateless
public class RefactoringController {
    @EJB
    private SampleGenerator sampleGenerator;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response refactor(RefactoringInput input) {
        FileRepository repository = new FileRepositoryImpl();
        Refactoring refactoring;
        if(input.getSmellType().equals("god_class"))
            refactoring = new GodClassRefactoring(input.getDestinationPath(), repository);
        else if(input.getSmellType().equals("long_method"))
            refactoring = new LongMethodRefactoring(input.getDestinationPath(), repository);
        else
            return Response.status(405).build();

        new Thread(() -> {
            sampleGenerator.setProperties((List<String>) input.getDataset(), input.getTotalPositive(), input.getTotalNegative(), input.getSamplingRatio(), input.getMaxIterations(), refactoring);
            sampleGenerator.generatePositiveSamples();
        }).start();

        return Response.ok("Augmentation started").build();
    }
}
