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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
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
        for (String outcome : variableOutcomes.get(Query)) {
            if(!outcome.equals(QueryOutcome)){
            funcOutput outcomeProbability = naiveCalculatejointProbability( givenVariables,Query,outcome);
            totalProbabilitySum.add(outcomeProbability);
            
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
    Set<String> ancestorSet= new HashSet<String>();
    //run BFS to discover all nodes 
    Queue<String> queue = new ArrayDeque<String>(givenVariables.keySet());
    queue.add(Query);
    String currentVariable;
    while (!queue.isEmpty()) {
        currentVariable = queue.remove();
        ancestorSet.add(currentVariable);
        queue.addAll(Arrays.asList(CPTNodes.get(currentVariable).getParents()));
        queue.removeAll(ancestorSet);
    }

  
    ancestorSet.removeAll(givenVariables.keySet());


    Set<factorOutput> factorSet = new HashSet<>(); 
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
        List<String> factorKeys = new ArrayList<String>( Arrays.asList( CPTNodes.get(variable).getKeyOrder()));
        factorKeys.removeAll(givenVariables.keySet());

        CPTNode factor= new CPTNode(factorKeys.stream().toArray(String[]::new));
        permutation_iterator permutation = new permutation_iterator(variableMap, order );
        //tricky bit of code to write concisely,
        //puts the variable into the factor but without the unnecessary variables(those that are given)
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
    factorOutput newfactor = null;
    
    //variable elimination
    //basic steps are:
    //choose a variable(either by the method specified on the algorithm)
    //join all of the variables containing it into one factor
    //cruelly eliminate that variable from that factor
    
    while (!hiddenVariableSet.isEmpty()) {
        //System.out.println(hiddenVariableSet);
        String variable = choose(hiddenVariableSet,factorSet,algorithm);
        hiddenVariableSet.remove(variable);
        
        ArrayList<factorOutput> joinList = new ArrayList<factorOutput>();
        //get all the factors we want to join
        for (factorOutput factor : factorSet) {
            if (Arrays.asList( factor.getTable().getKeyOrder()).contains(variable)) {
                joinList.add(factor);
            }
        }
        factorSet.removeAll(joinList);
        //just sorting by the order we want to join
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
        
        //important bits are here!! 
        //after all our hard work eliminating and joining is easy(even if you look inside those functions)
        newfactor = joinList.get(0);
        //System.out.println(joinList);
        for (int i = 1; i < joinList.size(); i++) {
            newfactor.join(joinList.get(i),variableOutcomes);
        }
        
        newfactor.eliminate(variable,this.variableOutcomes);
        
        if (newfactor.getTable().getCPT().size()!=1) {
            factorSet.add(newfactor);
        }
        
       
    }
    // if we did our job right we should have only one factor in the factor set
    factorOutput finalFactor=null;
    for (factorOutput factor : factorSet) {
       finalFactor = factor;
    }

    //that's a weird thing to do but it keeps permutation iterator as the only one handling keys, which is helpful
    float nonNormalizedProbability=0;
    permutation_iterator itr = new permutation_iterator(variableOutcomes, new  String[]{Query} );
    //System.out.println(finalFactor.table.getKeyOrder()[0]);
    //System.out.println(Arrays.toString(finalFactor.table.getKeyOrder())+" "+Query);
    System.out.println(finalFactor.table.getCPT());
    String key = itr.getkey(finalFactor.table.getKeyOrder());
    
    float Probability = finalFactor.table.getCPT().get(key);
    if  (itr.get_outcome(Query).equals(QueryOutcome)){
        
        nonNormalizedProbability = Probability;
    }
    probabilityOutput.updateOutput(Probability,0,0);
    while (itr.hasNext()) {
        itr.next();
        key = itr.getkey(finalFactor.table.getKeyOrder());
        Probability = finalFactor.table.getCPT().get(key);
        if  (itr.get_outcome(Query).equals(QueryOutcome)){
            
            nonNormalizedProbability = Probability;
        }
        probabilityOutput.add(Probability);
    }
    probabilityOutput.updateOutput(nonNormalizedProbability/probabilityOutput.getOutput(), finalFactor.multOperations,finalFactor.addOperations );
    return probabilityOutput;
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
    //start all the data we need
    HashMap<String,String> variables=new HashMap<String,String>(givenVariables);
    variables.put(Query, QueryOutcome);
    int additionOperations=0;
    int multOperations=0;
    funcOutput queryOutput= new funcOutput(0);


    HashMap<String,String[]> variableMap = new HashMap<String,String[]>(variableOutcomes);
    for (String variable : variables.keySet()) {
        
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

    
    queryOutput.updateOutput(probability,multOperations,additionOperations);
    return queryOutput;
}

}

