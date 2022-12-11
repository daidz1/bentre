package ws.core.enums;

public enum TaskCategory {
	DAGIAO("dagiao","Đã giao"),
	DUOCGIAO("duocgiao","Được giao"),
	THEODOI("theodoi","Theo dõi"),
	GIAOVIECTHAY("giaoviecthay","Giao việc thay"),
	THEODOITHAY("theodoithay","Theo dõi thay");
	
	private String categoryKey;
	private String categoryName;
	
	TaskCategory(String categoryKey, String categoryName){
		this.categoryKey=categoryKey;
		this.categoryName=categoryName;
	}

	public String getCategoryKey() {
		return categoryKey;
	}

	public String getCategoryName() {
		return categoryName;
	}
	
	public static TaskCategory getTaskCategory(String categoryKey) {
		for(TaskCategory taskCategory: TaskCategory.values()) {
			if(taskCategory.getCategoryKey().equalsIgnoreCase(categoryKey)) {
				return taskCategory;
			}
		}
		return null;
	}
	
	
}
