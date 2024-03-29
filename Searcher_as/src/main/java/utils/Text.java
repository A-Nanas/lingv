package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Text {
    public List<String> tokens = new ArrayList<>();
    public static class Context {
        public double tf;
        public double idf;
        public int freq = 0;
        public Context(int tf, int idf) { this.tf = tf; this.idf = idf; }
    }
    public final Map<Sinset, Context> entries = new HashMap<>();

    private void updateFreqInAssoc(Sinset s) {
        if (entries.get(s) != null)
            entries.get(s).freq++;
    }

    private void updateFreq(Sinset s, double height) {
        if (s == null)
            return;
        if (height <= 1)
            entries.get(s).freq++;
        if (height == 0) {
            for (var a : s.assoc)
                updateFreqInAssoc(a);
        }

        List<Sinset> sinsets = s.hyperonims;
        for (var i : sinsets)
            updateFreq(i, height+1);
    }

    public void computeFreq(Sinset sinset) {
        for (Ngram g : sinset.synonyms) {
            for (int i = 0; i <= tokens.size() - g.size(); i++) {
                Ngram ngramm = new Ngram(tokens.subList(i, i + g.size()));
                if (g.equals(ngramm)) {
                    updateFreq(sinset, 0);
                }
            }
        }
    }

    public void findTf() {
        for (var context : entries.values()) {
            context.tf = (double) context.freq / (double) tokens.size();
        }
    }

    public void findIdf(List<Text> texts, Sinset sinset) {
        double D = texts.size();
        double Dn = 0;

        for (var d : texts) {
            if (d.entries.get(sinset).freq > 0)
                Dn += 1;
        }
        entries.get(sinset).idf = Math.log(D / (Dn + 1f));
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        tokens.forEach(x -> s.append(x).append(" "));
        s.append("\n");
        for (var i : entries.keySet())
            if (entries.get(i).freq > 0)
                s.append("(").append(i.descriptor).append(" ").
                    append(entries.get(i).freq).append(" ").append(") ");
        return s.toString();
    }
}
