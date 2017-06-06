package com.github.bpark.companion;

import io.vertx.core.Handler;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassificationVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(ClassificationVerticle.class);

    private static final String ADDRESS = "classification.BASIC";

    private static final String ATTR_CLASS = "class";
    private static final String ATTR_TEXT = "text";


    @Override
    public void start() throws Exception {
        Classifier classifier = loadClassifier();

        EventBus eventBus = vertx.eventBus();

        eventBus.consumer(ADDRESS, (Handler<Message<String>>) message -> {
            String sentence = message.body();

            logger.info("received sentence: {}", sentence);

            Instances instances = buildInstances(sentence);

            Map<String, Double> classification = classify(classifier, instances);

            logger.info("evaluated classification: {}", classification);

            message.reply(classification);
        });
    }

    private FilteredClassifier loadClassifier() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/classifications/basic-dialogs.model");
        return (FilteredClassifier) SerializationHelper.read(inputStream);
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

}
