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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    
    public funcOutput naiveQuery(HashMap<String,String> givenVariables,String Query,String QueryOutcome){
        if (givenVariables.keySet().equals( new HashSet<>(Arrays.asList(CPTNodes.get(Query).getParents())))) {
            String key=QueryOutcome;
            for (int i = 0; i < CPTNodes.get(Query).getParents().length; i++) {
                key+=givenVariables.get(CPTNodes.get(Query).getParents()[i]);
            }
            return new funcOutput(CPTNodes.get(Query).getCPT().get(key));
        }
        
        
        funcOutput totalProbabilitySum =naiveCalculatejointProbability( givenVariables,Query,QueryOutcome);
        float QueryProbability = totalProbabilitySum.getOutput();
        //System.out.println(totalProbabilitySum.getAdditionOperations()+","+totalProbabilitySum.getMultOperations() +","+totalProbabilitySum.getOutput());
        for (String outcome : variableOutcomes.get(Query)) {
            if(!outcome.equals(QueryOutcome)){
            funcOutput outcomeProbability = naiveCalculatejointProbability( givenVariables,Query,outcome);
            //System.out.printf("%.9f", outcomeProbability.getOutput());
           // System.out.println();
            totalProbabilitySum.add(outcomeProbability);
            //System.out.println(outcomeProbability.getAdditionOperations()+","+outcomeProbability.getMultOperations());
        }
            
        }
        totalProbabilitySum.updateOutput(QueryProbability/totalProbabilitySum.getOutput(), 0, 0);

        return totalProbabilitySum;

}
    
    
public funcOutput VECalculateProbabilty(HashMap<String, String> givenVariables, String Query,String  QueryOutcome,int algorithm) {
    //if  we can answer the query straight answer it
    if (givenVariables.keySet().equals( new HashSet<>(Arrays.asList(CPTNodes.get(Query).getParents())))) {
        String key=QueryOutcome;
        for (int i = 0; i < CPTNodes.get(Query).getParents().length; i++) {
            key+=givenVariables.get(CPTNodes.get(Query).getParents()[i]);
        }
        return new funcOutput(CPTNodes.get(Query).getCPT().get(key));
    }


    //initialize variables
    funcOutput probabilityOutput = new funcOutput(0);
    //find all ancestors of given variables
    Set<String> ancestorSet= new HashSet<String>(givenVariables.keySet());
    int size=0;
    ancestorSet.add(Query);
    while (size!=ancestorSet.size()) {
        size=ancestorSet.size();
        for (String variable : ancestorSet) {
            for (String parent : CPTNodes.get(variable).getParents()) {
                ancestorSet.add(parent);
            }
        }
        
    }


    ancestorSet.removeAll(givenVariables.keySet());
    


    Set<factorOutput> factorSet =new HashSet<>(); 
    //create factors
    for (String variable :ancestorSet) {

        //create factor
        HashMap<String,String[]> variableMap = new HashMap<String,String[]>(variableOutcomes);
        variableMap.keySet().retainAll(Arrays.asList(CPTNodes.get(variable).getKeyOrder()));
        for (String given : givenVariables.keySet()) {
            //System.out.println(variable);
            variableMap.replace(given, new  String[]{givenVariables.get(given)});
        }
        String[] order = variableMap.keySet().toArray(new String[variableMap.size()]);
        List<String> factorKeys = Arrays.asList(CPTNodes.get(variable).getKeyOrder());
        factorKeys.removeAll(givenVariables.keySet());
        CPTNode factor= new CPTNode((String[] )factorKeys.toArray());
        permutation_iterator permutation = new permutation_iterator(variableMap, order );
        factor.getCPT().put(permutation.getkey(factor.getKeyOrder()), CPTNodes.get(variable).getCPT().get(permutation.getkey(order)));

        while (permutation.hasNext()) {
            permutation.next();
            factor.getCPT().put(permutation.getkey(factor.getKeyOrder()), CPTNodes.get(variable).getCPT().get(permutation.getkey(order)));

        }
        
        factorOutput newFactor=new factorOutput(factor);
        factorSet.add(newFactor);
            
    }


    ancestorSet.remove(Query);
    Set<String> hiddenVariableSet = ancestorSet;

    //variable elimination
    while (!hiddenVariableSet.isEmpty()) {
        String variable = choose(hiddenVariableSet,factorSet,algorithm);
        ArrayList<factorOutput> joinList = new ArrayList<factorOutput>();
        //get all the factors we want to join
        for (factorOutput factor : factorSet) {
            if (Arrays.asList( factor.getTable().getKeyOrder()).contains(variable)) {
                joinList.add(factor);
            }
        }
        
        joinList.sort(new Comparator<factorOutput>() {
            public int compare(factorOutput factor1,factorOutput factor2){
                CPTNode node1 =factor1.getTable();
                CPTNode node2 =factor2.getTable();
                if (node1.getCPT().size()!=node2.getCPT().size()){
                    return node1.getCPT().size() - node2.getCPT().size();
                }
                //else ASCII sum
                int node1Sum=0;
                for (String variable: node1.getKeyOrder()) {
                    for (int i = 0; i < variable.length(); i++) {
                        node1Sum+=variable.charAt(i);
                    }
                }
                int node2Sum=0;
                for (String variable: node2 .getKeyOrder()) {
                    for (int i = 0; i < variable.length(); i++) {
                        node2Sum+=variable.charAt(i);
                    }
                }
                return node1Sum-node2Sum;
            }
        });

        factorOutput newfactor = joinList.get(0);
        for (int i = 1; i < joinList.size(); i++) {
            newfactor.join(joinList.get(i),variableOutcomes);
        }
        newfactor.eliminate(variable,this.variableOutcomes);
        factorSet.removeAll(joinList);
        if (newfactor.getTable().getCPT().size()!=1) {
            factorSet.add(newfactor);
        }
       
    }
    
    
    return null;
}

