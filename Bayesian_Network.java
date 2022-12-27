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
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class Bayesian_Network {
    /**
     * contains an array of possible outcomes for each variable
     */
    private HashMap<String,String[]> variableOutcomes;
    
    /**
     *contains a node for each variable,the node contains the variable, its parents and the probability table given them
     */
    private HashMap<String,CPTNode> CPTNodes;
   
    /**
     * @param file xml file deescribing the network
     */
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
                    //get the outcomes and put them in a hash table to connect them to the variable
                    NodeList outcomesList =variable.getElementsByTagName("OUTCOME");
                    String[] outcomes= new String[outcomesList.getLength()];
                    for (int i = 0; i < outcomesList.getLength() ; i++) {
                        outcomes[i] = outcomesList.item(i).getTextContent();
                    }
                    varOutcomes.put(variableName,outcomes);
                }

            }


            //construct CPTNodes and connect each one to the variable it represents 
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
            //finally assign the data
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
    
    /**
     * this function uses a naive approach using the fact that P(X1=x1|X2=x2,....,Xn=xn )=P(X1=x1,X2=x2,....,Xn=xn )/P(X2=x2,....,Xn=xn )=
     *P(X1=x1,X2=x2,....,Xn=xn )/sum_{an outcome of X1}(P(X1=a,X2=x2,....,Xn=xn )) 
     * @param givenVariables a map that relates the given variables to their given outcomes
     * @param Query which variable we want to calculate the probability to given the other variables
     * @param QueryOutcome what outcome we want to caluculate the probability to
     * @return an output object containing the probability to and how many addition and multiplacation opertions we have done
     */
    public funcOutput naiveQuery(HashMap<String,String> givenVariables,String Query,String QueryOutcome){
        //check if we can just get the answer straight from the CPT
        if (givenVariables.keySet().equals( new HashSet<>(Arrays.asList(CPTNodes.get(Query).getParents())))) {
            String key=QueryOutcome;
            for (int i = 0; i < CPTNodes.get(Query).getParents().length; i++) {
                key+=givenVariables.get(CPTNodes.get(Query).getParents()[i]);
            }
            return new funcOutput(CPTNodes.get(Query).getCPT().get(key));
        }
        
        //calculate the joint probability of the given variables and query
        funcOutput totalProbabilitySum = naiveCalculatejointProbability( givenVariables,Query,QueryOutcome);
        float QueryProbability = totalProbabilitySum.getOutput();

        //sum the rest to find out the normalization factor
        //reminder: P(X1=x1|X2=x2,....,Xn=xn )=P(X1=x1,X2=x2,....,Xn=xn )/P(X2=x2,....,Xn=xn )=
        //P(X1=x1,X2=x2,....,Xn=xn )/sum_{a outcome of X1}(P(X1=a,X2=x2,....,Xn=xn )) 

        for (String outcome : variableOutcomes.get(Query)) {
            if(!outcome.equals(QueryOutcome)){
            funcOutput outcomeProbability = naiveCalculatejointProbability( givenVariables,Query,outcome);
            totalProbabilitySum.add(outcomeProbability);
            
        }
        }
        //normalize the the query probability by the sum of all the joint probabilities
        totalProbabilitySum.updateOutput(QueryProbability/totalProbabilitySum.getOutput(), 0, 0);

        return totalProbabilitySum;

}
    
    
/**
 * this function uses variable elimination to calulate a given probability using givan heuristic
 * https://en.wikipedia.org/wiki/Variable_elimination
 * @param givenVariables a map that relates the given variables to their given outcomes
 * @param Query which variable we want to calculate the probability to given the other variables
 * @param QueryOutcome what outcome we want to caluculate the probability to
 * @param heuristic an int representing the the heuristic we want to use in order to choose the variable
 * @return an output object containing the probability to and how many addition and multiplacation opertions we have done
 */
