package mySparkApp;

import java.io.Serializable;

public class ControllerCoffeeMachine implements Serializable {
	
	private final String id;
    private String location;
    //private Map<CapsuleType, Integer> capsules;
    private double currentCash;
    //private List<Beverage> menu;
    //private MachineStatus status
    
	public ControllerCoffeeMachine(String id, String location, double currentCash) {
		super();
		this.id = id;
		this.location = location;
		this.currentCash = currentCash;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public double getCurrentCash() {
		return currentCash;
	}

	public void setCurrentCash(double currentCash) {
		this.currentCash = currentCash;
	}

	public String getId() {
		return id;
	}
    
    
}


