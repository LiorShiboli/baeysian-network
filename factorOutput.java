import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
        CPTNode factor1 = this.getTable();
        CPTNode factor2 = toJoinFactor.getTable();
        Set<String> keySet = Arrays.stream(factor1.getKeyOrder()).collect(Collectors.toSet());
        keySet.addAll(Arrays.stream(factor2.getKeyOrder()).collect(Collectors.toSet()));
        String[] keyorder = (String[])keySet.toArray();
        CPTNode newTable= new CPTNode(keyorder);
        
        
    }
    public void eliminate(String variable, HashMap<String, String[]> variableOutcomes) {

    }


    public CPTNode getTable() {
        return this.table;
    }

}
