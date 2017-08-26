package com.github.bpark.companion

import org.hamcrest.Matchers.lessThan
import org.junit.Assert.assertThat
import org.junit.Test

class TextClassifierTest {

    @Test
    fun testClassify() {
        val classifier = TextClassifier("/classifications/basic-dialogs.model")
        classifier.registerClasses("greeting", "farewell", "other")

        assertThat<Double>(0.9, lessThan<Double>(classifier.classify("Hello John")["greeting"]))

    }

}