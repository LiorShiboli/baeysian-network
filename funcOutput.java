public class funcOutput {
    //the point of this class is to be able to add and multiply while keeping track of the operations,while not actually needing 
    private float output;

    private int multOperations;
    private int additionOperations;
    
    
    /**
     * construct output float
     * @param output 
     */
    public funcOutput(float output){
        this.multOperations=0;
        this.additionOperations=0;
        this.output=output;
        
    }
    public void updateOutput(float output,int multOperations,int additionOperations){
        this.additionOperations+= additionOperations;
        this.multOperations+=multOperations;
        this.output=output;
    }

    public void updateOutput(funcOutput updated){
        this.additionOperations+=updated.additionOperations;
        this.multOperations += updated.multOperations;
        this.output=updated.output;
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

    public void add(float output) {
        this.additionOperations++;
        this.output+= output;
    }

    public void multiply(Float factor) {
        this.multOperations++;
        this.output *= factor;
    }
    
}
