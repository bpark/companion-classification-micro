package com.github.bpark.companion.model;

import java.util.Map;

/**
 * @author ksr
 */
public class PredictedSentence {

    private Map<String, Double> predictions;

    public PredictedSentence(Map<String, Double> predictions) {
        this.predictions = predictions;
    }

    public Map<String, Double> getPredictions() {
        return predictions;
    }
}
