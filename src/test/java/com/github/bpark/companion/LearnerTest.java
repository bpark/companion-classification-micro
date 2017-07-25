package com.github.bpark.companion;

import org.junit.Test;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.SerializationHelper;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

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

        assertThat(0.9, lessThan(textClassifier.classify("It's rainy").get("weather")));
        assertThat(0.9, lessThan(textClassifier.classify("It's sunny").get("weather")));
        assertThat(0.9, lessThan(textClassifier.classify("It's cold").get("weather")));
        assertThat(0.9, lessThan(textClassifier.classify("The summer is hot").get("weather")));
        assertThat(0.9, lessThan(textClassifier.classify("The winter is cold").get("weather")));
        assertThat(0.9, lessThan(textClassifier.classify("It's hot outside").get("weather")));
        assertThat(0.9, lessThan(textClassifier.classify("The weather is bad").get("weather")));
        assertThat(0.9, lessThan(textClassifier.classify("The sun is shining").get("weather")));

        assertThat(0.9, lessThan(textClassifier.classify("How are you doing?").get("greeting")));
        assertThat(0.9, lessThan(textClassifier.classify("Hello John").get("greeting")));
        assertThat(0.9, lessThan(textClassifier.classify("Hi John").get("greeting")));

        assertThat(0.9, lessThan(textClassifier.classify("Bye Mary").get("farewell")));

    }
}