public funcOutput VECalculateProbabilty(HashMap<String, String> givenVariables, String Query,String  QueryOutcome,int heuristic) {
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
    //run BFS to discover all nodes that are ancenstors to our wanted variables
    Queue<String> queue = new ArrayDeque<String>(givenVariables.keySet());
    queue.add(Query);
    String currentVariable;
    while (!queue.isEmpty()) {
        currentVariable = queue.remove();
        ancestorSet.add(currentVariable);
        queue.addAll(Arrays.asList(CPTNodes.get(currentVariable).getParents()));
        queue.removeAll(ancestorSet);
    }

  


    Set<factorOutput> factorSet = new HashSet<>(); 
    //create factors
    // for each variable that matters create a a factor containing a table that contains only the releavent entries,
    // those that are possible given the variables that are given
    for (String variable :ancestorSet) {
        CPTNode node = CPTNodes.get(variable);
        //create factor
        List<String> factorKeys = new ArrayList<String>( Arrays.asList( CPTNodes.get(variable).getKeyOrder()));
        factorKeys.removeAll(givenVariables.keySet());
        CPTNode factor = new CPTNode(factorKeys.stream().toArray(String[]::new));

        HashMap<String,String[]> variableMap = new HashMap<String,String[]>(variableOutcomes);
        variableMap.keySet().retainAll(Arrays.asList(CPTNodes.get(variable).getKeyOrder()));
        for (String given : givenVariables.keySet()) {
            variableMap.replace(given, new  String[]{givenVariables.get(given)});
        }

        String[] order = variableMap.keySet().toArray(new String[variableMap.size()]);
        
        permutation_iterator permutation = new permutation_iterator(variableMap, order );
        //tricky bit of code to write concisely,
        //puts the variable into the factor but without the unnecessary variables(those that are given)
        factor.getCPT().put(permutation.getkey(factor.getKeyOrder()), node.getCPT().get(permutation.getkey(node.getKeyOrder())));

        while (permutation.hasNext()) {
            permutation.next();
            
            factor.getCPT().put(permutation.getkey(factor.getKeyOrder()), node.getCPT().get(permutation.getkey(node.getKeyOrder())));

        }
        
        factorOutput newFactor=new factorOutput(factor);
        if(newFactor.getTable().getCPT().size()!=1){
        factorSet.add(newFactor);
        }
            
    }

    ancestorSet.remove(Query);
    ancestorSet.removeAll(givenVariables.keySet());
    Set<String> hiddenVariableSet = ancestorSet;
    factorOutput newfactor = null;
    
    //variable elimination
    //basic steps are:
    //choose a variable(either by the method specified on the algorithm)
    //join all of the variables containing it into one factor
    //cruelly eliminate that variable from that factor
    while (!hiddenVariableSet.isEmpty()) {
        // choose a variable from our hidden variables to eliminate and remove it from the variables we want to eliminate
        String variable = choose(hiddenVariableSet,factorSet, heuristic);
        hiddenVariableSet.remove(variable);
        
        ArrayList<factorOutput> joinList = new ArrayList<factorOutput>();
        //get all the factors we want to join and remove them from our list
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
                //by cpt size
                if (node1.getCPT().size()!=node2.getCPT().size()){
                    return node1.getCPT().size() - node2.getCPT().size();
                }
                //else ASCII sum of variables
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
        for (int i = 1; i < joinList.size(); i++) {
            newfactor.join(joinList.get(i),variableOutcomes);
        }
        
        newfactor.eliminate(variable,this.variableOutcomes);
        
        if (newfactor.getTable().getCPT().size()!=1) {
            factorSet.add(newfactor);
        }
        
       
    }
    // now we are left with one variable(our query) left on all factors and we need only to join and normalize
    factorOutput[] finalJoinList = factorSet.toArray(new factorOutput[factorSet.size()]);
    factorOutput finalFactor = finalJoinList[0];
    for (int i = 1; i < finalJoinList.length; i++) {
        finalFactor.join(finalJoinList[i], variableOutcomes);
    } 

    //that's a weird thing to do but it keeps permutation iterator as the only one handling keys, which is helpful
    float nonNormalizedProbability=0;
    permutation_iterator itr = new permutation_iterator(variableOutcomes, new  String[]{Query} );

    //we now sum all the probabilities in our factor for the normaliztion component, and keep the one with the outcome we want 
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
    //finally normalize the probabilty and update the multiplication and addition operations\
    //(this will add to the multiplication and additon we have already done on the output) 
    probabilityOutput.updateOutput(nonNormalizedProbability/probabilityOutput.getOutput(), finalFactor.multOperations,finalFactor.addOperations );
    return probabilityOutput;
}



/**
 * @param hiddenVariableSet the variable set to choose from
 * @param factorSet the factors we want to eliminate
 * @param heuristic an integer representing the heuristic we want to use
 * @return the variable chosen by the heuristic given the factors exisiting and the variables we want to choose from
 */
