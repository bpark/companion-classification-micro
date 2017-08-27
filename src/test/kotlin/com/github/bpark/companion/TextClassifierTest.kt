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
import org.hamcrest.Matchers.lessThan
import org.junit.Assert.assertThat
import org.junit.Test

class TextClassifierTest {

    @Test
    fun testClassify() {
        val classifier = TextClassifier("/classifications/basic-dialogs.model", listOf("greeting", "farewell", "other"))

        assertThat<Double>(0.9, lessThan<Double>(classifier.classify(raw("Hello John"))["greeting"]))

    }

    private fun raw(raw: String) = Sentence(raw, emptyList(), emptyList())
}