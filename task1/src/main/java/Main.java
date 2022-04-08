import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        ArrayList<Lemma> lemmas = new ArrayList<>();
        Processor processor = null;
        ArrayList<String> tokens = new ArrayList<>();

        try (InputStream stream = new FileInputStream("src/main/resources/dict.opcorpora.xml")) {
            DictParser parser = new DictParser();
            processor = parser.parse(stream, lemmas);
        } catch (IOException | XMLStreamException e) {
            e.printStackTrace();
        }

        try {
            FileReader fr = null;
                fr = new FileReader("src/main/resources/1_Garri-Potter-i-Filosofskiy-kamen.txt");
            StreamTokenizer st = new StreamTokenizer(fr);
            st.lowerCaseMode(true);
            while (st.nextToken() != StreamTokenizer.TT_EOF) {
                if (st.ttype == StreamTokenizer.TT_WORD) {
                    tokens.add(st.sval);
                }
            }

            fr = new FileReader("src/main/resources/2_Garri-Potter-i-Taynaya-komnata.txt");
            st = new StreamTokenizer(fr);
            st.lowerCaseMode(true);
            while (st.nextToken() != StreamTokenizer.TT_EOF) {
                if (st.ttype == StreamTokenizer.TT_WORD) {
                    tokens.add(st.sval);
                }
            }

            fr = new FileReader("src/main/resources/3_Garri-Potter-i-uznik-Azkabana.txt");
            st = new StreamTokenizer(fr);
            st.lowerCaseMode(true);
            while (st.nextToken() != StreamTokenizer.TT_EOF) {
                if (st.ttype == StreamTokenizer.TT_WORD) {
                    tokens.add(st.sval);
                }
            }

            fr = new FileReader("src/main/resources/4_Garri-Potter-i-Kubok-ognya.txt");
            st = new StreamTokenizer(fr);
            st.lowerCaseMode(true);
            while (st.nextToken() != StreamTokenizer.TT_EOF) {
                if (st.ttype == StreamTokenizer.TT_WORD) {
                    tokens.add(st.sval);
                }
            }

            fr = new FileReader("src/main/resources/5_Garri-Potter-i-Orden-feniksa.txt");
            st = new StreamTokenizer(fr);
            st.lowerCaseMode(true);
            while (st.nextToken() != StreamTokenizer.TT_EOF) {
                if (st.ttype == StreamTokenizer.TT_WORD) {
                    tokens.add(st.sval);
                }
            }

            fr = new FileReader("src/main/resources/6_Garri-Potter-i-Princ-polukrovka.txt");
            st = new StreamTokenizer(fr);
            st.lowerCaseMode(true);
            while (st.nextToken() != StreamTokenizer.TT_EOF) {
                if (st.ttype == StreamTokenizer.TT_WORD) {
                    tokens.add(st.sval);
                }
            }

            fr = new FileReader("src/main/resources/7_Garri-Potter-i-Dary-smerti.txt");
            st = new StreamTokenizer(fr);
            st.lowerCaseMode(true);
            while (st.nextToken() != StreamTokenizer.TT_EOF) {
                if (st.ttype == StreamTokenizer.TT_WORD) {
                    tokens.add(st.sval);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        processor.processOpcorpora(tokens);
        String result = processor.out();

        File outputFile = new File("src/main/resources/statistics.txt");
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(result.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
