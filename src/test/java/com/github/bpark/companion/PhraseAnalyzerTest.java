package com.github.bpark.companion;

import com.github.bpark.companion.model.phrase.QuestionType;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class PhraseAnalyzerTest {

    private PhraseAnalyzer phraseAnalyzer;

    @Before
    public void init() {
        phraseAnalyzer = new PhraseAnalyzer();
    }

    @Test
    public void testPeopleQuestion() throws Exception {

        QuestionType questionType = phraseAnalyzer.detectInterrogative(
                asList("Who", "is", "the", "best", "football", "player", "in", "the", "world", "?"),
                asList("WP", "VBZ", "DT", "JJS", "NN", "NN", "IN", "DT", "NN", ".")
        );
        assertEquals(QuestionType.PEOPLE, questionType);

        questionType = phraseAnalyzer.detectInterrogative(
                asList("Who", "are", "your", "best", "friends", "?"),
                asList("WP", "VBP", "PRP$", "JJS", "NNS", ".")
        );
        assertEquals(QuestionType.PEOPLE, questionType);

        questionType = phraseAnalyzer.detectInterrogative(
                asList("Who", "is", "that", "strange", "guy", "over", "there", "?"),
                asList("WP", "VBZ", "DT", "JJ", "NN", "IN", "RB", ".")
        );
        assertEquals(QuestionType.PEOPLE, questionType);

    }

    @Test
    public void testLocationQuestion() throws Exception {

        QuestionType questionType = phraseAnalyzer.detectInterrogative(
                asList("Where", "is", "the", "library", "?"),
                asList("WRB", "VBZ", "DT", "NN", ".")
        );
        assertEquals(QuestionType.LOCATION, questionType);

        questionType = phraseAnalyzer.detectInterrogative(
                asList("Where", "do", "you", "live", "?"),
                asList("WRB", "VBP", "PRP", "VB", ".")
        );
        assertEquals(QuestionType.LOCATION, questionType);

        questionType = phraseAnalyzer.detectInterrogative(
                asList("Where", "are", "my", "shoes", "?"),
                asList("WRB", "VBP", "PRP$", "NNS", ".")
        );
        assertEquals(QuestionType.LOCATION, questionType);

    }

    @Test
    public void testOccasionQuestion() throws Exception {

        QuestionType questionType = phraseAnalyzer.detectInterrogative(
                asList("When", "do", "the", "shops", "open", "?"),
                asList("WRB", "VBP", "DT", "NNS", "JJ", ".")
        );
        assertEquals(QuestionType.OCCASION, questionType);

        questionType = phraseAnalyzer.detectInterrogative(
                asList("When", "is", "his", "birthday", "?"),
                asList("WRB", "VBZ", "PRP$", "NN", ".")
        );
        assertEquals(QuestionType.OCCASION, questionType);

        questionType = phraseAnalyzer.detectInterrogative(
                asList("When", "are", "we", "going", "to", "finish", "?"),
                asList("WRB", "VBP", "PRP", "VBG", "TO", "VB", ".")
        );
        assertEquals(QuestionType.OCCASION, questionType);

    }

    @Test
    public void testReasonQuestion() throws Exception {

        QuestionType questionType = phraseAnalyzer.detectInterrogative(
                asList("Why", "do", "we", "need", "a", "nanny", "?"),
                asList("WRB", "VBP", "PRP", "VB", "DT", "NN", ".")
        );
        assertEquals(QuestionType.REASON, questionType);

        questionType = phraseAnalyzer.detectInterrogative(
                asList("Why", "are", "they", "always", "late", "?"),
                asList("WRB", "VBP", "PRP", "RB", "RB", ".")
        );
        assertEquals(QuestionType.REASON, questionType);

        questionType = phraseAnalyzer.detectInterrogative(
                asList("Why", "does", "he", "complain", "all", "the", "time", "?"),
                asList("WRB", "VBZ", "PRP", "VB", "DT", "DT", "NN", ".")
        );
        assertEquals(QuestionType.REASON, questionType);

    }

    @Test
    public void testInformationQuestion() throws Exception {

        QuestionType questionType = phraseAnalyzer.detectInterrogative(
                asList("What", "is", "your", "name", "?"),
                asList("WP", "VBZ", "PRP$", "NN", ".")
        );
        assertEquals(QuestionType.INFORMATION, questionType);

        questionType = phraseAnalyzer.detectInterrogative(
                asList("What", "is", "her", "favourite", "colour", "?"),
                asList("WP", "VBZ", "PRP$", "JJ", "NN", ".")
        );
        assertEquals(QuestionType.INFORMATION, questionType);

        questionType = phraseAnalyzer.detectInterrogative(
                asList("What", "is", "the", "time", "?"),
                asList("WP", "VBZ", "DT", "NN", ".")
        );
        assertEquals(QuestionType.INFORMATION, questionType);

    }

    @Test
    public void testChoiceQuestion() throws Exception {

        QuestionType questionType = phraseAnalyzer.detectInterrogative(
                asList("Which", "drink", "did", "you", "order", "?"),
                asList("WDT", "NN", "VBD", "PRP", "NN", ".")
        );
        assertEquals(QuestionType.CHOICE, questionType);

        questionType = phraseAnalyzer.detectInterrogative(
                asList("Which", "day", "do", "you", "prefer", "for", "a", "meeting", "?"),
                asList("WDT", "NN", "VBP", "PRP", "VB", "IN", "DT", "NN", ".")
        );
        assertEquals(QuestionType.CHOICE, questionType);

        questionType = phraseAnalyzer.detectInterrogative(
                asList("Which", "is", "better", "-", "this", "one", "or", "that", "one", "?"),
                asList("WDT", "VBZ", "JJR", ":", "DT", "CD", "CC", "DT", "CD", ".")
        );
        assertEquals(QuestionType.CHOICE, questionType);

    }

    @Test
    public void testDescriptionQuestion() throws Exception {

        QuestionType questionType = phraseAnalyzer.detectInterrogative(
                asList("How", "do", "you", "cook", "paella", "?"),
                asList("WRB", "VBP", "PRP", "VB", "NN", ".")
        );
        assertEquals(QuestionType.DESCRIPTION, questionType);

        questionType = phraseAnalyzer.detectInterrogative(
                asList("How", "does", "he", "know", "the", "answer", "?"),
                asList("WRB", "VBZ", "PRP", "VB", "DT", "NN", ".")
        );
        assertEquals(QuestionType.DESCRIPTION, questionType);

        questionType = phraseAnalyzer.detectInterrogative(
                asList("How", "can", "I", "learn", "English", "quickly", "?"),
                asList("WRB", "MD", "PRP", "VB", "JJ", "RB", ".")
        );
        assertEquals(QuestionType.DESCRIPTION, questionType);

    }

    @Test
    public void testQuantityQuestion() throws Exception {

        QuestionType questionType = phraseAnalyzer.detectInterrogative(
                asList("How", "much", "money", "will", "I", "need", "?"),
                asList("WRB", "JJ", "NN", "MD", "PRP", "VB", ".")
        );
        assertEquals(QuestionType.QUANTITY, questionType);

        questionType = phraseAnalyzer.detectInterrogative(
                asList("How", "much", "time", "do", "you", "have", "to", "finish", "the", "test", "?"),
                asList("WRB", "JJ", "NN", "VBP", "PRP", "VB", "TO", "VB", "DT", "NN", ".")
        );
        assertEquals(QuestionType.QUANTITY, questionType);

    }

    @Test
    public void testFrequencyQuestion() throws Exception {

        QuestionType questionType = phraseAnalyzer.detectInterrogative(
                asList("How", "often", "does", "she", "study", "?"),
                asList("WRB", "RB", "VBZ", "PRP", "VB", ".")
        );
        assertEquals(QuestionType.FREQUENCY, questionType);

        questionType = phraseAnalyzer.detectInterrogative(
                asList("How", "often", "do", "you", "visit", "your", "grandmother", "?"),
                asList("WRB", "RB", "VBP", "PRP", "VB", "PRP$", "NN", ".")
        );
        assertEquals(QuestionType.FREQUENCY, questionType);

        questionType = phraseAnalyzer.detectInterrogative(
                asList("How", "often", "are", "you", "sick", "?"),
                asList("WRB", "RB", "VBP", "PRP", "JJ", ".")
        );
        assertEquals(QuestionType.FREQUENCY, questionType);

    }

    @Test
    public void testDistanceQuestion() throws Exception {

        QuestionType questionType = phraseAnalyzer.detectInterrogative(
                asList("How", "far", "is", "the", "bus", "stop", "from", "here", "?"),
                asList("WRB", "RB", "VBZ", "DT", "NN", "NN", "IN", "RB", ".")
        );
        assertEquals(QuestionType.DISTANCE, questionType);

        questionType = phraseAnalyzer.detectInterrogative(
                asList("How", "far", "is", "the", "university", "from", "your", "house", "?"),
                asList("WRB", "RB", "VBZ", "DT", "NN", "IN", "PRP$", "NN", ".")
        );
        assertEquals(QuestionType.DISTANCE, questionType);

    }
}
