import java.util.HashMap;
import java.util.Iterator;

public class permutation_iterator implements Iterator<String[]> {
    private HashMap<String,String[]> variableOutcomes;
    private String[] variables;
    private int[] indexes;
    public permutation_iterator(HashMap<String,String[]> variableOutcomes,String[] variables ){
        System.out.println("started iterator");
        this.variableOutcomes=variableOutcomes;
        this.variables=variables;
        indexes= new int[variables.length];
    }
    public String[] getPermutation(){
        String[] permutation=new String[variables.length];
        for (int j = 0; j < indexes.length; j++) {
            permutation[j]=variableOutcomes.get(variables[j])[indexes[j]];
        }
        return permutation;

    }
    @Override
    public boolean hasNext() {
        for (int i = 0; i < indexes.length; i++) {
            if (indexes[i]<(variableOutcomes.get(variables[i]).length-1)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String[] next() {
        if (hasNext()) {
            int i=0;
            for ( i = 0; (i < indexes.length)&&indexes[i]==(variableOutcomes.get(variables[i]).length-1); i++) {
                indexes[i]=0;
            }
        indexes[i]++;
        String[] permutation=new String[this.variables.length];
        for (int j = 0; j < indexes.length; j++) {
            permutation[j]=variableOutcomes.get(variables[j])[indexes[j]];
        }
        return permutation;
        }
        return null;
    }
    
}
