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

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.bpark.companion.input.AnalyzedText
import io.vertx.core.json.Json
import org.apache.commons.io.IOUtils
import org.junit.Assert
import org.junit.Test
import java.nio.charset.StandardCharsets


class SentenceFeatureAnalyzerTest {

    @Test
    fun testSize() {

        Json.mapper.registerModule(KotlinModule())

        val content = IOUtils.toString(this.javaClass.getResourceAsStream("/test-sentences.json"), StandardCharsets.UTF_8)

        val analyzedText = Json.decodeValue(content, AnalyzedText::class.java)

        analyzedText.sentences.forEach {
            val transform = SentenceFeatureTransformer.transform(it)
            Assert.assertEquals(transform.joinToString(" "), 8, transform.size)

            val raw = it.raw

            println(raw)
            println("$transform")
        }

    }
}