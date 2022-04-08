import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.ArrayList;

public class DictParser {

    public Processor parse(InputStream stream, ArrayList<Lemma> lemmas) throws XMLStreamException {
        Processor processor = new Processor();
        XMLInputFactory streamFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = streamFactory.createXMLStreamReader(stream);
        System.out.println("Parsing Opcorpora --->");
        boolean grammemaIsFormState = false;

        Form form = null;
        String key = null;
        Lemma lemma = null;

        while (reader.hasNext()) {
            reader.next();
            int event_type = reader.getEventType();
            switch (event_type) {
                case XMLStreamConstants.START_ELEMENT -> {
                    switch (reader.getLocalName()) {
                        case "grammemes":
                            break;
                        case "restrictions":
                            break;
                        case "lemmata":
                            break;
                        case "lemma":
                            grammemaIsFormState = false;
                            lemma = new Lemma();
                            for (int i = 0; i < reader.getAttributeCount(); i++) {
                                switch (reader.getAttributeLocalName(i)) {
                                    case "id" -> {}
                                    case "rev" -> lemma.rev = reader.getAttributeValue(i).trim();
                                    default -> System.out.println("Smth strange in <lemma>");
                                }
                            }
                            break;
                        case "l":
                            for (int i = 0; i < reader.getAttributeCount(); i++) {
                                if ("t".equals(reader.getAttributeLocalName(i))) {
                                    lemma.word = reader.getAttributeValue(i).trim();
                                } else {
                                    System.out.println("Smth strange in <l>");
                                }
                            }
                            break;
                        case "g":
                            for (int i = 0; i < reader.getAttributeCount(); i++) {
                                if ("v".equals(reader.getAttributeLocalName(i))) {
                                    if (!grammemaIsFormState) {
                                        lemma.grammemes.add(reader.getAttributeValue(i).trim());
                                    } else {
                                        form.grammemes.add(reader.getAttributeValue(i).trim());
                                    }
                                } else {
                                    System.out.println("Smth strange in <g>");
                                }
                            }
                            break;
                        case "f":
                            grammemaIsFormState = true;
                            form = new Form();
                            form.lemma = lemma;
                            for (int i = 0; i < reader.getAttributeCount(); i++) {
                                if ("t".equals(reader.getAttributeLocalName(i))) {
                                    key = reader.getAttributeValue(i);
                                } else {
                                    System.out.println("Smth strange in <f>");
                                }
                            }
                            break;
                    }
                }
                case XMLStreamConstants.END_ELEMENT -> {
                    if (reader.getLocalName().equals("lemma")) {
                        grammemaIsFormState = true;
                        lemmas.add(lemma);
                        lemma = null;
                    }
                    if (reader.getLocalName().equals("f")) {
                        grammemaIsFormState = false;
                        processor.put(key, form);
                        form = null;
                    }
                }
                default -> {}
            }
        }

        reader.close();
        System.out.println("---");
        return processor;
    }
}
