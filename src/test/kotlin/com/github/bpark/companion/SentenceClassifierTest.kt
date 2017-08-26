package com.github.bpark.companion

import org.junit.Test

class SentenceClassifierTest {

    @Test
    fun testClassifier() {
        val classifier = SentenceClassifier("/classifications/sentences.model")
        classifier.registerClasses("IMPERATIVE","DECLARATIVE","PEOPLE","LOCATION","OCCASION","REASON","INFORMATION","CHOICE","DESCRIPTION","QUANTITY","FREQUENCY","DISTANCE")

        val tokens = listOf("(*)","(often/JT)","(_/VERB)","(*)","(*)","(*)","(*)","(./.)")

        val classify = classifier.classify(tokens)

        println(classify)
    }
}