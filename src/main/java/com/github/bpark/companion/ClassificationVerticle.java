/*
 * Copyright 2017 Kurt Sparber
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
package com.github.bpark.companion;

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
import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassificationVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(ClassificationVerticle.class);

    private static final String ADDRESS = "classification.BASIC";

    private static final String NLP_KEY = "nlp";
    private static final String CLASSIFICATION_KEY = "classification";

    private static final String ATTR_CLASS = "class";
    private static final String ATTR_TEXT = "text";


    @Override
    public void start() throws Exception {

        vertx.<Classifier>rxExecuteBlocking(future -> {

            try {

                Classifier classifier = loadClassifier();

                future.complete(classifier);

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

    private void registerAnalyzer(Classifier classifier) {
        EventBus eventBus = vertx.eventBus();

        MessageConsumer<String> consumer = eventBus.consumer(ADDRESS);
        Observable<Message<String>> observable = consumer.toObservable();

        observable.subscribe(message -> {
            String id = message.body();

            readMessage(id).flatMap(analyzedText -> {

                List<Map<String, Double>> classifications = analyzedText.getSentences().stream().map(sentence -> {
                    logger.info("received sentence: {}", sentence);

                    Instances instances = buildInstances(sentence.getRaw());

                    Map<String, Double> classification = classify(classifier, instances);

                    logger.info("evaluated classification: {}", classification);

                    return classification;
                }).collect(Collectors.toList());

                return Observable.just(classifications);
            }).flatMap(classifications -> saveMessage(id, classifications)).subscribe(a -> message.reply(id));
        });
    }

    private Instances buildInstances(String text) {

        List<String> classes = new ArrayList<>();
        classes.add("greeting");
        classes.add("goodbye");
        classes.add("other");
        Attribute attributeClass = new Attribute(ATTR_CLASS, classes);
        Attribute attributeText = new Attribute(ATTR_TEXT, (List<String>)null);

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(attributeClass);
        attributes.add(attributeText);

        Instances instances = new Instances("Test relation", attributes, 1);
        instances.setClassIndex(0);

        Instance instance = new DenseInstance(2);

        instance.setValue(attributeText, text);
        instances.add(instance);

        return instances;
    }

    private Map<String, Double> classify(Classifier classifier, Instances instances) {

        Map<String, Double> distributionMap = new HashMap<>();

        try {

            double[] distributions = classifier.distributionForInstance(instances.instance(0));
            for (int i = 0; i < distributions.length; i++) {
                String classValue = instances.classAttribute().value(i);
                double distribution = distributions[i];

                distributionMap.put(classValue, distribution);
            }
        } catch (Exception e) {
            logger.error("Error during classification", e);
        }

        return distributionMap;
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