private String choose(Set<String> hiddenVariableSet, Set<factorOutput> factorSet,int heuristic) {
    
    switch (heuristic) {
        case 2:
        //order by lexicographic order on the variables,each time choose the smallest by that order
            return Collections.min(hiddenVariableSet);
            
        case 3:
            //order by "minimum degree" ie create the smallest factor

            return Collections.min(hiddenVariableSet,new Comparator<String>() {
                private Set<factorOutput> NodeSet = factorSet;
                @Override
                public int compare(String variable1, String variable2) {
                    Set<String> joinedVariables1 = new HashSet<String>();
                    Set<String> joinedVariables2 = new HashSet<String>();
                    //get all the variables in the final factors  
                    for (factorOutput factor : NodeSet) {
                        if (Arrays.asList( factor.getTable().getKeyOrder()).contains(variable1)) {
                            joinedVariables1.addAll(Arrays.asList(factor.getTable().getKeyOrder()));
                        }
                        if (Arrays.asList( factor.getTable().getKeyOrder()).contains(variable2)) {
                            joinedVariables2.addAll(Arrays.asList(factor.getTable().getKeyOrder()));
                        }

                    }
                    int joinedFactorSize1 = 1;
                    int joinedFactorSize2 = 1;
                    //calculate the size of those factors
                    for (String variable : joinedVariables1) {
                        joinedFactorSize1 *= variableOutcomes.get(variable).length ;
                    }
                    for (String variable : joinedVariables2) {
                        joinedFactorSize2 *= variableOutcomes.get(variable).length ;
                    }
                    
                    return joinedFactorSize1- joinedFactorSize2;
                }
            });
        default:
        return Collections.min(hiddenVariableSet);
    }
}

/**
 * @param givenVariables given variables and their outcomes
 * @param Query query variable
 * @param QueryOutcome outcome query variable
 * @return the probability of all of the variables and the query happening with the given outcomes,the joint probability
 * uses the baeysian network property that P(X1,X2,,,,Xn) = multiplication_{i->n}(P(Xi|parents(Xi))
 * given that those are all the variables
 * and using the law of total probability https://en.wikipedia.org/wiki/Law_of_total_probability 
 */
private funcOutput naiveCalculatejointProbability(HashMap<String, String> givenVariables,String Query,String QueryOutcome) {
    //start all the data we need
    HashMap<String,String> variables=new HashMap<String,String>(givenVariables);
    variables.put(Query, QueryOutcome);

    //create a map that represents whats the possible outcomes for which variable
    HashMap<String,String[]> variableMap = new HashMap<String,String[]>(variableOutcomes);
    for (String variable : variables.keySet()) {
        
        variableMap.replace(variable, new  String[]{variables.get(variable)});
    }
    String[] order =variableMap.keySet().toArray(new String[variableMap.size()]);
    
    //calculate probability by the law of total probability and by the baeysian network rule we mentioned above
    //first go through the first permutation(to slightly reduce our addition and multiplication operations,needed for assignment)
    permutation_iterator permutation = new permutation_iterator(variableMap, order );
    String variable=permutation.getVariables()[0];
    String key=permutation.getkey(CPTNodes.get(permutation.getVariables()[0]).getKeyOrder());
    funcOutput probability = new funcOutput(CPTNodes.get(variable).getCPT().get(key));


    for (int i = 1; i < permutation.getVariables().length; i++) {
        variable=permutation.getVariables()[i];
        
        key=permutation.getkey(CPTNodes.get(variable).getKeyOrder());
        
        probability.multiply(CPTNodes.get(variable).getCPT().get(key));
        
    }
    
     while (permutation.hasNext()) {
        
        permutation.next();
        variable=permutation.getVariables()[0];
        key=permutation.getkey(CPTNodes.get(permutation.getVariables()[0]).getKeyOrder());
        funcOutput permutationProbability = new funcOutput(CPTNodes.get(variable).getCPT().get(key));

        for (int i = 1; i < permutation.getVariables().length; i++) {
            variable=permutation.getVariables()[i];
            key=permutation.getkey(CPTNodes.get(variable).getKeyOrder());
            
            permutationProbability.multiply(CPTNodes.get(variable).getCPT().get(key));
            
        }
        probability.add(permutationProbability);
    }
    
    return probability;
}

}

