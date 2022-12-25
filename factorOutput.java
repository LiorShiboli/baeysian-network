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
        multOperations+=toJoinFactor.multOperations;
        addOperations+=toJoinFactor.multOperations;
        HashMap<String, Float> factor1 = this.getTable().getCPT();
        HashMap<String, Float> factor2 = toJoinFactor.getTable().getCPT();
        String[] keys1= this.getTable().getKeyOrder();
        String[] keys2 = toJoinFactor.getTable().getKeyOrder();
        Set<String> keySet = Arrays.stream(keys1).collect(Collectors.toSet());
        keySet.addAll(Arrays.stream(keys2).collect(Collectors.toSet()));
        String[] keyorder = (String[])keySet.toArray();
        CPTNode newTable = new CPTNode(keyorder);
        permutation_iterator itr = new permutation_iterator(variableOutcomes,keyorder);
        newTable.getCPT().put(itr.getkey(keyorder),factor1.get(itr.getkey(keys1))*factor2.get(itr.getkey(keys2)) );
        multOperations++;

        while (itr.hasNext()) {
            itr.next();
            newTable.getCPT().put(itr.getkey(keyorder),factor1.get(itr.getkey(keys1))*factor1.get(itr.getkey(keys1)) );
            multOperations++;
        }
        this.table=newTable;
    }
    public void eliminate(String variable, HashMap<String, String[]> variableOutcomes) {
        CPTNode factor = this.table;
        String[] newKeys = new String[factor.getKeyOrder().length - 1];

        int j=0;
        for (int i = 0; i < factor.getKeyOrder().length ; i++) {
            if (variable!=factor.getKeyOrder()[i]){
            newKeys[j] = factor.getKeyOrder()[i];
            i++;
            }
        }
        CPTNode newTable = new CPTNode(newKeys);
        permutation_iterator itr = new permutation_iterator(variableOutcomes, factor.getKeyOrder());
        newTable.getCPT().put(itr.getkey(newKeys), factor.getCPT().get(itr.getkey(factor.getKeyOrder())));
        while (itr.hasNext()) {
            itr.next();
            if(newTable.getCPT().get()){
            addOperations++;
            }
        }
        
    }


    public CPTNode getTable() {
        return this.table;
    }

}
