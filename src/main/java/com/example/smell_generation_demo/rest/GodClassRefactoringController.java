package com.example.smell_generation_demo.rest;

import com.example.smell_generation_demo.dtos.RefactoringInput;
import com.example.smell_generation_demo.repository.impl.FileRepositoryImpl;
import com.example.smell_generation_demo.service.Refactoring;
import com.example.smell_generation_demo.service.SampleGenerator;
import com.example.smell_generation_demo.service.godclass.GodClassRefactoring;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.util.List;

@Path("/blob/class")
@Stateless
public class GodClassRefactoringController {
    @EJB
    private SampleGenerator sampleGenerator;

//    @GET
//    public Response refactor(@QueryParam("firstClass") String firstClassPath, @QueryParam("secondClass") String secondClassPath) {
//        Refactoring refactoring = new GodClassRefactoring(firstClassPath, secondClassPath, destination, new FileRepositoryImpl());
//        try {
//            refactoring.doRefactoring();
//        } catch (FileNotFoundException | ClassNotFoundException e) {
//            return Response.status(404).entity(e.getMessage()).build();
//        } catch (IllegalStateException e) {
//            return Response.status(400).entity(e.getMessage()).build();
//        }
//        return Response.ok("ok").build();
//    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response refactor(RefactoringInput input) {
        new Thread(() -> {
            sampleGenerator.setProperties((List<String>) input.getDataset(), input.getTotalPositive(), input.getTotalNegative(), input.getSamplingRatio(), input.getMaxIterations(), input.getDestinationPath());
            sampleGenerator.generatePositiveSamples();
        }).start();
        return Response.ok("Augmentation started").build();
    }
}
