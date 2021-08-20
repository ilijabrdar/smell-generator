package com.example.smell_generation_demo.service.godclass;

import com.example.smell_generation_demo.repository.FileRepository;
import com.example.smell_generation_demo.service.Refactoring;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.sun.org.apache.bcel.internal.generic.ATHROW;

import java.io.File;
import java.util.UUID;

public class LongMethodRefactoring implements Refactoring {
    private final String destination;
    private final FileRepository repository;

    public LongMethodRefactoring(String destination, FileRepository repository) {
        this.destination = destination;
        this.repository = repository;
    }

    @Override
    public String doRefactoring(String firstMethodPath, String secondMethodPath) {
        String firstMethodWrapped = "public class No {" + repository.readFile(firstMethodPath) + "}";
        String secondMethodWrapped = "public class Name {" + repository.readFile(secondMethodPath) + "}";
        try {
            CompilationUnit firstClass = StaticJavaParser.parse(new File(firstMethodWrapped));
            CompilationUnit secondClass = StaticJavaParser.parse(new File(secondMethodWrapped));

            ClassOrInterfaceDeclaration firstClassDeclaration = firstClass.findAll(ClassOrInterfaceDeclaration.class)
                    .stream().findFirst().orElseThrow(ClassNotFoundException::new);
            ClassOrInterfaceDeclaration secondClassDeclaration = secondClass.findAll(ClassOrInterfaceDeclaration.class)
                    .stream().findFirst().orElseThrow(ClassNotFoundException::new);

            firstClassDeclaration.getMethods().forEach(secondClassDeclaration::addMember);
            mergeMethods(firstClassDeclaration, secondClassDeclaration);
            String filename = getFilename(firstClassDeclaration, secondClassDeclaration);
            repository.dumpToFile(destination + File.separator + filename + ".java", secondClass.toString());
            return filename;
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void mergeMethods(ClassOrInterfaceDeclaration firstClass, ClassOrInterfaceDeclaration secondClass) {
        MethodDeclaration firstMethod = firstClass.getMethods().get(0);
        MethodDeclaration secondMethod = secondClass.getMethods().get(0);
        BlockStmt firstBody = firstMethod.getBody().orElseThrow(IllegalArgumentException::new);
        BlockStmt secondBody = secondMethod.getBody().orElseThrow(IllegalArgumentException::new);
        secondBody.getStatements().addAll(firstBody.getStatements());
    }

    private String getFilename(ClassOrInterfaceDeclaration firstClass, ClassOrInterfaceDeclaration secondClass) {
        String className = firstClass.getNameAsString() + secondClass.getNameAsString();
        secondClass.setName(className);
        secondClass.getConstructors().forEach(c -> c.setName(className));
        return UUID.randomUUID().toString();
    }
}
