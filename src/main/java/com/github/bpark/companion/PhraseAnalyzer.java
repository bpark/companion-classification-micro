/*
 * Copyright 2017 bpark
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.bpark.companion;

import com.github.bpark.companion.model.phrase.QuestionType;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author bpark
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
