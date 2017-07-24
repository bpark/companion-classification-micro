package com.github.bpark.companion;

import weka.core.stopwords.StopwordsHandler;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author ksr
 */
public class DialogueStopWords implements StopwordsHandler, Serializable {

    private static final long serialVersionUID = -7227952739823739L;

    private static final List<String> STOPS = Arrays.asList("I", "i", "it", "It");

    @Override
    public boolean isStopword(String word) {
        return STOPS.contains(word);
    }
}
