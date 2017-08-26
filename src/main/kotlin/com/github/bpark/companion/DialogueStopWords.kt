package com.github.bpark.companion

import weka.core.stopwords.StopwordsHandler
import java.io.Serializable
import java.util.*

class DialogueStopWords : StopwordsHandler, Serializable {

    companion object {

        private const val serialVersionUID = -7227952739823739L

        private val STOPS = Arrays.asList("I", "i", "it", "It")
    }

    override fun isStopword(word: String): Boolean {
        return STOPS.contains(word)
    }

}