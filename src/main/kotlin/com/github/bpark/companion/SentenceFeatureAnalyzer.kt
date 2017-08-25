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

package com.github.bpark.companion

import com.github.bpark.companion.input.Sentence
import mu.KotlinLogging

/**
 * @author bpark
 */
typealias AnalyzedToken = Pair<String, String>

private object TokenConstants {

    const val VTAG = "V"
    const val JTAG = "J"
    const val WHTAG = "WH"
    const val ITAG = "."

    val ANY = AnalyzedToken("", "*")
    val VERB = AnalyzedToken("_", VTAG)
    val START = AnalyzedToken("", "^")

    val ANALYZER_TAGS = listOf<String>(WHTAG, VTAG, JTAG, ITAG)
}

private interface SentenceTransformer {

    fun transform(analyzedTokens: List<AnalyzedToken>): List<AnalyzedToken>

    fun startsWith(item: String, list: List<String>): Boolean {
        return list.stream().anyMatch { item.startsWith(it) }
    }

}

private object RelevantTokenTransformer : SentenceTransformer {

    val jTypes = listOf<String>("much", "often", "many", "far")

    override fun transform(analyzedTokens: List<AnalyzedToken>): List<AnalyzedToken> {
        val filteredTokens = analyzedTokens.map { if (startsWith(it.second, TokenConstants.ANALYZER_TAGS)) it else TokenConstants.ANY }.toMutableList()
        filteredTokens.forEachIndexed { index, pair ->
            run {
                if (pair.second == TokenConstants.VTAG) filteredTokens.set(index, TokenConstants.VERB)
                if (pair.second == TokenConstants.JTAG && !jTypes.contains(pair.first) ) filteredTokens.set(index, TokenConstants.ANY)
            }
        }
        return filteredTokens
    }

}

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

private object FillTokenTransformer : SentenceTransformer {

    override fun transform(analyzedTokens: List<AnalyzedToken>): List<AnalyzedToken> {
        val tokens = analyzedTokens.toMutableList()

        while (tokens.size < 8) {
            tokens.add(tokens.size - 1, TokenConstants.ANY)
        }

        return tokens
    }

}


object SentenceFeatureTransformer {

    private val logger = KotlinLogging.logger {}

    fun transform(sentence: Sentence): List<AnalyzedToken> {

        logger.info { "analyzing sentence $sentence" }

        val tokens = sentence.tokens
        val tags = sentence.posTags

        val transformers = listOf<SentenceTransformer>(RelevantTokenTransformer,
                DuplicateTokenTransformer,
                StartTokenTransformer,
                FillTokenTransformer)

        var analyzedTokens = mapToAnalyzed(tokens, tags)

        for (transformer in transformers) {
            analyzedTokens = transformer.transform(analyzedTokens)
        }

        logger.info { "analyzed tokens $analyzedTokens" }

        return analyzedTokens
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
            tokenTags.add(Pair(token, tag))
        }

        if (tags.last() != ".") {
            tokenTags.add(AnalyzedToken(".", "."))
        }

        return tokenTags
    }

}

fun main(args: Array<String>) {
    val transform = SentenceFeatureTransformer.transform(Sentence("What is your name?", listOf("What", "is", "your", "name", "?"), listOf("WP", "VBZ", "PRP$", "NN", ".")))
    println()
    println(transform.joinToString(" "))
}
