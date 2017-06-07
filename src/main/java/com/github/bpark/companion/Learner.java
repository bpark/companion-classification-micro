/*
 * Copyright 2017 Kurt Sparber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.bpark.companion;

import weka.classifiers.Classifier;
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

    public void evaluate(Classifier classifier, Instances trainData) throws Exception {
        trainData.setClassIndex(0);

        Evaluation eval = new Evaluation(trainData);

        int folds = 3;
        eval.crossValidateModel(classifier, trainData, folds, new Random(1));

        System.out.println(eval.toSummaryString());
        System.out.println(eval.toClassDetailsString());

    }

    public Classifier learn(Instances trainData) throws Exception {
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
        Classifier classifier = learner.learn(instances);
        learner.evaluate(classifier, instances);

        SerializationHelper.write("src/main/resources/classifications/basic-dialogs.model", classifier);
    }
}
