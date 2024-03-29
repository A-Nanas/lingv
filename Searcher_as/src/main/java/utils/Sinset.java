package utils;

import java.util.ArrayList;

public class Sinset {
    public String descriptor;
    public ArrayList<Ngram> synonyms;

    public ArrayList<Sinset> hyperonims = new ArrayList<>();
    public ArrayList<Sinset> hyponims = new ArrayList<>();

    public ArrayList<Sinset> assoc = new ArrayList<>();

    public Sinset(String descriptor) {
        this.descriptor = descriptor;
        synonyms = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("{ " + descriptor);
        s.append(" [ ");
        for (var i : synonyms) {
            s.append(i.toString()).append(" ");
        }
        s.append(" ]");
        s.append(" [ ");
        for (var i : hyperonims) {
            s.append(i.descriptor).append(" ");
        }
        s.append(" ]");
        s.append(" [ ");
        for (var i : hyponims) {
            s.append(i.descriptor).append(" ");
        }
        s.append(" ]");
        return s.toString();
    }
}
