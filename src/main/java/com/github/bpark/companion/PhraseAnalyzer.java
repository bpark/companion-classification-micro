package com.github.bpark.companion;

import com.github.bpark.companion.model.phrase.QuestionType;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author ksr
 */
public class PhraseAnalyzer {

	private static final String[][] SEQUENCES = {
			{ "WRB" },
			{ "MD" },
			{ "WDT" },
			{ "WP" },
			{ "VBP", "PRP" },
			{ "WP", "VBP", "PRP", "VB" } };

    public QuestionType detectInterrogative(List<String> tokens, List<String> posTags) {

        boolean sequenceFound = Stream.of(SEQUENCES).anyMatch(seq -> hasStartingSequence(posTags, seq));

        boolean interrogative = sequenceFound && !tokens.get(tokens.size() - 1).equals("!");

        return interrogative ? QuestionType.evaluate(tokens, posTags) : null;

    }

    private boolean hasStartingSequence(List<String> posTags, String... sequence) {

        boolean hasSequence = false;

        if (posTags.get(0).equals(sequence[0])) {

            hasSequence = true;

            int lastPosition = 0;
            int currentPosition = 0;
            for (int i = 1; i < sequence.length; i++) {
                String seq = sequence[i];
                for (int j = 1; j < posTags.size(); j++) {
                    String posTag = posTags.get(j);
                    if (seq.equals(posTag)) {
                        currentPosition = j;
                        break;
                    }
                }
                if (currentPosition > lastPosition) {
                    lastPosition = currentPosition;
                } else {
                    hasSequence = false;
                    break;
                }
            }
        }

        return hasSequence;
    }

}
