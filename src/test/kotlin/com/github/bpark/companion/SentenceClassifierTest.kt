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