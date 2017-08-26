package com.github.bpark.companion

import org.junit.Test
import weka.core.SerializationHelper

class SentenceTypeLearnerTest {

    @Test
    fun testLearner() {
        val learner = SentenceTypeLearner()
        val instances = learner.loadDataset("src/test/resources/sentences.arff")
        val classifier = learner.learn(instances)
        learner.evaluate(classifier, instances)

        SerializationHelper.write("target/classes/sentences.model", classifier)
    }
}