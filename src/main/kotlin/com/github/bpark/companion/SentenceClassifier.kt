package com.github.bpark.companion

import mu.KotlinLogging
import weka.classifiers.trees.J48
import weka.core.SerializationHelper
import java.util.*

class SentenceClassifier {

    private val logger = KotlinLogging.logger {}

    private var classifier: J48? = null

    private var classes: MutableList<String> = mutableListOf()

    fun loadClassifier(location: String) {
        val inputStream = this.javaClass.getResourceAsStream(location)
        classifier = SerializationHelper.read(inputStream) as J48
    }

    fun registerClasses(vararg classes: String) {
        this.classes.addAll(Arrays.asList(*classes))
    }

    /*
    private fun buildInstances(text: String): Instances {

        val attributeClass = Attribute(ATTR_CLASS, classes)
        val attributeText = Attribute(ATTR_TEXT, null as List<String>?)

        val attributes = ArrayList<Attribute>()
        attributes.add(attributeClass)
        attributes.add(attributeText)

        val instances = Instances("Test relation", attributes, 1)
        instances.setClassIndex(0)

        val instance = DenseInstance(2)

        instance.setValue(attributeText, text)
        instances.add(instance)

        return instances
    } */
}