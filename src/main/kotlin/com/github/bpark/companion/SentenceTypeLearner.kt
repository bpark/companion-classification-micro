package com.github.bpark.companion

import weka.classifiers.Classifier
import weka.classifiers.Evaluation
import weka.classifiers.trees.J48
import weka.core.Instances
import weka.core.converters.ArffLoader
import java.io.BufferedReader
import java.io.FileReader
import java.util.*

class SentenceTypeLearner {

    fun loadDataset(fileName: String): Instances {
        BufferedReader(FileReader(fileName)).use { reader ->
            val arff = ArffLoader.ArffReader(reader)
            return arff.data
        }
    }

    fun evaluate(classifier: Classifier, trainData: Instances) {
        trainData.setClassIndex(0)

        val eval = Evaluation(trainData)

        val folds = 3
        eval.crossValidateModel(classifier, trainData, folds, Random(1))

        println(eval.toSummaryString())
        println(eval.toClassDetailsString())

    }

    fun learn(trainData: Instances): Classifier {
        trainData.setClassIndex(0)

        val classifier = J48()
        classifier.options = arrayOf()

        classifier.buildClassifier(trainData)

        return classifier
    }

}
