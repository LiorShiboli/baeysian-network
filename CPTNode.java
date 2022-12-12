import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
public class CPTNode {
    //the variable to which the cpt belongs to and the parents(variables he depends on)
    private String[] keyOrder;
    //conditional probability table based variable given its parents
    //keys are of the shape "<variable outcome>,<parents[0] outcome>,<parents[1] outcome>,..." and value contains the probability
    private HashMap<String,Float> CPT;

    public CPTNode(HashMap<String,String[]> variables, Element definition){
        //get variable
        String variableName= (String) definition.getElementsByTagName("FOR").item(0).getTextContent();
        //get the parents

        NodeList parentsList =definition.getElementsByTagName("GIVEN");
        String[] parents= new String[parentsList.getLength()];
        for (int i = 0; i < parentsList.getLength() ; i++) {
            parents[i] = parentsList.item(i).getTextContent();
        }
       
        Collections.reverse(Arrays.asList(parents));
       
        //
        String[] order=new String[parents.length+1];
        order[0]=variableName;
        for (int i = 0; i < parents.length; i++) {
            order[i+1]=parents[i];
        }
        this.keyOrder=order;
        HashMap<String, String[]> subMap = new HashMap<String, String[]>(variables);
        subMap.keySet().retainAll(Arrays.asList(parents));
        this.CPT= new HashMap<String,Float>();
        permutation_iterator itr= new permutation_iterator(variables, order);
        String[] table=definition.getElementsByTagName("TABLE").item(0).getTextContent().split(" ");
        for (int i = 0; i<table.length ; i++, itr.next()) {
            
            this.CPT.put(itr.getPermutation(),Float.valueOf(table[i]));
        }

    }






    public HashMap<String, Float> getCPT() {
        return CPT;
    }

    public String getVariable() {
        return keyOrder[0];
    }

    public String[] getParents() {
        return Arrays.copyOfRange(this.keyOrder, 1, this.keyOrder.length);
    }

    public void setCPT(HashMap<String, Float> CPT) {
        this.CPT = CPT;
    }

    @Override
    public String toString() {
        return this.keyOrder[0];
    }






    public String[] getKeyOrder() {
        return this.keyOrder;
    }
}
