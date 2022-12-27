import java.util.Arrays;
import java.util.HashMap;

import java.util.Set;
import java.util.stream.Collectors;

public class factorOutput {
    CPTNode table;
    int addOperations;
    int multOperations;
    public factorOutput(CPTNode factor) {
        this.addOperations=0;
        this.multOperations=0;
        this.table=factor;
    }
    /**
     * joins another factor to that one, making one table
     * @param toJoinFactor factor to join the factor with
     * @param variableOutcomes the outcome options for each variable
     */
    public void join(factorOutput toJoinFactor,HashMap<String, String[]> variableOutcomes) {
        //add operations
        multOperations += toJoinFactor.multOperations;
        addOperations += toJoinFactor.addOperations;

        HashMap<String, Float> factor1 = this.getTable().getCPT();
        HashMap<String, Float> factor2 = toJoinFactor.getTable().getCPT();
        

        String[] keys1= this.getTable().getKeyOrder();
        String[] keys2 = toJoinFactor.getTable().getKeyOrder();

        Set<String> keySet = Arrays.stream(keys1).collect(Collectors.toSet());
        keySet.addAll(Arrays.stream(keys2).collect(Collectors.toSet()));
        String[] keyorder = keySet.toArray(new String[keySet.size()]);
        CPTNode newTable = new CPTNode(keyorder);

        permutation_iterator itr = new permutation_iterator(variableOutcomes,keyorder);
        // for each entry in the new table, multiply the entries from the entries in the 2 factors 
        //containing the same outcomes as the new entry
        newTable.getCPT().put(itr.getkey(keyorder),factor1.get(itr.getkey(keys1))*factor2.get(itr.getkey(keys2)) );
        multOperations++;

        while (itr.hasNext()) {
            itr.next();
            newTable.getCPT().put(itr.getkey(keyorder),factor1.get(itr.getkey(keys1))*factor2.get(itr.getkey(keys2)) );
            multOperations++;
           
        }
        
        this.table=newTable;
    }


    /**
     * sum all variables with the same outcomes beside one variable's to one entry,thus eliminating that variable
     * @param variable variable to eliminate 
     * @param variableOutcomes the outcome options for each variable
     */
    public void eliminate(String variable, HashMap<String, String[]> variableOutcomes) {
        
        CPTNode factor = this.table;
        String[] newKeys = new String[factor.getKeyOrder().length - 1];
        
        //construct a new keyorder for the new factor
        for (int i=0 , j = 0; i < factor.getKeyOrder().length ; i++) {
            
            if (!variable.equals(factor.getKeyOrder()[i])){
            newKeys[j] = factor.getKeyOrder()[i];
            j++;
            }
        }
        //for each one of the old entries sum to a new entry that contains all the same outcomes 
        //besides the variable we want to eliminate which is non existent
        CPTNode newTable = new CPTNode(newKeys);
        permutation_iterator itr = new permutation_iterator(variableOutcomes, factor.getKeyOrder());
        newTable.getCPT().put(itr.getkey(newKeys), factor.getCPT().get(itr.getkey(factor.getKeyOrder())));
        while (itr.hasNext()) {
            itr.next();
            String newKey = itr.getkey(newKeys);
            String oldKey = itr.getkey(factor.getKeyOrder());

            if(newTable.getCPT().containsKey(newKey)){
                newTable.getCPT().replace( newKey, newTable.getCPT().get(newKey) + factor.getCPT().get(oldKey));
                addOperations++;
               
            }
            
            else{
                newTable.getCPT().put(newKey, factor.getCPT().get(oldKey));
            }
            
        }
     this.table=newTable;
    }


    public CPTNode getTable() {
        return this.table;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     * not used for code but is really nice for testing
     */
    public String toString(){
        return Arrays.toString(this.table.getKeyOrder());
    }
}
