package com.github.bpark.companion;

import org.junit.Test;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.SerializationHelper;

import static org.junit.Assert.assertTrue;

public class LearnerTest {

    @Test
    public void testLearn() throws Exception {
        Learner learner = new Learner();
        Instances instances = learner.loadDataset("src/test/resources/topics.arff");
        Classifier classifier = learner.learn(instances);
        learner.evaluate(classifier, instances);

        SerializationHelper.write("target/classes/topics.model", classifier);

        TextClassifier textClassifier = new TextClassifier();
        textClassifier.registerClasses("greeting", "farewell", "weather", "other");
        textClassifier.loadClassifier("/topics.model");

        assertTrue(0.9 < textClassifier.classify("It's rainy").get("weather"));
        assertTrue(0.9 < textClassifier.classify("It's sunny").get("weather"));
        assertTrue(0.9 < textClassifier.classify("It's cold").get("weather"));
        assertTrue(0.9 < textClassifier.classify("The summer is hot").get("weather"));
        assertTrue(0.9 < textClassifier.classify("The winter is cold").get("weather"));
        assertTrue(0.9 < textClassifier.classify("It's hot outside").get("weather"));
        assertTrue(0.9 < textClassifier.classify("The weather is bad").get("weather"));
        assertTrue(0.9 < textClassifier.classify("The sun is shining").get("weather"));

        assertTrue(0.9 < textClassifier.classify("How are you doing?").get("greeting"));
        assertTrue(0.9 < textClassifier.classify("Hello John").get("greeting"));
        assertTrue(0.9 < textClassifier.classify("Hi John").get("greeting"));

        assertTrue(0.9 < textClassifier.classify("Bye Mary").get("farewell"));

    }
}
