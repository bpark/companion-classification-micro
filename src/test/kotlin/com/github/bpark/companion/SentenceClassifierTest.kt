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

import com.github.bpark.companion.input.Sentence
import org.hamcrest.Matchers.lessThan
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class SentenceClassifierTest {

    lateinit var classifier: SentenceClassifier

    @Before
    fun init() {
        classifier = SentenceClassifier("/classifications/sentences.model")
        classifier.registerClasses("IMPERATIVE","DECLARATIVE","PEOPLE","LOCATION","OCCASION","REASON","INFORMATION","CHOICE","DESCRIPTION","QUANTITY","FREQUENCY","DISTANCE")

    }

    @Test
    fun testInformationClassification() {

        val information = Sentence("What is your name?",
                listOf("What", "is", "your", "name", "?"),
                listOf("WP", "VBZ", "PRP$", "NN", "."))

        assertThat<Double>(0.9, lessThan<Double>(classifier.classify(information)["INFORMATION"]))

    }

    @Test
    fun testImperativeClassification() {

        val imperative = Sentence("Do it now!",
                listOf("Do", "it", "now", "!"),
                listOf("VB", "PRP", "RB", "."))

        assertThat<Double>(0.9, lessThan<Double>(classifier.classify(imperative)["IMPERATIVE"]))

    }

    @Test
    fun testOccasionClassification() {

        val occasion = Sentence("When do the shops open?",
                listOf("When", "do", "the", "shops", "open", "?"),
                listOf("WRB", "VBP", "DT", "NNS", "JJ", "."))

        assertThat<Double>(0.9, lessThan<Double>(classifier.classify(occasion)["OCCASION"]))

    }
}