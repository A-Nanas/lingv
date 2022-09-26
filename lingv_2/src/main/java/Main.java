import utils.Lemma;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static boolean punctCheck(String str) {
        return str.equals(".") || str.equals(",") || str.equals("===") || str.equals(":") || str.equals(";")
                || str.equals("?") || str.equals("!") || str.equals("—");
    }

    public static void main(String[] args) throws IOException {

        String phrase = "по обезвреживанию опасных";
        System.out.println(phrase);
        Integer neighbours_size = 6;

        ArrayList<Lemma> lemmas = new ArrayList<>();
        Morph morph = new Morph();

        // parse dictionary
        try (InputStream stream = new FileInputStream("src/main/resources/dict.opcorpora.xml")) {
            Parser parser = new Parser();
            parser.parse(stream, lemmas, morph);
        } catch (IOException | XMLStreamException e) {
            e.printStackTrace();
        }

        // get tokens from text
//        String text = null;
//        try {
//            text = Files.readString(Paths.get("src/main/resources/5_Garri-Potter-i-Orden-feniksa.txt"), StandardCharsets.UTF_8);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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

        ArrayList<String> tokens;
        tokens = Tokenizer.tokenize(text);

        ArrayList<Morph.AnnToken> annTokens = morph.lemmatize(tokens);

        // find occurrences of the phrase in tokens
        ArrayList<Morph.AnnToken> window = morph.lemmatize(Tokenizer.tokenize(phrase));
        ArrayList<Integer> occurs = new ArrayList<>();
        for (int window_shift = 0; window_shift < annTokens.size() - window.size(); window_shift++) {
            boolean found = true;
            for (int i = 0; i < window.size(); i++) {
                Set<String> intersection = new HashSet<>(annTokens.get(i+window_shift).lemmas);
                intersection.retainAll(window.get(i).lemmas);
                if (intersection.size() == 0) {
                    found = false;
                    break;
                }
            }
            if (found) {
                occurs.add(window_shift);
            }
        }

        System.out.println("All occurrences found");
        System.out.println(occurs.size());

        TokensMap lefts = new TokensMap();
        for (var i : occurs) {
            for (int left_shift = 0; left_shift < neighbours_size; left_shift++) {
                List<String> context = new ArrayList<>();
                boolean wrongPunct = false;
                // left occurs
                for (int sh = left_shift; sh > 0; sh--) {
                    if (!wrongPunct) {
                        wrongPunct = punctCheck(annTokens.get(i - sh).token);
                    }
                    context.add(annTokens.get(i - sh).lemmas.stream().limit(1).collect(Collectors.joining()));
                }
                // middle occurs
                for (int sh = 0; sh < window.size(); sh++) {
                    context.add(annTokens.get(i + sh).lemmas.stream().limit(1).collect(Collectors.joining()));
                }
                if (wrongPunct) {
                    break;
                }
                lefts.add(context);
            }
        }

        TokensMap rights = new TokensMap();
        for (var i : occurs) {
            for (int right_shift = 0; right_shift < neighbours_size; right_shift++) {
                List<String> context = new ArrayList<>();
                boolean wrongPunct = false;
                // middle occurrences
                for (int sh = 0; sh < window.size(); sh++) {
                    context.add(annTokens.get(i + sh).lemmas.stream().limit(1).collect(Collectors.joining()));
                }
                // right occurrences
                for (int sh = 0; sh < right_shift; sh++) {
                    if (!wrongPunct) {
                        wrongPunct = punctCheck(annTokens.get(i + window.size() + sh).token);
                    }
                    context.add(annTokens.get(i + window.size() + sh).lemmas.stream().limit(1).collect(Collectors.joining()));
                }
                if (wrongPunct) {
                    break;
                }
                rights.add(context);
            }
        }

        System.out.println("Left contexts: ");
        List<TokensMap.Info> resultsL = new ArrayList<>(lefts.map.values());
        resultsL.sort(Comparator.comparing(x -> x.freq));
        for (var i : resultsL) {
            System.out.print("\"");
            i.ngram.forEach(x -> System.out.print(x + " "));
            System.out.println("\" " + i.freq);
        }

        System.out.println("Right contexts: ");
        List<TokensMap.Info> resultsR = new ArrayList<>(rights.map.values());
        resultsR.sort(Comparator.comparing(x -> x.freq));
        for (var i : resultsR) {
            System.out.print("\"");
            i.ngram.forEach(x -> System.out.print(x + " "));
            System.out.println("\" " + i.freq);
        }


        // process text
        //morph.processTokens(tokens);
        //String result = morph.printRecords();

        // write result to output file
        /*File outputFile = new File("src/main/resources/output.txt");
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(result.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
