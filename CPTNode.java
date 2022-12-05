import javax.lang.model.element.Element;
import java.util.HashMap;

public class CPTNode {
    //the variable to which the cpt belongs to and the parents(variables he depends on)
    private String variable;
    private String[] parents;
    //conditional probability table based variable given its parents
    //keys are of the shape "<variable outcome>,<parents[0] outcome>,<parents[1] outcome>,..." and value contains the probability
    private HashMap<String,Float> CPT;

    public CPTNode(HashMap<String,String[]> variables, Element node){

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
