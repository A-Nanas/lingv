import utils.*;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    static ArrayList<Model> parseModels(String path) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(path));
        ArrayList<Model> models = new ArrayList<>();
        int models_num = sc.nextInt();
        for (int i = 0; i < models_num; i++) {
            Model model = new Model();
            int units_num = sc.nextInt();

            boolean readNgram = false;


            for (int j = 0; j < units_num; j++) {
                String token = sc.next();

                if (token.equals("{")) {
                    readNgram = true;
                    model.createNewNgram();
                    continue;
                }
                if (token.equals("}")) {
                    readNgram = false; continue;
                }

                if (!readNgram) {
                    model.units.add(new PartOfSpeech(token));
                } else {
                    model.popNgram().words.add(new PartOfSpeech(token));
                }

            }
            models.add(model);
        }
        sc.close();
        return models;
    }

    public static void main(String[] args) throws IOException {

        // Integer freq_max = 100;

        ArrayList<Lemma> lemmas = new ArrayList<>();
        Morph morph = new Morph();

        try (InputStream stream = new FileInputStream("src/main/resources/dict.opcorpora2.xml")) {
            Parser parser = new Parser();

            parser.parse(stream, lemmas, morph);
        } catch (IOException | XMLStreamException e) {
            e.printStackTrace();
        }

        String text = null;
        String text1 = null;
        try {
            text1 = Files.readString(
                    Paths.get("src/main/resources/1_Garri-Potter-i-Filosofskiy-kamen.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String text2 = null;
        try {
            text2 = Files.readString(
                    Paths.get("src/main/resources/2_Garri-Potter-i-Taynaya-komnata.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String text3 = null;
        try {
            text3 = Files.readString(
                    Paths.get("src/main/resources/3_Garri-Potter-i-uznik-Azkabana.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String text4 = null;
        try {
            text4 = Files.readString(
                    Paths.get("src/main/resources/4_Garri-Potter-i-Kubok-ognya.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String text5 = null;
        try {
            text5 = Files.readString(
                    Paths.get("src/main/resources/5_Garri-Potter-i-Orden-feniksa.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String text6 = null;
        try {
            text6 = Files.readString(
                    Paths.get("src/main/resources/6_Garri-Potter-i-Princ-polukrovka.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String text7 = null;
        try {
            text7 = Files.readString(
                    Paths.get("src/main/resources/7_Garri-Potter-i-Dary-smerti.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        text = text1 + "\n ==== \n" + text2 + "\n ==== \n" + text3 + "\n ==== \n" + text4 + "\n ==== \n" + text5 +
                "\n ==== \n" + text6 + "\n ==== \n" + text7;
        text = text.toLowerCase();

        ArrayList<String> tokens;
        tokens = Tokenisator.tokenize(text);

        // lemmatized tokens from corpora
        ArrayList<Morph.AToken> aTokens = morph.lemmatize(tokens);


//        Scanner sc = new Scanner(new File("src/main/resources/semantics.txt"));
        Scanner sc = new Scanner(new File("src/main/resources/semantics.txt"));
        ArrayList<SemanticPiece> semantics = new ArrayList<>();
        //System.out.println(new File("src/main/resources/semantics.txt"));
        if (sc == null) {
            System.out.println("troubles : semantics.txt readed as null");
        }
        if (!sc.hasNext()) {
            System.out.println("empty semantics.txt");
        }
        int n = sc.nextInt();
        for (int i = 0; i < n; i++) {
            SemanticPiece unit = new SemanticPiece(sc.next());
            int lemm_n = sc.nextInt();
            for (int j = 0; j < lemm_n; j++)
                unit.entries.add(sc.next());
            semantics.add(unit);
        }

        /*for (var i : lemmas)
            System.out.println(i.word);*/

        ArrayList<Model> models = parseModels("src/main/resources/models.txt");

        /*
         * Split tokens into sentences
         */

        ArrayList<Sentence> sentences = new ArrayList<>();
        Sentence new_one = new Sentence();
        for (var token : aTokens) {
            if (token.token.equals(".") || token.token.equals("!") || token.token.equals("?")) {
                sentences.add(new_one);
                new_one = new Sentence();
                continue;
            }
            new_one.add(token.getLemma());
        }

        /*
         * Then let's find sentences for every model
         */

        System.out.println("=== Semantic models ===");
        for (var model : models) {

            System.out.print("MODEL: ");
            for (var unit : model.units) {
                System.out.print(unit.token + " ");
            }
            for (var ngram : model.ngrams) {
                System.out.print("{ ");
                ngram.words.forEach(x -> System.out.print(x.token + " "));
                System.out.println("} ");
            }
            System.out.println();

            List<Sentence> answer = findAllSectences(sentences, model, semantics);

            System.out.println("FOUND SENTENCES: " + answer.size());
            System.out.println("FREQ: " + ((double) answer.size() /  (double) sentences.size()));

            answer.stream().limit(7).forEach(x -> System.out.println(x.toString()));
            System.out.println();
        }

    }

    public static List<Sentence>
    findAllSectences(List<Sentence> sentences, Model model, List<SemanticPiece> semantics) {
        List<Sentence> rightSentences = new ArrayList<>();

        for (var sentence : sentences) {
            int counter = 0;
            for (var model_unit : model.units) {
                if (model_unit.isPartOfSpeech()) {

                    for (var word : sentence.lemmas) {
                        if (word.grammemes.size() > 0 && model_unit.token.equals(word.grammemes.get(0))) {
                            counter++;
                            break;
                        }
                    }

                } else {

                    SemanticPiece unit = null;
                    for (var s : semantics) {
                        if (s.unit_name.equals(model_unit.token)) {
                            unit = s;
                        }
                    }
                    assert unit != null;

                    for (var word : sentence.lemmas) {
                        if (unit.contains(word.word)) {
                            counter++;
                            break;
                        }
                    }
                }
            }

            for (var ngram : model.ngrams) {
                if (findNgram(sentence, ngram)) {
                    counter++;
                }
            }

            if (counter == model.units.size() + model.ngrams.size()) {
                rightSentences.add(sentence);
            }
        }
        return rightSentences;
    }

    public static boolean findNgram(Sentence s, Ngram ngram) {
        for (int i = 0; i < s.length()-ngram.words.size(); i++) {
            boolean found = true;
            for (int j = 0; j < ngram.words.size(); j++) {

                if (ngram.words.get(j).isPartOfSpeech()) {
                    if (s.get(i + j).grammemes.size() == 0 ||
                            !s.get(i + j).grammemes.get(0).equals(ngram.words.get(j).token)) {
                        found = false;
                    }
                } else {
                    if (!s.get(i + j).word.equals(ngram.words.get(j).token)) {
                        found = false;
                    }
                }
            }
            if (found) {
                return true;
            }
        }
        return false;
    }
}
