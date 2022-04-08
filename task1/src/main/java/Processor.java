import java.util.*;

public class Processor {

    private static class Record {
        Lemma lemma = null;
        String part = null;
        Integer frequency = 0;
    }

    private final HashMap<String, ArrayList<Form>> forms = new HashMap<>();
    private final HashMap<Lemma, Record> records = new HashMap<>();

    public void put(String key, Form value) {
        if (forms.containsKey(key)) {
            forms.get(key).add(value);
        } else {
            ArrayList<Form> form = new ArrayList<>();
            form.add(value);
            forms.put(key, form);
        }
    }

    public ArrayList<Form> get(String key) {
        return forms.get(key);
    }

    public ArrayList<Record> processOpcorpora(ArrayList<String> tokens) {
        System.out.println("The results of processing:");
        int amountOfTokens = tokens.size();
        int notFound = 0;
        double error = 0;
        double amountOfFound = 0;

        for (String t : tokens) {
            ArrayList<Form> formsOnKey = forms.get(t);
            if (formsOnKey != null) {
                Set<Lemma> lemmas = new HashSet<>();
                for (var i : formsOnKey) {
                    lemmas.add(i.lemma);
                }
                if (lemmas.size() != 1) {
                    error += (double) (lemmas.size()-1)/lemmas.size();
                }
                Lemma l = ((Lemma) lemmas.toArray()[0]);

                if (records.containsKey(l)) {
                    Record record = records.get(l);
                    record.frequency += 1;
                } else {
                    Record record = new Record();
                    record.lemma = l;
                    record.part = l.grammemes.get(0); // part of speech is always in 0 index of grammemes
                    record.frequency = 1;
                    records.put(l, record);
                }
                amountOfFound++;
            }
            else {
                notFound++;
            }
        }

        System.out.println(" Слов рассмотренно : " + amountOfTokens);
        System.out.println(" Нераспознано слов : " + (notFound * 100 / (amountOfFound + notFound)) + " %");
        System.out.println(" Точность определения леммы слова : " + ((1 - error/amountOfFound)*100) + " %");
        return new ArrayList<>(records.values());
    }

    public String out() {
        StringBuilder sb = new StringBuilder();
        ArrayList<Record> records_arr = new ArrayList<>(records.values());
        records_arr.sort(Comparator.comparing(r -> -r.frequency));
        System.out.println(" Статистика по леммам :\n---");
        for (var i : records_arr) {
            System.out.println(i.lemma.word + " " + i.frequency + " " + i.part);
            sb.append(i.lemma.word).append(" ").append(i.frequency)
                    .append(" ").append(i.part).append("\n");
        }
        System.out.println("---");
        return sb.toString();
    }
}
