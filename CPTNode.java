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
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Set;
public class CPTNode {
    //the variable to which the cpt belongs to and the parents(variables he depends on)
    private String variable;
    private String[] parents;
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
        //assign parents and variable
        Collections.reverse(Arrays.asList(parents));
        this.parents=parents;
        this.variable=variableName;
        //
        String[] order=new String[parents.length+1];
        order[0]=this.variable;
        for (int i = 0; i < parents.length; i++) {
            order[i+1]=parents[i];
        }
        HashMap<String, String[]> subMap = new HashMap<String, String[]>(variables);
        subMap.keySet().retainAll(Arrays.asList(parents));
        this.CPT= new HashMap<String,Float>();
        permutation_iterator itr= new permutation_iterator(variables, order);
        String[] table=definition.getElementsByTagName("TABLE").item(0).getTextContent().split(" ");
        String[] permutation= itr.getPermutation();
        for (int i = 0; i<table.length ; i++, permutation=itr.next()) {
            String key="";
            for (int j = 0; j < permutation.length; j++) {
                key=key.concat(permutation[j]);
            }
            this.CPT.put(key,Float.valueOf(table[i]));
        }

    }






    public HashMap<String, Float> getCPT() {
        return CPT;
    }

    public String getVariable() {
        return variable;
    }

    public String[] getParents() {
        return parents;
    }

    public void setCPT(HashMap<String, Float> CPT) {
        this.CPT = CPT;
    }

    public void setParents(String[] parents) {
        this.parents = parents;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }
    @Override
    public String toString() {
        return this.variable;
    }
}
