package com.github.bpark.companion.model;

import java.util.List;

public class ClassificationResult {

    private List<PredictedSentence> senteces;

    public ClassificationResult(List<PredictedSentence> senteces) {
        this.senteces = senteces;
    }

    public List<PredictedSentence> getSenteces() {
        return senteces;
    }
}
