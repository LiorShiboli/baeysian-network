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
import java.util.Comparator;
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
    
    public funcOutput Query(HashMap<String,String> givenVariables,String Query,String QueryOutcome,int algorithm){
        if (givenVariables.keySet().equals( new HashSet<>(Arrays.asList(CPTNodes.get(Query).getParents())))) {
            String key=QueryOutcome;
            for (int i = 0; i < CPTNodes.get(Query).getParents().length; i++) {
                key+=givenVariables.get(CPTNodes.get(Query).getParents()[i]);
            }
            return new funcOutput(CPTNodes.get(Query).getCPT().get(key));
        }
        
        
        funcOutput totalProbabilitySum =calculateTotalProbability( givenVariables,Query,QueryOutcome,algorithm);
        float QueryProbability = totalProbabilitySum.getOutput();
        //System.out.println(totalProbabilitySum.getAdditionOperations()+","+totalProbabilitySum.getMultOperations());
        for (String outcome : variableOutcomes.get(Query)) {
            if(!outcome.equals(QueryOutcome)){
            funcOutput outcomeProbability = calculateTotalProbability( givenVariables,Query,outcome,algorithm);
            //System.out.printf("%.9f", outcomeProbability);
           // System.out.println();
            totalProbabilitySum.add(outcomeProbability);
            //System.out.println(outcomeProbability.getAdditionOperations()+","+outcomeProbability.getMultOperations());
        }
            
        }
        totalProbabilitySum.updateOutput(QueryProbability/totalProbabilitySum.getOutput(), 0, 0);

        return totalProbabilitySum;

}
public funcOutput calculateTotalProbability(HashMap<String,String> givenVariables,String Query,String QueryOutcome,int algorithm){
    HashMap<String,String> variables=new HashMap<String,String>(givenVariables);
    variables.put(Query, QueryOutcome);
    Comparator<CPTNode> byFactorSize= new Comparator<CPTNode>() {
        @Override
        public int compare(CPTNode o1, CPTNode o2) {
            if (o1.getCPT().keySet().size()!=o2.getCPT().keySet().size()) {
                return o1.getCPT().keySet().size()-o2.getCPT().keySet().size();
                
            }
            return 0;
        }
    };
    switch(algorithm) {
        case 1:
          return naiveCalculateProbability(variables);
          
        case 2:
          return VECalculateProbabilty(variables,byFactorSize);
          
        case 3:
          //return VECalculateProbabilty(variables,by<to be created>);
      }
      return null;

}
private funcOutput VECalculateProbabilty(HashMap<String, String> variables,Comparator<String> comparator) {
    return null;
}

private funcOutput naiveCalculateProbability(HashMap<String, String> givenVariables) {
    //System.out.println("calculate probability");
    /* for 2nd function //find all ancestors of given variables
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
    
    int additionOperations=0;
    int multOperations=0;
    funcOutput queryOutput= new funcOutput(0);
    HashMap<String,String[]> variableMap = new HashMap<String,String[]>(variableOutcomes);
    for (String variable : givenVariables.keySet()) {
        //System.out.println(variable);
        variableMap.replace(variable, new  String[]{givenVariables.get(variable)});
    }
    String[] order =variableMap.keySet().toArray(new String[variableMap.size()]);
    
    
    permutation_iterator permutation = new permutation_iterator(variableMap, order );
    String variable=permutation.getVariables()[0];
    String key=permutation.getkey(CPTNodes.get(permutation.getVariables()[0]).getKeyOrder());
    float probability=CPTNodes.get(variable).getCPT().get(key);
    for (int i = 1; i < permutation.getVariables().length; i++) {
        variable=permutation.getVariables()[i];
        key=permutation.getkey(CPTNodes.get(variable).getKeyOrder());
        probability*=CPTNodes.get(variable).getCPT().get(key);
        multOperations++;
    }
     while (permutation.hasNext()) {
        
        permutation.next();
        variable=permutation.getVariables()[0];
        key=permutation.getkey(CPTNodes.get(permutation.getVariables()[0]).getKeyOrder());
        float permutationProbability=CPTNodes.get(variable).getCPT().get(key);
        for (int i = 1; i < permutation.getVariables().length; i++) {
            variable=permutation.getVariables()[i];
            key=permutation.getkey(CPTNodes.get(variable).getKeyOrder());
            permutationProbability*=CPTNodes.get(variable).getCPT().get(key);
            multOperations++;
        }
        probability+=permutationProbability;
        additionOperations++;
    }

    
    return queryOutput.updateOutput(probability,multOperations,additionOperations);
}







}

