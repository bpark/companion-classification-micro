package com.github.bpark.companion.model;

import java.util.Map;

public class ClassificationResult {

    private Map<String, Double> classifications;

    public ClassificationResult(Map<String, Double> classifications) {
        this.classifications = classifications;
    }

    public Map<String, Double> getClassifications() {
        return classifications;
    }
}
