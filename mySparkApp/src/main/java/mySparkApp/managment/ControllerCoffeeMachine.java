package mySparkApp.managment;

import java.io.Serializable;

public class ControllerCoffeeMachine implements Serializable {
	
	private final String id;
    private String instituteId;
    private Status status;
    
	public ControllerCoffeeMachine(String id, String instituteId, Status status) {
		super();
		this.id = id;
		this.instituteId = instituteId;
		this.status = status;
		
	}

	public String getId() {
		return id;
	}

	public String getInstituteId() {
		return instituteId;
	}

	public void setInstituteId(String instituteId) {
		this.instituteId = instituteId;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	
    
}


