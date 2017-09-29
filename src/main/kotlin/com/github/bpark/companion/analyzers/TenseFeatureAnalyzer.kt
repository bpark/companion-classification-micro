/*
 * Copyright 2017 bpark
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.bpark.companion.analyzers

import com.github.bpark.companion.input.Sentence
import mu.KotlinLogging

private data class WordInfo(val token: String, val tag: String, val lemma: String?) {

    fun map(index: Int): String {
        return when {
            (tag == "VBP" || tag == "VBZ") && lemma == "be" -> "$tag($lemma,$index)"
            (tag == "VBP" || tag == "VBZ") && lemma == "have" -> "$tag($lemma,$index)"
            (tag == "VBD" || tag == "VBN") && lemma == "be" -> "$tag($lemma,$index)"
            (tag == "VBD" || tag == "VB") && lemma == "have" -> "$tag($lemma,$index)"
            tag == "VBG" && token == "going" -> "$tag($token,$index)"
            tag == "MD" && (token == "will" || token == "would") -> "$tag($token,$index)"
            tag == "VBP" || tag == "VBG" || tag == "VBZ" -> "$tag($index)"
            else -> tag
        }
    }
}

object TenseFeatureTransformer : FeatureAnalyzer {

    private val logger = KotlinLogging.logger {}

    override fun transform(sentence: Sentence): List<String> {
        return listOf(buildBag(buildWordInfo(sentence)).joinToString(" "))
    }

    private fun buildBag(wordinfos: List<WordInfo>): List<String> {
        val verbs = wordinfos.filter {
            it.tag.startsWith("V") ||
                    (it.tag == "MD" && (it.token == "will" || it.token == "would")) ||
                    it.tag == "TO"
        }

        return verbs.mapIndexed { index, verb -> verb.map(index) }

    }

    private fun buildWordInfo(sentence: Sentence): List<WordInfo> {

        val tags = sentence.nlp.posTags
        val tokens = sentence.nlp.tokens.map { removeContractions(it.toLowerCase()) }
        val words = sentence.wordnet.analyzedWords;

        return tokens.mapIndexed { index, token -> WordInfo(token, tags[index], words[index]?.lemma) }
    }

    private fun removeContractions(inputString: String): String {

        var normalForm = inputString

        normalForm = normalForm.replace("^wo$".toRegex(), "will")
        normalForm = normalForm.replace("n't".toRegex(), "not")
        normalForm = normalForm.replace("'re".toRegex(), "are")
        normalForm = normalForm.replace("'m".toRegex(), " am")
        normalForm = normalForm.replace("'ll".toRegex(), "will")
        normalForm = normalForm.replace("'ve".toRegex(), "have")

        // conversional
        normalForm = normalForm.replace("'d".toRegex(), "would")
        normalForm = normalForm.replace("'s".toRegex(), "is")

        return normalForm
    }
}