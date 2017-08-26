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
import weka.classifiers.trees.J48
import weka.core.Attribute
import weka.core.DenseInstance
import weka.core.Instances
import weka.core.SerializationHelper
import java.util.*

class SentenceClassifier(location: String) {

    companion object {
        private const val ATTR_CLASS = "type"
        private const val ATTR_START = "start"
        private const val ATTR_P1 = "p1"
        private const val ATTR_P2 = "p2"
        private const val ATTR_P3 = "p3"
        private const val ATTR_P4 = "p4"
        private const val ATTR_P5 = "p5"
        private const val ATTR_P6 = "p6"
        private const val ATTR_END = "end"
    }

    private val logger = KotlinLogging.logger {}

    private var classifier: J48 = SerializationHelper.read(this.javaClass.getResourceAsStream(location)) as J48

    private var classes: MutableList<String> = mutableListOf()


    fun registerClasses(vararg classes: String) {
        this.classes.addAll(Arrays.asList(*classes))
    }

    fun classify(tokens: List<String>): Map<String, Double> {
        val instances = buildInstances(tokens)
        return classify(instances)
    }

    private fun buildInstances(tokens: List<String>): Instances {

        val attributes = arrayListOf(
                Attribute(ATTR_CLASS, classes),
                Attribute(ATTR_START, null as List<String>?),
                Attribute(ATTR_P1, null as List<String>?),
                Attribute(ATTR_P2, null as List<String>?),
                Attribute(ATTR_P3, null as List<String>?),
                Attribute(ATTR_P4, null as List<String>?),
                Attribute(ATTR_P5, null as List<String>?),
                Attribute(ATTR_P6, null as List<String>?),
                Attribute(ATTR_END, null as List<String>?)
        )

        val instances = Instances("Test relation", attributes, 1)
        instances.setClassIndex(0)

        val instance = DenseInstance(9)

        attributes.drop(1).forEachIndexed { index, attribute -> instance.setValue(attribute, tokens[index]) }

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