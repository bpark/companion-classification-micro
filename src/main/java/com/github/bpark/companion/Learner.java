package com.github.bpark.companion;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Learner {

    public Instances loadDataset(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            ArffLoader.ArffReader arff = new ArffLoader.ArffReader(reader);
            return arff.getData();
        }
    }

    public FilteredClassifier evaluate(Instances trainData) throws Exception {
        trainData.setClassIndex(0);
        StringToWordVector stringToWordVector = new StringToWordVector();
        stringToWordVector.setAttributeIndices("last");

        FilteredClassifier classifier = new FilteredClassifier();
        classifier.setFilter(stringToWordVector);

        classifier.setClassifier(new NaiveBayes());

        Evaluation eval = new Evaluation(trainData);

        int folds = 3;
        eval.crossValidateModel(classifier, trainData, folds, new Random(1));

        System.out.println(eval.toSummaryString());
        System.out.println(eval.toClassDetailsString());

        return classifier;

    }

    public FilteredClassifier learn(Instances trainData) throws Exception {
        trainData.setClassIndex(0);
        StringToWordVector filter = new StringToWordVector();
        filter.setAttributeIndices("last");
        FilteredClassifier classifier = new FilteredClassifier();
        classifier.setFilter(filter);
        classifier.setClassifier(new NaiveBayes());


        classifier.buildClassifier(trainData);

        return classifier;
        //System.out.println("===== Training on filtered (training) dataset =====");
    }

    public static void main(String[] args) throws Exception {
        Learner learner = new Learner();
        Instances instances = learner.loadDataset("src/main/resources/classifications/basic-dialogs.arff");
        learner.evaluate(instances);
        FilteredClassifier classifier = learner.learn(instances);

        SerializationHelper.write("src/main/resources/classifications/basic-dialogs.model", classifier);
    }
}
