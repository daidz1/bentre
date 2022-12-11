package ws.core.enums;

public enum TaskPriority {
	THUONG(1, "Thường"),
	KHAN(2, "Khẩn"),
	HOATOC(3, "Hỏa tốc");
	
	private int key;
	private String name;
	
	TaskPriority(int key, String name){
		this.key=key;
		this.name=name;
	}

	public int getKey() {
		return key;
	}

	public String getName() {
		return name;
	}
	
	public static String getName(int key) {
		for(TaskPriority taskPriority:TaskPriority.values()) {
			if(taskPriority.key==key) {
				return taskPriority.name;
			}
		}
		return "Thường";
	}
}
