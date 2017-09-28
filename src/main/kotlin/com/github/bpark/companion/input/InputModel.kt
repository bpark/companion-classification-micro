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

@JsonIgnoreProperties(ignoreUnknown = true)
data class NlpSentence(val raw: String, val tokens: List<String>, val posTags: List<String>)


@JsonIgnoreProperties(ignoreUnknown = true)
data class AnalyzedText(val sentences: List<NlpSentence>)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AnalyzedWord(val stem: String, val lemma: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class WordnetSentence(val analyzedWords: List<AnalyzedWord>)

@JsonIgnoreProperties(ignoreUnknown = true)
data class WordnetAnalysis(val wordnetSentences: List<WordnetSentence>)

data class Sentence(val nlpSentence: NlpSentence, val wordnetSentence: WordnetSentence)

data class AnalyzedInputText(val analyzedText: AnalyzedText, val wordnetAnalysis: WordnetAnalysis) {

    fun getSentences(): List<Sentence> {
        return analyzedText.sentences.mapIndexed { index, sentence ->  Sentence(sentence, wordnetAnalysis.wordnetSentences[index])}
    }
}