import java.util.HashMap;
import java.util.Iterator;
//this is t
public class permutation_iterator implements Iterator<Boolean> {
    private HashMap<String,String[]> variableOutcomes;
    private String[] variables;
    private HashMap<String,Integer> indexes;
    public permutation_iterator(HashMap<String,String[]> variableOutcomes,String[] variables ){
        this.variableOutcomes=variableOutcomes;
        this.variables=variables;
        indexes= new HashMap<String,Integer>();
        
        for (String variable : variables) {
            indexes.put(variable, 0);
        }
        
    }
    public String[] getVariables() {
        return variables;
    }

    public String getPermutation(){
        String permutation="";
            for (int j = 0; j < variables.length; j++) {
                permutation = permutation.concat(this.get_outcome(variables[j])+",");
            }
        return permutation;

    }
    @Override
    public boolean hasNext() {
        for (int i = 0; i < variables.length; i++) {
            if (indexes.get(variables[i])<(variableOutcomes.get(variables[i]).length-1)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean next() {
            int i=0;
            for ( i = 0; (i < variables.length)&&indexes.get(variables[i])==(variableOutcomes.get(variables[i]).length-1); i++) {
                indexes.put(variables[i],0);
            }
        if(i==variables.length){
            return false;
        }
        indexes.put(variables[i],indexes.get(variables[i])+1);
        return true;
    }
 public String get_outcome(String variable){  
    return variableOutcomes.get(variable)[indexes.get(variable)];
 }
public String getkey(String[] order) {
    String key="";
    for (int j = 0; j < order.length; j++) {
        key = key.concat(this.get_outcome(order[j])+",");
    }
return key;
}   
}
