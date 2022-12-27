import java.util.HashMap;
import java.util.Iterator;
//this is one of the more important classes
//this class gets variables to iterate over their permutation(ie and iterates over them in order of the variables given 
//is also able to get keys to the given permutation in a specfic CPT given the key order!
//this is really important as it lets us communicate between CPTs
public class permutation_iterator implements Iterator<Boolean> {
    private HashMap<String,String[]> variableOutcomes;
    private String[] variables;
    private HashMap<String,Integer> indexes;

    public permutation_iterator(HashMap<String,String[]> variableOutcomes,String[] variables ){
        this.variableOutcomes=variableOutcomes;
        this.variables=variables;
        indexes= new HashMap<String,Integer>();
        //start the permutation at the first outcome of each variable
        for (String variable : variables) {
            indexes.put(variable, 0);
        }
        
    }
    public String[] getVariables() {
        return variables;
    }
    //like get key but by the order given in the start of the function
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
    //get to the next permutation, moves to the next outcome for the first variable,if completes a cycle do the same for the second
    @Override
    public Boolean next() {
            int i=0;
            for ( i = 0; (i < variables.length) && indexes.get(variables[i]) == (variableOutcomes.get(variables[i]).length-1); i++) {
                indexes.put(variables[i],0);
            }
        if(i == variables.length){
            return false;
        }
        indexes.put(variables[i],indexes.get(variables[i])+1);
        return true;
    }

    public String get_outcome(String variable){  
    return variableOutcomes.get(variable)[indexes.get(variable)];
    }
    //get key for permutation the key order (doesnt have to contain all the variables),
    //given a key order will get the key to the entry corresponding to the outcomes in the permutation
    public String getkey(String[] order) {
    String key="";
    for (int j = 0; j < order.length; j++) {
        key = key.concat(this.get_outcome(order[j])+",");
    }
return key;
}   
}
