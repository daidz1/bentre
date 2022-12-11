package vn.com.ngn.site.enums;

public enum TaskPriorityEnum {
	HOATOC(3,"Hỏa tốc","red"),
	KHAN(2,"Khẩn","orange"),
	THUONG(1,"Thường","green")
	;
	
	private int key;
	private String caption;
	private String color;
	
	private TaskPriorityEnum(int key, String caption, String color) {
		this.key = key;
		this.caption = caption;
		this.color = color;
	}

	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
}
