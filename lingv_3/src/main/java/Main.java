import utils.Lemma;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        ArrayList<Lemma> lemmas = new ArrayList<>();
        Morph morph = new Morph();
//        int aaa = 0;

         long now = System.currentTimeMillis();
        System.out.println("time: " + now);
        try (InputStream stream = new FileInputStream("src/main/resources/dict.opcorpora.xml")) {
            Parser parser = new Parser();

            parser.parse(stream, lemmas, morph);
        } catch (IOException | XMLStreamException e) {
            e.printStackTrace();
        }

        now = System.currentTimeMillis();
        System.out.println("time: " + now);


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


        ArrayList<String> tokens;
        text = text1 + "\n ==== \n" + text2 + "\n ==== \n" + text3 + "\n ==== \n" + text4 + "\n ==== \n" + text5 +
                "\n ==== \n" + text6 + "\n ==== \n" + text7;
        text = text.toLowerCase();
        tokens = Tokenizer.tokenize(text);

        ArrayList<Morph.AnnToken> annTokens = morph.lemmatize(tokens);
        int[] t = new int[7];


        now = System.currentTimeMillis();
        System.out.println("time: " + now);


        TokensMap ngrams = new TokensMap();
        for (int n = 3; n < 15; n++) {


            int text_id = 0;


            for (int i = 1; i < annTokens.size() - n - 1; i++) {
                List<String> ngram = new ArrayList<>();

                boolean wasBeginningOfNewText = false;
                for (int j = i; j < i + n; j++) {
                    if (annTokens.get(j).token.equals("====")) {
                        wasBeginningOfNewText = true;
                        break;
                    }
                    ngram.add(annTokens.get(j).getLemma());
                }

                if (n == 3) {
                    t[text_id]++;
                }
                if (wasBeginningOfNewText) {
//                    aaa++;
                    text_id += 1;
                    i += 3;
                    continue;
                }

                ngrams.add(ngram, annTokens.get(i - 1).getLemma(),
                        annTokens.get(i + ngram.size() + 1).getLemma(), text_id);
            }
            ngrams.map = ngrams.map.entrySet().stream().filter(x -> x.getValue().freq > 1).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }


        now = System.currentTimeMillis();
        System.out.println("time: " + now);


        ngrams.map = ngrams.map.entrySet().stream().filter(x -> x.getValue().freq > 1).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        double P = 1;
        System.out.println("----N-граммы----");
        for (var i : ngrams.map.entrySet()) {
            i.getValue().tfIdf = Math.max(TokensMap.countTfIdf(i, 0, t[0]),
                    Math.max(TokensMap.countTfIdf(i, 1, t[1]), Math.max(TokensMap.countTfIdf(i, 2, t[2]),
                    Math.max(TokensMap.countTfIdf(i, 3, t[3]), Math.max(TokensMap.countTfIdf(i, 4, t[4]),
                    Math.max(TokensMap.countTfIdf(i, 5, t[5]), TokensMap.countTfIdf(i, 6, t[6])))))));
        }

        for (var i : ngrams.map.entrySet().stream().sorted(Comparator.comparing(x -> -x.getValue().tfIdf))
                .limit(2000).collect(Collectors.toList())) {
            Optional<Integer> maxAO = i.getValue().As.map.
                    values().stream().
                    max(Integer::compareTo);

            Optional<Integer> maxBO = i.getValue().Bs.map.
                    values().stream().
                    max(Integer::compareTo);

            if (maxAO.isPresent() && maxBO.isPresent()) {
                Integer maxA = maxAO.get();
                Integer maxB = maxBO.get();

                double f_xn = i.getValue().freq;
                double f_axn = maxA;
                double f_xnb = maxB;

                if (f_axn / f_xn <= P && f_xnb / f_xn <= P) {

                    System.out.print("\" ");
                    i.getKey().forEach(x -> System.out.print(x + " "));
//                    System.out.println(i.getValue().text_ids.toString());
                    System.out.println("\" |частота: " + f_xn + " | текстова€ частота: " +
                            i.getValue().text_ids.getSize() +
//                            "| Left: " + f_axn / f_xn + " Right: " + f_xnb / f_xn +
                            "| TF-IDF: " + i.getValue().tfIdf);
//                    System.out.println(i.getValue().tfIdf);
//                    System.out.printf(" TF-IDF: 1 - %.6f; TF-IDF: 2 - %.3f; TF-IDF: 3 - %.3f; TF-IDF: 4 - %.3f; " +
//                                    "TF-IDF: 5 - %.3f; TF-IDF: 6 - %.3f; TF-IDF: 7 - %.3f; \n", TokensMap.countTfIdf(i, 0, t[0]),
//                            TokensMap.countTfIdf(i, 1, t[1]), TokensMap.countTfIdf(i, 2, t[2]),
//                            TokensMap.countTfIdf(i, 3, t[3]), TokensMap.countTfIdf(i, 4, t[4]),
//                            TokensMap.countTfIdf(i, 5, t[5]), TokensMap.countTfIdf(i, 6, t[6]));
//                            " TF-IDF: 1 - " + TokensMap.countTfIdf(i, 0, t[0]) +
//                            " TF-IDF: 2 - " + TokensMap.countTfIdf(i, 1, t[1]) +
//                            " TF-IDF: 3 - " + TokensMap.countTfIdf(i, 2, t[2]) +
//                            " TF-IDF: 4 - " + TokensMap.countTfIdf(i, 3, t[3]) +
//                            " TF-IDF: 5 - " + TokensMap.countTfIdf(i, 4, t[4]) +
//                            " TF-IDF: 6 - " + TokensMap.countTfIdf(i, 5, t[5]) +
//                            " TF-IDF: 7 - " + TokensMap.countTfIdf(i, 6, t[6]));
                }
            }
        }


        now = System.currentTimeMillis();
        System.out.println("time: " + now);


        System.out.print("---количество N-грамм: " + ngrams.map.size() + "---");
    }

    private static boolean punctCheck(String str) {
        return str.equals(".") || str.equals("===") || str.equals(":") || str.equals(";")
                || str.equals("?") || str.equals("!") || str.equals("Ч") || str.equals("-") || str.equals(",");
    }

}
