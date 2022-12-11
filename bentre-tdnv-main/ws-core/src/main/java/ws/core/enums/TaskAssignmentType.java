package ws.core.enums;

public enum TaskAssignmentType {
	User("user", "Giao cho cá nhân"),
	Organization("organization", "Giao cho đơn vị");
	
	private String key;
	private String desciption;
	
	TaskAssignmentType(String key, String desciption){
		this.key=key;
		this.desciption=desciption;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDesciption() {
		return desciption;
	}

	public void setDesciption(String desciption) {
		this.desciption = desciption;
	}

}
