import java.util.HashMap;
import java.util.Iterator;

public class permutation_iterator implements Iterator<Void> {
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
    public String getPermutation(){
        String permutation="";
            for (int j = 0; j < variables.length; j++) {
                permutation = permutation.concat(this.get_outcome(variables[j]));
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
    public Void next() {
            int i=0;
            for ( i = 0; (i < variables.length)&&indexes.get(variables[i])==(variableOutcomes.get(variables[i]).length-1); i++) {
                indexes.put(variables[i],0);
            if(i==variables.length){
                return null;
            }
        indexes.put(variables[i],indexes.get(variables[i])+1);
    }
        return null;
    }
 public String get_outcome(String variable){
    
    return variableOutcomes.get(variable)[indexes.get(variable)];
 }   
}
