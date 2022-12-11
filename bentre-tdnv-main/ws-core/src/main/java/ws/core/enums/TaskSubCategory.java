package ws.core.enums;

public enum TaskSubCategory {
	CHUAHOANTHANH("chuahoanthanh","Chưa hoàn thành"),
	CHUAHOANTHANH_TRONGHAN("chuahoanthanh_tronghan","Chưa hoàn thành trong hạn"),
	CHUAHOANTHANH_QUAHAN("chuahoanthanh_quahan","Chưa hoàn thành quá hạn"),
	CHUAHOANTHANH_KHONGHAN("chuahoanthanh_khonghan","Chưa hoàn thành không hạn"),
	DAHOANTHANH("dahoanthanh","Đã hoàn thành"),
	DAHOANTHANH_TRONGHAN("dahoanthanh_tronghan","Đã hoàn thành trong hạn"),
	DAHOANTHANH_QUAHAN("dahoanthanh_quahan","Đã hoàn thành quá hạn"),
	DAHOANTHANH_KHONGHAN("dahoanthanh_khonghan","Đã hoàn thành không hạn"),
	CHUATHUCHIEN("chuathuchien","Chưa thực hiện");
	
	private String subCategoryKey;
	private String subCategoryName;
	
	TaskSubCategory(String subCategoryKey, String subCategoryName){
		this.subCategoryKey=subCategoryKey;
		this.subCategoryName=subCategoryName;
	}

	public String getSubCategoryKey() {
		return subCategoryKey;
	}

	public String getSubCategoryName() {
		return subCategoryName;
	}
	
	public static TaskSubCategory getTaskSubCategory(String subCategoryKey) {
		for(TaskSubCategory taskSubCategory: TaskSubCategory.values()) {
			if(taskSubCategory.getSubCategoryKey().equalsIgnoreCase(subCategoryKey)) {
				return taskSubCategory;
			}
		}
		return null;
	}
}
