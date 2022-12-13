public class funcOutput {
    private int multOperations;
    
    
    private int additionOperations;
    
    private float output;
    
    public funcOutput(float output){
        this.multOperations=0;
        this.additionOperations=0;
        this.output=0;
        
    }
    public funcOutput updateOutput(float output,int multOperations,int additionOperations){
        this.additionOperations+= additionOperations;
        this.multOperations+=multOperations;
        this.output=output;
        return this;
        
    }
    public funcOutput updaOutput(funcOutput updated){
        this.additionOperations+=updated.additionOperations;
        this.multOperations += updated.multOperations;
        this.output=updated.output;
        return this;

    }

    public float getOutput() {
        return output;
    }

    public int getMultOperations() {
        return multOperations;
    }
    public int getAdditionOperations() {
        return additionOperations;
    }
    public void add(funcOutput addedOutput) {
        this.additionOperations+= addedOutput.additionOperations+1;
        this.multOperations+= addedOutput.multOperations;
        this.output+= addedOutput.output;
    }
    
}