private String choose(Set<String> hiddenVariableSet, Set<factorOutput> factorSet, int algorithm) {
    switch (algorithm) {
        case 2:
            return Collections.min(hiddenVariableSet);
            
        case 3:
            
        default:
        return Collections.min(hiddenVariableSet);
    }
}

private funcOutput naiveCalculatejointProbability(HashMap<String, String> givenVariables,String Query,String QueryOutcome) {
    //System.out.println("calculate joint probability");
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
    //start all the data we need
    HashMap<String,String> variables=new HashMap<String,String>(givenVariables);
    variables.put(Query, QueryOutcome);
    int additionOperations=0;
    int multOperations=0;
    funcOutput queryOutput= new funcOutput(0);


    HashMap<String,String[]> variableMap = new HashMap<String,String[]>(variableOutcomes);
    for (String variable : variables.keySet()) {
        //System.out.println(variable);
        variableMap.replace(variable, new  String[]{variables.get(variable)});
    }
    String[] order =variableMap.keySet().toArray(new String[variableMap.size()]);
    
    
    permutation_iterator permutation = new permutation_iterator(variableMap, order );
    String variable=permutation.getVariables()[0];
    String key=permutation.getkey(CPTNodes.get(permutation.getVariables()[0]).getKeyOrder());
    float probability=CPTNodes.get(variable).getCPT().get(key);
    for (int i = 1; i < permutation.getVariables().length; i++) {
        variable=permutation.getVariables()[i];
        
        key=permutation.getkey(CPTNodes.get(variable).getKeyOrder());
        //System.out.println(key);
        probability*=CPTNodes.get(variable).getCPT().get(key);
        multOperations++;
    }
    //System.out.println();
    //System.out.println(permutation.getPermutation());
     while (permutation.hasNext()) {
        
        permutation.next();
        variable=permutation.getVariables()[0];
        key=permutation.getkey(CPTNodes.get(permutation.getVariables()[0]).getKeyOrder());
        float permutationProbability=CPTNodes.get(variable).getCPT().get(key);
        for (int i = 1; i < permutation.getVariables().length; i++) {
            variable=permutation.getVariables()[i];
            key=permutation.getkey(CPTNodes.get(variable).getKeyOrder());
            //System.out.print(key+",");
            permutationProbability*=CPTNodes.get(variable).getCPT().get(key);
            multOperations++;
        }
        //System.out.println();
        //System.out.println(permutation.getPermutation());
        probability+=permutationProbability;
        additionOperations++;
    }

    
    return queryOutput.updateOutput(probability,multOperations,additionOperations);
}

}

