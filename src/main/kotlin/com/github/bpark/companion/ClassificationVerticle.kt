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

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.bpark.companion.input.AnalyzedText
import com.github.bpark.companion.output.ClassificationResult
import com.github.bpark.companion.output.PredictedSentence
import io.vertx.core.json.Json
import io.vertx.rxjava.core.AbstractVerticle
import mu.KotlinLogging
import rx.Observable
import rx.Single
import kotlin.streams.toList

class ClassificationVerticle : AbstractVerticle() {

    private val logger = KotlinLogging.logger {}

    companion object {
        private const val ADDRESS = "classification.BASIC"

        private const val NLP_KEY = "nlp"
        private const val CLASSIFICATION_KEY = "classification"
    }

    @Throws(Exception::class)
    override fun start() {

        Json.mapper.registerModule(KotlinModule())

        vertx.rxExecuteBlocking<TextClassifier> { future ->

            try {

                val textClassifier = TextClassifier("/classifications/basic-dialogs.model")
                textClassifier.registerClasses("greeting", "farewell", "other")

                future.complete(textClassifier)

            } catch (e: Exception) {
                future.fail(e)
            }

        }.toObservable().subscribe(
                { this.registerAnalyzer(it) },
                { error -> logger.error("failed to load classifier models!", error) }
        )

    }

    private fun registerAnalyzer(classifier: TextClassifier) {
        val eventBus = vertx.eventBus()

        val consumer = eventBus.consumer<String>(ADDRESS)
        val observable = consumer.toObservable()

        observable.subscribe { message ->
            val id = message.body()

            readMessage(id).flatMap { (sentences) ->

                val classifications = sentences.stream().map { sentence ->

                    logger.info { "received sentence: $sentence" }

                    val classification = classifier.classify(sentence)

                    logger.info { "evaluated classification: $classification" }

                    classification
                }.toList()

                Observable.just(classifications)
            }.flatMap { classifications -> saveMessage(id, classifications) }.subscribe { message.reply(id) }
        }
    }

    private fun readMessage(id: String): Observable<AnalyzedText> {
        return vertx.sharedData().rxGetClusterWideMap<String, String>(id)
                .flatMap { map -> map.rxGet(NLP_KEY) }
                .flatMap { content -> Single.just(Json.decodeValue(content, AnalyzedText::class.java)) }
                .toObservable()
    }

    private fun saveMessage(id: String, analyses: List<Map<String, Double>>): Observable<Void> {

        val predictedSentences = analyses.map { PredictedSentence(it) }
        val classificationResult = ClassificationResult(predictedSentences)

        return vertx.sharedData().rxGetClusterWideMap<String, String>(id)
                .flatMap { map -> map.rxPut(CLASSIFICATION_KEY, Json.encode(classificationResult)) }
                .toObservable()
    }
}