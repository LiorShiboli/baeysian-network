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
import java.util.Set;

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
        float QueryProbability=calculateTotalProbability( givenVariables,Query,QueryOutcome,algorithm);
        float totalProbabilitySum=QueryProbability;
       // System.out.printf("%.9f", QueryProbability);
        //System.out.println();
        for (String outcome : variableOutcomes.get(Query)) {
            if(!outcome.equals(QueryOutcome)){
            Float outcomeProbability= calculateTotalProbability( givenVariables,Query,outcome,algorithm);
            //System.out.printf("%.9f", outcomeProbability);
           // System.out.println();
            totalProbabilitySum+= outcomeProbability;
        }
            
        }
        probability=QueryProbability/totalProbabilitySum;

        return probability;

}
public float calculateTotalProbability(HashMap<String,String> givenVariables,String Query,String QueryOutcome,int algorithm){
    System.out.println("calculate probability");
    float probability=0;
    HashMap<String,String> variables=new HashMap<String,String>(givenVariables);
    variables.put(Query, QueryOutcome);
    switch(algorithm) {
        case 1:
          probability = naiveCalculateProbability(variables);
          break;
        case 2:
          //probability = VECalculateProbabilty(variables);
          break;
        case 3:
          //probability = heuristicVECAlculateProbability(variables);
      }

    return probability;
}
private float naiveCalculateProbability(HashMap<String, String> givenVariables) {
    float probability=0;
    /*//find all ancestors of given variables
    Set<String> ancestorSet= new HashSet<String>(givenVariables.keySet());
    int size=0;
    while (size!=ancestorSet.size()) {
        size=ancestorSet.size();
        for (String variable : ancestorSet) {
            for (String parent : CPTNodes.get(variable).getParents()) {
                ancestorSet.add(parent);
            }
        }
        
    }
    */
    HashMap<String,String[]> variableMap = new HashMap<String,String[]>(variableOutcomes);
    for (String variable : givenVariables.keySet()) {
        //System.out.println(variable);
        variableMap.replace(variable, new  String[]{givenVariables.get(variable)});
    }
    String[] order =variableMap.keySet().toArray(new String[variableMap.size()]);
    
    
    permutation_iterator permutation= new permutation_iterator(variableMap, order );
    for (int j = 0; j < order.length; j++) {
        System.out.print(order[j]);
    }
    System.out.println();
    
     do{
        float permutationProbability=1;
        for (String variable: permutation.getVariables() ) {
            String key=permutation.getkey(CPTNodes.get(variable).getKeyOrder());
           permutationProbability*=CPTNodes.get(variable).getCPT().get(key);
        }
        System.out.print( permutation.getPermutation()+"=");
        System.out.printf("%.15f", permutationProbability);
        System.out.println();
        probability+=permutationProbability;
    }while(permutation.next());
    System.out.printf("%.15f", probability);
    System.out.println();
    
    return probability;
}

public static boolean compareArrays(String[] arr1, String[] arr2) {
    HashSet<String> set1 = new HashSet<String>(Arrays.asList(arr1));
    HashSet<String> set2 = new HashSet<String>(Arrays.asList(arr2));
    return set1.equals(set2);
}
}

