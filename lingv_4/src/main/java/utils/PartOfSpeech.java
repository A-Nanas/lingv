package utils;

import java.util.Arrays;

public class PartOfSpeech {
    String[] parts_of_speech = {"NOUN", "ADJF", "ADJS", "COMP", "VERB", "INFN",
            "PRTF", "PRTS", "GRND", "NUMR", "ADVB", "NPRO", "PRED", "PREP", "CONJ", "PRCL", "INTJ"};
    public String token;

    public PartOfSpeech(String token) {
        this.token = token;
    }

    public boolean isPartOfSpeech() {
        return Arrays.asList(parts_of_speech).contains(token);
    }
}
