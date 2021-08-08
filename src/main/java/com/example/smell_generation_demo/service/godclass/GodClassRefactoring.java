package com.example.smell_generation_demo.service.godclass;

import com.example.smell_generation_demo.repository.FileRepository;
import com.example.smell_generation_demo.service.Refactoring;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class GodClassRefactoring implements Refactoring {
    private final String firstClassPath;
    private final String secondClassPath;
    private final String destination;

    private final FileRepository repository;

    public GodClassRefactoring(String firstClassPath, String secondClassPath, String destination, FileRepository repository) {
        this.firstClassPath = firstClassPath;
        this.secondClassPath = secondClassPath;
        this.destination = destination;
        this.repository = repository;
    }

    @Override
    public String doRefactoring() {
            StaticJavaParser.getConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_11);
            StaticJavaParser.getConfiguration().setCharacterEncoding(StandardCharsets.UTF_8);
            try {
                CompilationUnit firstClass = StaticJavaParser.parse(new File(firstClassPath));
                CompilationUnit secondClass = StaticJavaParser.parse(new File(secondClassPath));

                ClassOrInterfaceDeclaration firstClassDeclaration = firstClass.findAll(ClassOrInterfaceDeclaration.class)
                        .stream().findFirst().orElseThrow(ClassNotFoundException::new);
                ClassOrInterfaceDeclaration secondClassDeclaration = secondClass.findAll(ClassOrInterfaceDeclaration.class)
                        .stream().findFirst().orElseThrow(ClassNotFoundException::new);

                if (firstClassDeclaration.isInterface() || secondClassDeclaration.isInterface())
                    throw new ClassNotFoundException();

                checkRefactoringConditions(firstClassDeclaration, secondClassDeclaration);
                mergeImports(firstClass, secondClass);
                mergeClassContent(firstClassDeclaration, secondClassDeclaration);
                String filename = getFilename(firstClassDeclaration, secondClassDeclaration);
                repository.dumpToFile(destination + File.separator + filename + ".java", secondClass.toString());
                return filename;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }
    }

    private void checkRefactoringConditions(ClassOrInterfaceDeclaration firstClass, ClassOrInterfaceDeclaration secondClass) {
        if(!checkMultipleInheritance(firstClass, secondClass))
            throw new IllegalStateException("Cannot have multiple inheritance");

        if(!checkMultipleMethodSignatures(firstClass, secondClass))
            throw new IllegalStateException("Cannot have the same method signatures in both classes");

        if(!checkMultipleFieldNames(firstClass, secondClass))
            throw new IllegalStateException("Cannot have the same field names in both classes");
    }

    private boolean checkMultipleInheritance(ClassOrInterfaceDeclaration firstClass, ClassOrInterfaceDeclaration secondClass) {
        ClassOrInterfaceType firstClassET = firstClass.getExtendedTypes().stream().findFirst().orElse(null);
        ClassOrInterfaceType secondClassET = secondClass.getExtendedTypes().stream().findFirst().orElse(null);
        if(firstClassET != null && secondClassET != null)
            return firstClassET.getName().equals(secondClassET.getName());
        else
            return true;
    }

    private boolean checkMultipleMethodSignatures(ClassOrInterfaceDeclaration firstClass, ClassOrInterfaceDeclaration secondClass) {
        Set<String> firstClassMethods = firstClass.getMethods().stream().map(m -> m.getSignature().toString()).collect(Collectors.toSet());
        Set<String> secondClassMethods = secondClass.getMethods().stream().map(m -> m.getSignature().toString()).collect(Collectors.toSet());
        firstClassMethods.retainAll(secondClassMethods);
        return firstClassMethods.size() == 0;
    }

    private boolean checkMultipleFieldNames(ClassOrInterfaceDeclaration firstClass, ClassOrInterfaceDeclaration secondClass) {
        Set<String> firstClassFields = firstClass.getFields().stream()
                .flatMap(f -> f.getVariables().stream())
                .map(VariableDeclarator::getNameAsString).collect(Collectors.toSet());
        Set<String> secondClassFields = secondClass.getFields().stream()
                .flatMap(f -> f.getVariables().stream())
                .map(VariableDeclarator::getNameAsString).collect(Collectors.toSet());
        firstClassFields.retainAll(secondClassFields);
        return firstClassFields.size() == 0;
    }

    private boolean isSubClass(ClassOrInterfaceDeclaration subClass, ClassOrInterfaceDeclaration superClass) {
        ClassOrInterfaceType subClassET = subClass.getExtendedTypes().stream().findFirst().orElse(null);
        String superClassName = superClass.getNameAsString();
        return subClassET != null && superClassName.equals(subClassET.getNameAsString());
    }

    private void mergeImports(CompilationUnit firstClass, CompilationUnit secondClass) {
        firstClass.getImports().forEach(secondClass::addImport);
    }

    private void mergeClassContent(ClassOrInterfaceDeclaration firstClass, ClassOrInterfaceDeclaration secondClass) {
        firstClass.getAnnotations().forEach(secondClass::addAnnotation);
        firstClass.getImplementedTypes().forEach(secondClass::addImplementedType);
        firstClass.getExtendedTypes().forEach(secondClass::addExtendedType);
        firstClass.getMethods().forEach(secondClass::addMember);
        firstClass.getFields().forEach(secondClass::addMember);
    }

    private String getFilename(ClassOrInterfaceDeclaration firstClass, ClassOrInterfaceDeclaration secondClass) {
        String className = firstClass.getNameAsString() + secondClass.getNameAsString();
        secondClass.setName(className);
        secondClass.getConstructors().forEach(c -> c.setName(className));
        return UUID.randomUUID().toString();
    }

//    private String preprocessFile(String path) {
//        String file = repository.readFile(path);
//        List<String> filteredCode = Arrays.stream(file.split("\\s+"))
//                .filter(word -> !word.equals("static") && !word.equals("private"))
//                .collect(Collectors.toList());
//        return String.join(" ", filteredCode);
//    }
}
