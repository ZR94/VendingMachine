package models;

public class Revenue {
    
    private int machineId;
    private double amount;
    
    public Revenue() {}

    public Revenue(int machineId, double amount) {
        
    	
        this.machineId = machineId;
        this.amount = amount;
    }

    //GETTER
	

	public int getMachineId() {
		return machineId;
	}

	public double getAmount() {
		return amount;
	}

	//SETTER	
	
	
	public void setMachineId(int machineId) {
		this.machineId = machineId;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	
    
}