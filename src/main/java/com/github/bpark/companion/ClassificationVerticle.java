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
package com.github.bpark.companion;

import com.fasterxml.jackson.module.kotlin.KotlinModule;
import com.github.bpark.companion.input.AnalyzedText;
import com.github.bpark.companion.model.ClassificationResult;
import com.github.bpark.companion.model.PredictedSentence;
import io.vertx.core.json.Json;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.eventbus.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Single;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.SerializationHelper;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author bpark
 */
public class ClassificationVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(ClassificationVerticle.class);

    private static final String ADDRESS = "classification.BASIC";

    private static final String NLP_KEY = "nlp";
    private static final String CLASSIFICATION_KEY = "classification";


    @Override
    public void start() throws Exception {

        Json.mapper.registerModule(new KotlinModule());

        vertx.<TextClassifier>rxExecuteBlocking(future -> {

            try {

                TextClassifier textClassifier = new TextClassifier();
                textClassifier.loadClassifier("/classifications/basic-dialogs.model");
                textClassifier.registerClasses("greeting", "farewell", "other");

                future.complete(textClassifier);

            } catch (Exception e) {
                future.fail(e);
            }

        }).toObservable().subscribe(
                this::registerAnalyzer,
                error -> logger.error("faild to load classifier models!", error)
        );

    }

    private FilteredClassifier loadClassifier() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/classifications/basic-dialogs.model");
        return (FilteredClassifier) SerializationHelper.read(inputStream);
    }

    private void registerAnalyzer(TextClassifier classifier) {
        EventBus eventBus = vertx.eventBus();

        MessageConsumer<String> consumer = eventBus.consumer(ADDRESS);
        Observable<Message<String>> observable = consumer.toObservable();

        observable.subscribe(message -> {
            String id = message.body();

            readMessage(id).flatMap(analyzedText -> {

                List<Map<String, Double>> classifications = analyzedText.getSentences().stream().map(sentence -> {
                    logger.info("received sentence: {}", sentence);

                    Map<String, Double> classification = classifier.classify(sentence.getRaw());

                    logger.info("evaluated classification: {}", classification);

                    return classification;
                }).collect(Collectors.toList());

                return Observable.just(classifications);
            }).flatMap(classifications -> saveMessage(id, classifications)).subscribe(a -> message.reply(id));
        });
    }

    private Observable<AnalyzedText> readMessage(String id) {
        return vertx.sharedData().<String, String>rxGetClusterWideMap(id)
                .flatMap(map -> map.rxGet(NLP_KEY))
                .flatMap(content -> Single.just(Json.decodeValue(content, AnalyzedText.class)))
                .toObservable();
    }

    private Observable<Void> saveMessage(String id, List<Map<String, Double>> analyses) {

        List<PredictedSentence> predictedSentences = analyses.stream().map(PredictedSentence::new).collect(Collectors.toList());
        ClassificationResult classificationResult = new ClassificationResult(predictedSentences);

        return vertx.sharedData().<String, String>rxGetClusterWideMap(id)
                .flatMap(map -> map.rxPut(CLASSIFICATION_KEY, Json.encode(classificationResult)))
                .toObservable();
    }

}
