package models;

public class MachineManagement {

	private int id;
	private int instituteId;
	private MachineStatus status;
	
	public MachineManagement(int instituteId) {
		this.instituteId=instituteId;
	}
	
	public MachineManagement(int id, int instituteId, MachineStatus status) {
		
		this.id = id;
		this.instituteId = instituteId;
		this.status = status;
	}

	//GETTER
	public int getInstituteId() {
		return instituteId;
	}

	public MachineStatus getStatus() {
		return status;
	}

	public int getId() {
		return id;
	}

	//SETTER
	public void setInstituteId(int instituteId) {
		this.instituteId = instituteId;
	}

	public void setStatus(MachineStatus status) {
		this.status = status;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
}
