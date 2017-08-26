/*
 * Copyright 2017 bpark
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

package com.github.bpark.companion

import org.hamcrest.Matchers.lessThan
import org.junit.Assert.assertThat
import org.junit.Test
import weka.core.SerializationHelper

class LearnerTest {

    @Test
    @Throws(Exception::class)
    fun testLearner() {
        val learner = SentenceTypeLearner()
        val instances = learner.loadDataset("src/test/resources/sentences.arff")
        val classifier = learner.learn(instances)
        learner.evaluate(classifier, instances)

        SerializationHelper.write("target/classes/sentences.model", classifier)
    }

    @Test
    @Throws(Exception::class)
    fun testLearn() {
        val learner = TextClassifierLearner()
        val instances = learner.loadDataset("src/test/resources/topics.arff")
        val classifier = learner.learn(instances)
        learner.evaluate(classifier, instances)

        SerializationHelper.write("target/classes/topics.model", classifier)

        val textClassifier = TextClassifier("/topics.model")
        textClassifier.registerClasses("greeting", "farewell", "weather", "other")

        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify("It's rainy")["weather"]))
        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify("It's sunny")["weather"]))
        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify("It's cold")["weather"]))
        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify("The summer is hot")["weather"]))
        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify("The winter is cold")["weather"]))
        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify("It's hot outside")["weather"]))
        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify("The weather is bad")["weather"]))
        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify("The sun is shining")["weather"]))

        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify("How are you doing?")["greeting"]))
        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify("Hello John")["greeting"]))
        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify("Hi John")["greeting"]))

        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify("Bye Mary")["farewell"]))

    }
}