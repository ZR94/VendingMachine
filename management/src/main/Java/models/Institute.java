package models;

public class Institute {

	private int instituteId;
	private String instituteName;
	
	public Institute() {}
	
	public Institute(String name) {
		this.instituteName=name;
	}
	
	public Institute(int instituteId, String instituteName) {
		
		this.instituteId = instituteId;
		this.instituteName = instituteName;
	}

	//GETTER
	public int getInstituteId() {
		return instituteId;
	}

	public String getInstituteName() {
		return instituteName;
	}

	public void setInstituteId(int instituteId) {
		this.instituteId = instituteId;
	}

	

	
	
}
