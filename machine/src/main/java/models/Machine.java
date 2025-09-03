package models;

public class Machine {
    private int machineId;
    private double totalCash;       // total_cash
    private double maxCash;         // max_cash
    private double currentCredit;   // current_credit
    private StatusMachine status;   // status (mappato all'enum)

    public Machine() {
        // Costruttore di default (eventualmente necessario se usi framework che richiedono un no-args constructor)
    }

    public Machine(int machineId, double totalCash, double maxCash, double currentCredit, StatusMachine status) {
        this.machineId = machineId;
        this.totalCash = totalCash;
        this.maxCash = maxCash;
        this.currentCredit = currentCredit;
        this.status = status;
    }

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public double getTotalCash() {
        return totalCash;
    }

    public void setTotalCash(double totalCash) {
        this.totalCash = totalCash;
    }

    public double getMaxCash() {
        return maxCash;
    }

    public void setMaxCash(double maxCash) {
        this.maxCash = maxCash;
    }

    public double getCurrentCredit() {
        return currentCredit;
    }

    public void setCurrentCredit(double currentCredit) {
        this.currentCredit = currentCredit;
    }

    public StatusMachine getStatus() {
        return status;
    }

    public void setStatus(StatusMachine status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Machine{" +
               "machineId=" + machineId +
               ", totalCash=" + totalCash +
               ", maxCash=" + maxCash +
               ", currentCredit=" + currentCredit +
               ", status=" + status +
               '}';
    }
}

