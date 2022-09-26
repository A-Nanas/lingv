import utils.Form;
import utils.Lemma;

import java.util.*;

public class Morph {

    public Morph() {
    }

    private class Record {
        Lemma lemma = null;
        String part = null;
        Integer freq = 0;
    }

    public class AToken {
        String token = null;
        Set<Lemma> lemmas = null;

        AToken(String token, Set<Lemma> lemmas) {
            this.token = token;
            this.lemmas = lemmas;
        }

        Lemma getLemma() {
            return lemmas.iterator().next();
        }
    }

    String[] parts_of_speach = {"NOUN", "ADJF", "ADJS", "COMP", "VERB", "INFN",
            "PRTF", "PRTS", "GRND", "NUMR", "ADVB", "NPRO", "PRED", "PREP", "CONJ", "PRCL", "INTJ"};

    private final HashMap<String, ArrayList<Form>> forms = new HashMap<>();
    private final HashMap<Lemma, Record> records = new HashMap<>();

    public void put(String key, Form value) {
        if (forms.containsKey(key)) {
            forms.get(key).add(value);
        } else {
            ArrayList<Form> fs = new ArrayList<>();
            fs.add(value);
            forms.put(key, fs);
        }
    }

    public ArrayList<Form> get(String key) {
        return forms.get(key);
    }

    public ArrayList<Record> processTokens(ArrayList<String> tokens) {
        System.out.println("Result --->");
        int aboba = 0;
        int notFound = 0;

        double error = 0;
        double n = 0;


        for (var t : tokens) {
            ArrayList<Form> fs = forms.get(t);

            if (fs != null) {

                Set<Lemma> lls = new HashSet<>();
                for (var i : fs) {
                    lls.add(i.lemma);
                }

                if (lls.size() == 1) {
                    assert true;
                } else {
                    // compute error
                    double size = lls.size();
                    error += (size-1)/size;
                }
                Lemma l = ((Lemma) lls.toArray()[0]);
                aboba++;
                if (records.containsKey(l)) {
                    Record record = records.get(l);
                    record.freq += 1;
                } else {
                    Record record = new Record();
                    record.lemma = l;
                    record.part = l.grammemes.get(0);
                    record.freq = 1;
                    records.put(l, record);
                }
                n++;
            }
            else {
                notFound++;
                aboba++;

            }
        }
        error = error / n;
        return new ArrayList<>(records.values());
    }

    public ArrayList<AToken> lemmatize(ArrayList<String> tokens) {
        ArrayList<AToken> aTokens = new ArrayList<>();

        for (var t : tokens) {
            ArrayList<Form> fs = forms.get(t);

            if (fs != null) {

                Set<Lemma> lls = new HashSet<>();
                for (var i : fs) {
                    lls.add(i.lemma);
                }

                aTokens.add(new AToken(t, lls));

            } else {
                Set<Lemma> lemmas = new HashSet<>();

                Lemma new_lemma = new Lemma();
                new_lemma.word = t;
                lemmas.add(new_lemma);

                aTokens.add(new AToken(t, lemmas));
            }
        }

        return aTokens;
    }
}
