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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bpark
 */
public class TextClassifier {

    private static final String ATTR_CLASS = "class";
    private static final String ATTR_TEXT = "text";

    private static final Logger logger = LoggerFactory.getLogger(TextClassifier.class);

    private FilteredClassifier filteredClassifier;

    private List<String> classes;


    public void loadClassifier(String location) throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream(location);
        filteredClassifier = (FilteredClassifier) SerializationHelper.read(inputStream);
    }

    public void registerClasses(String... classes) {
        this.classes = new ArrayList<>(Arrays.asList(classes));
    }

    public Map<String, Double> classify(String text) {
        Instances instances = buildInstances(text);
        return classify(instances);
    }

    private Instances buildInstances(String text) {

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

    private Map<String, Double> classify(Instances instances) {

        Map<String, Double> distributionMap = new HashMap<>();

        try {

            double[] distributions = filteredClassifier.distributionForInstance(instances.instance(0));
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
