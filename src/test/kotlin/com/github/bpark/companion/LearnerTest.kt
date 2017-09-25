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

import com.github.bpark.companion.classifier.TextClassifier
import com.github.bpark.companion.input.Sentence
import com.github.bpark.companion.learn.SentenceTypeLearner
import com.github.bpark.companion.learn.TextClassifierLearner
import org.hamcrest.Matchers.lessThan
import org.junit.Assert.assertThat
import org.junit.Test
import weka.core.SerializationHelper

/**
 * Test for different learner implementations.
 *
 * Each test reads an .arff file and produces a trained model. This model is used for classification and can be used
 * for the different classification implementations.
 */
class LearnerTest {

    /**
     * Test for the sentence type (imperative, declarative, people questions, etc.)
     */
    @Test
    @Throws(Exception::class)
    fun testLearner() {
        val learner = SentenceTypeLearner()
        val instances = learner.loadDataset("src/test/resources/sentences.arff")
        val classifier = learner.learn(instances)
        learner.evaluate(classifier, instances)

        SerializationHelper.write("target/classes/sentences.model", classifier)
    }

    /**
     * Test for topic type, ex, if a phrase is a greeting or farewell.
     */
    @Test
    @Throws(Exception::class)
    fun testLearn() {
        val learner = TextClassifierLearner()
        val instances = learner.loadDataset("src/test/resources/topics.arff")
        val classifier = learner.learn(instances)
        learner.evaluate(classifier, instances)

        SerializationHelper.write("target/classes/topics.model", classifier)

        val textClassifier = TextClassifier("/topics.model", listOf("greeting", "farewell", "weather", "other"))

        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify(raw("It's rainy"))["weather"]))
        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify(raw("It's sunny"))["weather"]))
        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify(raw("It's cold"))["weather"]))
        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify(raw("The summer is hot"))["weather"]))
        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify(raw("The winter is cold"))["weather"]))
        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify(raw("It's hot outside"))["weather"]))
        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify(raw("The weather is bad"))["weather"]))
        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify(raw("The sun is shining"))["weather"]))

        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify(raw("How are you doing?"))["greeting"]))
        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify(raw("Hello John"))["greeting"]))
        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify(raw("Hi John"))["greeting"]))

        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify(raw("Bye Mary"))["farewell"]))

    }

    /**
     * Test for tense detection.
     */
    @Test
    @Throws(Exception::class)
    fun testTenseLearn() {
        val learner = TextClassifierLearner()
        val instances = learner.loadDataset("src/test/resources/tenses.arff")
        val classifier = learner.learn(instances)
        learner.evaluate(classifier, instances)

        SerializationHelper.write("target/classes/tenses.model", classifier)

        val textClassifier = TextClassifier("/tenses.model", listOf("simplePresent", "presentProgressive",
                "simplePast", "pastProgressive", "simplePresentPerfect", "presentPerfectProgressive",
                "simplePastPerfect", "pastPerfectProgressive", "willFuture", "goingToFuture", "simpleFuturePerfect",
                "futurePerfectProgressive", "conditionalSimple", "conditionalProgressive",
                "conditionalPerfect", "conditionalPerfectProgressive"))

        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify(raw("VBP(0)"))["simplePresent"]))
        assertThat<Double>(0.9, lessThan<Double>(textClassifier.classify(raw("MD(will,0) VB"))["willFuture"]))
    }

    private fun raw(raw: String) = Sentence(raw, emptyList(), emptyList())
}