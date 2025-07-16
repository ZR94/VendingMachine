package mySparkApp.administration;

public class Revenue {
    
    private int idMachine;
    private double amount;

    public Revenue() {
        
    }

    public Revenue(int idMachine, double amount) {
        
        this.idMachine = idMachine;
        this.amount = amount;
    }

    public int getIdMachine() {
        return idMachine;
    }

    public void setIdMachine(int idMachine) {
        this.idMachine = idMachine;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}
