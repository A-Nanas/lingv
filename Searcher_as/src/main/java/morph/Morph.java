package morph;

import utils.Form;
import utils.Lemma;

import java.util.*;

public class Morph {
    public Morph() {
    }
    public class AToken {
        public String token = null;
        Set<Lemma> lemmas = null;
        AToken(String token, Set<Lemma> lemmas) {
            this.token = token;
            this.lemmas = lemmas;
        }
        public Lemma getLemma() {
            return lemmas.iterator().next();
        }
    }

    private final HashMap<String, ArrayList<Form>> forms = new HashMap<>();

    public void put(String key, Form value) {
        if (forms.containsKey(key)) {
            forms.get(key).add(value);
        } else {
            ArrayList<Form> fs = new ArrayList<>();
            fs.add(value);
            forms.put(key, fs);
        }
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
