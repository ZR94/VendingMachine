package mySparkApp.machine;

import java.io.Serializable;

public class ControllerCoffeeMachine implements Serializable {
	
	private int idMachine;
    private int idInstitute;
    private Status status;
	private double totalCash;
    private double maxCash;
    private double currentCredit;

	public ControllerCoffeeMachine(){}

	public ControllerCoffeeMachine(int idInstitute) {
		this.idInstitute = idInstitute;
	}

	public ControllerCoffeeMachine(int idMachine, int idInstitute, Status status) {
		this.idMachine = idMachine;
		this.idInstitute = idInstitute;
		this.status = status;
	}
    
	public ControllerCoffeeMachine(int idMachine, int idInstitute, Status status, double totalCash, double maxCash, double currentCredit) {
		this.idMachine = idMachine;
		this.idInstitute = idInstitute;
		this.status = status;
		this.totalCash = totalCash;
		this.maxCash = maxCash;
		this.currentCredit = currentCredit;
	}

	public int getIdMachine() {
		return idMachine;
	}

	public void setIdMachine(int idMachine) {
		this.idMachine = idMachine;
	}

	public int getIdInstitute() {
		return idInstitute;
	}

	public void setIdInstitute(int idInstitute) {
		this.idInstitute = idInstitute;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
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

}


