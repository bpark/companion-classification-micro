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