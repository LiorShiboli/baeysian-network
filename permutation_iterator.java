import java.util.HashMap;
import java.util.Iterator;

public class permutation_iterator implements Iterator<String[]> {
    private HashMap<String,String[]> variableOutcomes;
    private String[] variables;
    private HashMap<String,Integer> indexes;
    public permutation_iterator(HashMap<String,String[]> variableOutcomes,String[] variables ){
        
        this.variableOutcomes=variableOutcomes;
        this.variables=variables;
        for (String variable : variables) {
            indexes.put(variable, 0);
        }
        
    }
    public String[] getPermutation(){
        String[] permutation=new String[variables.length];
        for (int j = 0; j < variables.length; j++) {
            permutation[j]=variableOutcomes.get(variables[j])[indexes.get(variables[j])];
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
    public String[] next() {
        if (hasNext()) {
            int i=0;
            for ( i = 0; (i < variables.length)&&indexes.get(variables[i])==(variableOutcomes.get(variables[i]).length-1); i++) {
                indexes.put(variables[i],0);
            }
        indexes.put(variables[i],indexes.get(variables[i])+1);
        String[] permutation=new String[this.variables.length];
        for (int j = 0; j < variables.length; j++) {
            permutation[j]=variableOutcomes.get(variables[j])[indexes.get(variables[j])];
        }
        return permutation;
        }
        return null;
    }
 public String get_outcomes(String variable){
    
    return variableOutcomes.get(variable)[indexes.get(variable)];
 }   
}
