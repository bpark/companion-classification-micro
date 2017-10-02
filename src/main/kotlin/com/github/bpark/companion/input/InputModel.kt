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

package com.github.bpark.companion.input

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Data class to hold all nlp results.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class NlpSentence(val raw: String, val tokens: List<String>, val posTags: List<String>)

/**
 * Data class to hold all nlp processed sentences.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class AnalyzedText(val sentences: List<NlpSentence>)

/**
 * Data class with all required wordnet results for a single word.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class AnalyzedWord(val lemma: String?)

/**
 * Data class with all analyzed words of a single sentence.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class WordnetSentence(val analyzedWords: List<AnalyzedWord?>)

/**
 * Data class with all wordnet analyzed sentences.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class WordnetAnalysis(val sentences: List<WordnetSentence>)

/**
 * Helper class to aggregate an nlp and a wordnet sentence.
 */
data class Sentence(val nlp: NlpSentence, val wordnet: WordnetSentence)

/**
 * Helper class to aggregate the whole nlp and wordnet analyzed text.
 */
data class AnalyzedInputText(val analyzedText: AnalyzedText, val wordnetAnalysis: WordnetAnalysis) {

    fun getSentences(): List<Sentence> {
        return analyzedText.sentences.mapIndexed { index, sentence ->  Sentence(sentence, wordnetAnalysis.sentences[index])}
    }
}