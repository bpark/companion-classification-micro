package com.github.bpark.companion.output


data class PredictedSentence(val predictions: Map<String, Double>)

data class ClassificationResult(val sentences: List<PredictedSentence>)