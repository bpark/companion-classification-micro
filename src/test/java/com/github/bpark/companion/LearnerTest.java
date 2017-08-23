/*
 * Copyright 2017 bpark
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.bpark.companion;

import org.junit.Test;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.SerializationHelper;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

/**
 * @author bpark
 */
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
