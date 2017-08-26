package com.github.bpark.companion

import weka.classifiers.Classifier
import weka.classifiers.Evaluation
import weka.classifiers.bayes.NaiveBayes
import weka.classifiers.meta.FilteredClassifier
import weka.classifiers.trees.J48
import weka.core.Instances
import weka.core.converters.ArffLoader
import weka.core.stemmers.LovinsStemmer
import weka.filters.unsupervised.attribute.StringToWordVector
import java.io.BufferedReader
import java.io.FileReader
import java.util.*

abstract class AbstractLearner {

    abstract fun learn(trainData: Instances): Classifier

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

}

class TextClassifierLearner: AbstractLearner() {

    override fun learn(trainData: Instances): Classifier {
        trainData.setClassIndex(0)
        val filter = StringToWordVector()
        filter.stopwordsHandler = DialogueStopWords()
        filter.stemmer = LovinsStemmer()
        filter.attributeIndices = "last"
        val classifier = FilteredClassifier()
        classifier.filter = filter
        classifier.classifier = NaiveBayes()


        classifier.buildClassifier(trainData)

        return classifier
    }
}

class SentenceTypeLearner: AbstractLearner() {

    override fun learn(trainData: Instances): Classifier {
        trainData.setClassIndex(0)

        val classifier = J48()
        classifier.options = arrayOf()

        classifier.buildClassifier(trainData)

        return classifier
    }

}