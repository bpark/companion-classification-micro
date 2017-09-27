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

package com.github.bpark.companion.classifier

import com.github.bpark.companion.input.Sentence

/**
 * Abstract super class implementation for all classifiers.
 */
abstract class PhraseClassifier {

    abstract fun classify(sentence: Sentence): Map<String, Double>

    /**
     * Name to define the classification result name.
     *
     * @return the name of the classifier used for the result name.
     */
    abstract fun name(): String

    /**
     * Helper function to match a specific class with all predictions and returns true if the top result
     * matches the class.
     *
     * @param predictions all predictions.
     * @param clazz the specific classification value to match.
     * @return true if predictions[clazz] has the highest value.
     */
    fun mostLikely(predictions: Map<String, Double>, clazz: String): Boolean {
        val maximum = predictions.entries.stream().max { e1, e2 -> e1.value.compareTo(e2.value) }.get()
        return maximum.key == clazz
    }
}