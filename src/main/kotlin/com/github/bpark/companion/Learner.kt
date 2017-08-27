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
        //classifier.options = arrayOf("-C 0.05", "-M 1", "-N 3")
        //options.add("-U") // unpruned tree
        //options.add("-C 0.05")         // confidence threshold for pruning. (Default: 0.25)
        //options.add("-M 1")            // minimum number of instances per leaf. (Default: 2)
        //options.add("-R");            // use reduced error pruning. No subtree raising is performed.
        //options.add("-N 3")            // number of folds for reduced error pruning. One fold is used as the pruning set. (Default: 3)
        //options.add("-B");            // Use binary splits for nominal attributes.
        //options.add("-S");            // not perform subtree raising.
        //options.add("-L");            // not clean up after the tree has been built.
        //options.add("-A");            // if set, Laplace smoothing is used for predicted probabilites.
        //options.add("-Q");            // The seed for reduced-error pruning.

        classifier.buildClassifier(trainData)

        return classifier
    }

}