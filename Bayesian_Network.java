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
import java.util.HashMap;

public class Bayesian_Network {
private HashMap<String,String[]> variableOutcomes;
private HashMap<String,CPTNode> CPTNodes;
//creates new bayesian network from an xml file (takes an address or name if in the same file)
public Bayesian_Network(String file){
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Get Document
        Document document = builder.parse(new File(file));

        // Normalize the xml structure
        document.getDocumentElement().normalize();
        //get all the variables
        NodeList variableList = document.getElementsByTagName("VARIABLE");
        //construct variableOutcomes
        HashMap<String,String[]> varOutcomes = new HashMap<String,String[]>();
        for (int variableNum = 0; variableNum < variableList.getLength() ; variableNum++) {
            Node node = variableList.item(variableNum);
            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element variable = (Element) node;
                String variableName= (String) variable.getElementsByTagName("NAME").item(0).getTextContent();
                //get the outcomes
                NodeList outcomesList =variable.getElementsByTagName("OUTCOME");
                String[] outcomes= new String[outcomesList.getLength()];
                for (int i = 0; i < outcomesList.getLength() ; i++) {
                    outcomes[i] = outcomesList.item(i).getTextContent();
                }
                varOutcomes.put(variableName,outcomes);
            }

        }



        NodeList CPTdefinitions = document.getElementsByTagName("DEFINITION");
        HashMap<String,CPTNode> CPTNodes=new HashMap<String,CPTNode>();
        for (int definitionNum = 0; definitionNum < CPTdefinitions.getLength() ;definitionNum++) {
            Node node = CPTdefinitions.item(definitionNum);
            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element definition = (Element) node;
                CPTNode CPT= new CPTNode(varOutcomes,definition);
                CPTNodes.put(CPT.getVariable(), CPT);
            }

        }
        this.variableOutcomes=varOutcomes;
        this.CPTNodes=CPTNodes;

    } catch (ParserConfigurationException e) {
        e.printStackTrace();
    } catch (SAXException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }

}
}
