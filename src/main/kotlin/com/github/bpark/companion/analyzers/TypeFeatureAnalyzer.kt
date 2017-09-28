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

package com.github.bpark.companion.analyzers

import com.github.bpark.companion.input.Sentence
import mu.KotlinLogging

/**
 * Short alias for analyzed token.
 */
typealias AnalyzedToken = Pair<String, String>

/**
 * Set of constants, fixed tags and predefined tokens.
 */
private object TokenConstants {

    const val VTAG = "VERB"
    const val JTAG = "JT"
    const val WHTAG = "WHQ"
    const val ITAG = ""
    const val QTAG = "?"
    const val ETAG = "!"

    val ANY = AnalyzedToken("", "*")
    val VERB = AnalyzedToken("_", VTAG)
    val START = AnalyzedToken("", "^")
    val QM = AnalyzedToken("QM", "")
    val EM = AnalyzedToken("EM", "")
    val DT = AnalyzedToken("DT", "")

    val ANALYZER_TAGS = listOf(WHTAG, VTAG, JTAG, ITAG)
}

/**
 * Interface for all transformer steps. Each transformer step processes a list of analyzed tokens.
 */
private interface SentenceTransformer {

    fun transform(analyzedTokens: List<AnalyzedToken>): List<AnalyzedToken>

    fun startsWith(item: String, list: List<String>): Boolean {
        return list.stream().anyMatch { item.startsWith(it) }
    }

}

/**
 * Transformer to remove all irrelevant tokens for further processing, only verbs and a set of specific tokens are
 * kept.
 */
private object RelevantTokenTransformer : SentenceTransformer {

    val jTypes = listOf("much", "often", "many", "far")

    /**
     * Removes all irrelevant tokens from the list.
     */
    override fun transform(analyzedTokens: List<AnalyzedToken>): List<AnalyzedToken> {
        val filteredTokens = analyzedTokens.map {
            if (startsWith(it.second, TokenConstants.ANALYZER_TAGS))
                it
            else
                TokenConstants.ANY
        }.toMutableList()
        filteredTokens.forEachIndexed { index, (first, second) ->
            run {
                if (second == TokenConstants.VTAG) filteredTokens[index] = TokenConstants.VERB
                if (second == TokenConstants.JTAG && !jTypes.contains(first)) filteredTokens[index] = TokenConstants.ANY
            }
        }
        return filteredTokens
    }

}

/**
 * Removes all duplicated tokens.
 */
private object DuplicateTokenTransformer : SentenceTransformer {

    override fun transform(analyzedTokens: List<AnalyzedToken>): List<AnalyzedToken> {
        val filtered = analyzedTokens.toMutableList()

        for (tag in TokenConstants.ANALYZER_TAGS) {
            val index = filtered.indexOfFirst { it.second == tag }
            if (index != -1) {
                val first = filtered[index]
                filtered.removeAll { it.second == tag }
                filtered.add(index, first)
            }
        }

        val reduced = mutableListOf<AnalyzedToken>()

        filtered.forEach { if (reduced.lastOrNull() != it) reduced.add(it) }

        return reduced
    }

}

/**
 * Defines the start token, relevant if a phrase starts with a WH-Token or a verb.
 */
private object StartTokenTransformer : SentenceTransformer {

    override fun transform(analyzedTokens: List<AnalyzedToken>): List<AnalyzedToken> {
        val (_, tag) = analyzedTokens.first()

        val tokens = analyzedTokens.toMutableList()

        if (tag == TokenConstants.WHTAG || tag == TokenConstants.VTAG) {
            tokens.add(0, TokenConstants.START)
        } else if (tag != "*") {
            tokens.add(0, TokenConstants.ANY)
        }

        return tokens
    }

}

/**
 * Fills the remaining set of tokens with ANY Tokens (*) until a defined size of the set.
 */
private object FillTokenTransformer : SentenceTransformer {

    override fun transform(analyzedTokens: List<AnalyzedToken>): List<AnalyzedToken> {
        val tokens = analyzedTokens.toMutableList()

        while (tokens.size < 8) {
            tokens.add(tokens.size - 1, TokenConstants.ANY)
        }

        return tokens
    }

}

/**
 * Defines the End-Token (.!?).
 */
private object EndTokenTransformer : SentenceTransformer {

    override fun transform(analyzedTokens: List<AnalyzedToken>): List<AnalyzedToken> {

        val tokens = analyzedTokens.toMutableList()

        val (token, _) = tokens.last()
        if (token == TokenConstants.QTAG) {
            tokens[tokens.lastIndex] = TokenConstants.QM
        }
        if (token == TokenConstants.ETAG) {
            tokens[tokens.lastIndex] = TokenConstants.EM
        }
        if (token == TokenConstants.ITAG) {
            tokens[tokens.lastIndex] = TokenConstants.DT
        }

        return tokens
    }

}

/**
 * Transforms a given sentence into a fixed-length sequence of defined tokens. This token sequence is used to
 * build an instance for classification.
 */
object SentenceTypeFeatureTransformer {

    private val logger = KotlinLogging.logger {}

    fun transform(sentence: Sentence): List<String> {

        logger.info { "analyzing sentence $sentence" }

        val tokens = sentence.nlp.tokens
        val tags = sentence.nlp.posTags

        val transformers = listOf(RelevantTokenTransformer,
                DuplicateTokenTransformer,
                StartTokenTransformer,
                FillTokenTransformer,
                EndTokenTransformer)

        var analyzedTokens = mapToAnalyzed(tokens, tags)

        for (transformer in transformers) {
            analyzedTokens = transformer.transform(analyzedTokens)
        }

        logger.info { "analyzed tokens $analyzedTokens" }

        return analyzedTokens.map { render(it) }
    }

    private fun mapToAnalyzed(tokens: List<String>, tags: List<String>): List<AnalyzedToken> {
        val tokenTags = mutableListOf<Pair<String, String>>()

        for ((index, token) in tokens.withIndex()) {
            var tag = tags[index]
            if (tag.startsWith("WRB") || tag.startsWith("WP")) {
                tag = TokenConstants.WHTAG
            }
            if (tag.startsWith("VB")) {
                tag = TokenConstants.VTAG
            }
            if (tag == "JJ" || tag == "RB") {
                tag = TokenConstants.JTAG
            }
            tokenTags.add(Pair(token.toLowerCase(), tag))
        }

        if (tags.last() != "") {
            tokenTags.add(AnalyzedToken("", ""))
        }

        return tokenTags
    }

    private fun render(analyzedToken: AnalyzedToken): String {
        val (token, tag) = analyzedToken
        return if (token.isNotEmpty()) {
            "($token/$tag)"
        } else {
            "($tag)"
        }
    }

}

