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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

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
    
    public float Query(HashMap<String,String> givenVariables,String Query,String QueryOutcome,int algorithm){
        float probability=0;
        if (givenVariables.keySet().equals( new HashSet<>(Arrays.asList(CPTNodes.get(Query).getParents())))) {
            String key=QueryOutcome;
            for (int i = 0; i < CPTNodes.get(Query).getParents().length; i++) {
                key+=givenVariables.get(CPTNodes.get(Query).getParents()[i]);
            }
            return CPTNodes.get(Query).getCPT().get(key);
        }
        float QueryProbabilty=calculateProbability( givenVariables,Query,QueryOutcome,algorithm);
        float totalProbabilitySum=QueryProbabilty;
        for (String outcome : variableOutcomes.get(Query)) {
            if(outcome!=QueryOutcome)
            totalProbabilitySum+=calculateProbability( givenVariables,Query,outcome,algorithm);
        }
        probability=totalProbabilitySum/QueryProbabilty;

        return probability;

}
public float calculateProbability(HashMap<String,String> givenVariables,String Query,String QueryOutcome,int algorithm){
    float probability=0;
    switch(algorithm) {
        case 1:
          probability = naiveCalculateProbability(givenVariables,Query,QueryOutcome);
          break;
        case 2:
          //probability = VECalculateProbabilty(givenVariables,Query,QueryOutcome);
          break;
        case 3:
          //probability = heuristicVECAlculateProbability(givenVariables,Query,QueryOutcome);
      }

    return probability;
}
private float naiveCalculateProbability(HashMap<String, String> givenVariables, String query, String queryOutcome) {
    float probability=1;
    HashMap<String,String[]> variableMap = new HashMap<String,String[]>(variableOutcomes);
    for (String variable : givenVariables.keySet()) {
        variableMap.replace(variable, new  String[]{givenVariables.get(variable)});
    }
    
    permutation_iterator permutation= new permutation_iterator(variableMap,(String[])variableMap.keySet().toArray() );
    while (permutation.hasNext()){
        for (CPTNode cptNode : iterable) {
            
        }
    }
    
    return probability;
}

public static boolean compareArrays(String[] arr1, String[] arr2) {
    HashSet<String> set1 = new HashSet<String>(Arrays.asList(arr1));
    HashSet<String> set2 = new HashSet<String>(Arrays.asList(arr2));
    return set1.equals(set2);
}
}

