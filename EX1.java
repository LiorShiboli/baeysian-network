import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
public class EX1 {
    public static void main(String[] args) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Get Document
            Document document = builder.parse(new File("alarm_net.xml"));

            // Normalize the xml structure
            document.getDocumentElement().normalize();
            //get all the variables
            NodeList variableList = document.getElementsByTagName("VARIABLE");
            //for each variable construct a "something" do we even want something here
            for (int variableNum = 0; variableNum < variableList.getLength() ; variableNum++) {
                System.out.println(variableNum);
                Node node = variableList.item(variableNum);
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element variable = (Element) node;
                    //name,just for the test
                    System.out.println(variable.getElementsByTagName("NAME").item(0).getTextContent());
                    //get the outcomes
                    NodeList outcomesList =variable.getElementsByTagName("OUTCOME");
                    for (int outcomesNum = 0; outcomesNum <outcomesList.getLength() ; outcomesNum++) {
                        System.out.println(outcomesList.item(outcomesNum).getTextContent());
                    }
                }
            }
            // for each
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
