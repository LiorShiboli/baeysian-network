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
public class CPTNode {
    //the variable to which the cpt belongs to and the parents(variables he depends on)
    private String variable;
    private String[] parents;
    //conditional probability table based variable given its parents
    //keys are of the shape "<variable outcome>,<parents[0] outcome>,<parents[1] outcome>,..." and value contains the probability
    private HashMap<String,Float> CPT;

    public CPTNode(HashMap<String,String[]> variables, Element definition){


        permutation_iterator itr= new permutation_iterator(variables, parents);

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
