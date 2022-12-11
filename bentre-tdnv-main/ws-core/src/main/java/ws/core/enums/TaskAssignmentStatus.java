package ws.core.enums;

public enum TaskAssignmentStatus {
	DAPHAN_CANBO("daphan_canbo","Đã phân cán bộ"),
	CHUAPHAN_CANBO("chuaphan_canbo","Chưa phân cán bộ");
	
	private String key;
	private String name;
	
	TaskAssignmentStatus(String key, String name){
		this.key=key;
		this.name=name;
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}
	
	public static TaskAssignmentStatus getTaskAssignmentStatus(String key) {
		for(TaskAssignmentStatus taskAssignmentStatus: TaskAssignmentStatus.values()) {
			if(taskAssignmentStatus.getKey().equalsIgnoreCase(key)) {
				return taskAssignmentStatus;
			}
		}
		return null;
	}
}
