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

import mu.KotlinLogging
import weka.classifiers.meta.FilteredClassifier
import weka.core.Attribute
import weka.core.DenseInstance
import weka.core.Instances
import weka.core.SerializationHelper
import java.util.*

class TextClassifier(location: String) {

    companion object {
        private const val ATTR_CLASS = "class"
        private const val ATTR_TEXT = "text"
    }

    private val logger = KotlinLogging.logger {}

    private var classifier = SerializationHelper.read(this.javaClass.getResourceAsStream(location)) as FilteredClassifier

    private var classes: MutableList<String> = mutableListOf()

    fun registerClasses(vararg classes: String) {
        this.classes.addAll(Arrays.asList(*classes))
    }

    fun classify(text: String): Map<String, Double> {
        val instances = buildInstances(text)
        return classify(instances)
    }

    private fun buildInstances(text: String): Instances {

        val attributes = arrayListOf(
                Attribute(ATTR_CLASS, classes),
                Attribute(ATTR_TEXT, null as List<String>?)
        )

        val instances = Instances("Test relation", attributes, 1)
        instances.setClassIndex(0)

        val instance = DenseInstance(2)

        instance.setValue(attributes.last(), text)
        instances.add(instance)

        return instances
    }

    private fun classify(instances: Instances): Map<String, Double> {

        val distributionMap = HashMap<String, Double>()

        val distributions = classifier.distributionForInstance(instances.instance(0))
        for (i in distributions.indices) {
            val classValue = instances.classAttribute().value(i)
            val distribution = distributions[i]

            distributionMap.put(classValue, distribution)
        }

        return distributionMap
    }
}