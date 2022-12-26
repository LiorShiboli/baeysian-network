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
    public void join(factorOutput toJoinFactor,HashMap<String, String[]> variableOutcomes) {
        //System.out.println("join");
        multOperations+=toJoinFactor.multOperations;
        addOperations+=toJoinFactor.addOperations;
        HashMap<String, Float> factor1 = this.getTable().getCPT();
        HashMap<String, Float> factor2 = toJoinFactor.getTable().getCPT();
        

        String[] keys1= this.getTable().getKeyOrder();
        String[] keys2 = toJoinFactor.getTable().getKeyOrder();
        
        //System.out.println(Arrays.toString(keys1));
        //System.out.println(factor1);
        //System.out.println(Arrays.toString(keys2));
        //System.out.println(factor2);
        
        Set<String> keySet = Arrays.stream(keys1).collect(Collectors.toSet());
        keySet.addAll(Arrays.stream(keys2).collect(Collectors.toSet()));
        String[] keyorder = keySet.toArray(new String[keySet.size()]);
        CPTNode newTable = new CPTNode(keyorder);
        permutation_iterator itr = new permutation_iterator(variableOutcomes,keyorder);
        newTable.getCPT().put(itr.getkey(keyorder),factor1.get(itr.getkey(keys1))*factor2.get(itr.getkey(keys2)) );
        multOperations++;

        while (itr.hasNext()) {
            itr.next();
            newTable.getCPT().put(itr.getkey(keyorder),factor1.get(itr.getkey(keys1))*factor2.get(itr.getkey(keys2)) );
            multOperations++;
           
        }
        //System.out.println(Arrays.toString(newTable.getKeyOrder()));
        //System.out.println(newTable.getCPT());
        this.table=newTable;
    }


    public void eliminate(String variable, HashMap<String, String[]> variableOutcomes) {
        //System.out.println("eliminate "+variable);
        CPTNode factor = this.table;
        String[] newKeys = new String[factor.getKeyOrder().length - 1];
        //System.out.println(factor.getCPT()+" "+ Arrays.toString(factor.getKeyOrder()));
        int j=0;
        for (int i = 0; i < factor.getKeyOrder().length ; i++) {
            
            if (!variable.equals(factor.getKeyOrder()[i])){
            newKeys[j] = factor.getKeyOrder()[i];
            j++;
            }
        }
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
    //System.out.println(this.table.getCPT());
    }


    public CPTNode getTable() {
        return this.table;
    }
    public String toString(){
        return Arrays.toString(this.table.getKeyOrder());
    }
}
