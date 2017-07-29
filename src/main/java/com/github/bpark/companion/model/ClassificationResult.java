package com.github.bpark.companion.model;

import java.util.List;

public class ClassificationResult {

    private List<PredictedSentence> sentences;

    public ClassificationResult(List<PredictedSentence> sentences) {
        this.sentences = sentences;
    }

    public List<PredictedSentence> getSentences() {
        return sentences;
    }
}
