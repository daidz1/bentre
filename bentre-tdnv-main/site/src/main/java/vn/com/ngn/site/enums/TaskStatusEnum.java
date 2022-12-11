package vn.com.ngn.site.enums;

public enum TaskStatusEnum {
	TATCA("tatca","Tất cả"),
	CHUAHOANTHANH("chuahoanthanh","Chưa hoàn thành"),
	CHUAHOANTHANH_TRONGHAN("chuahoanthanh_tronghan","Chưa hoàn thành trong hạn"),
	CHUAHOANTHANH_QUAHAN("chuahoanthanh_quahan","Chưa hoàn thành quá hạn"),
	CHUAHOANTHANH_KHONGHAN("chuahoanthanh_khonghan","Chưa hoàn thành không hạn"),
	DAHOANTHANH("dahoanthanh","Đã hoàn thành"),
	DAHOANTHANH_TRONGHAN("dahoanthanh_tronghan","Đã hoàn thành trong hạn"),
	DAHOANTHANH_QUAHAN("dahoanthanh_quahan","Đã hoàn thành quá hạn"),
	DAHOANTHANH_KHONGHAN("dahoanthanh_khonghan","Đã hoàn thành không hạn"), 
//	CHUAPHAN("chuaphan", "Chưa phân cán bộ"), 
//	DAPHAN("daphan","Đã phân cán bộ"),
	;
	
	private String key;
	private String caption;
	
	private TaskStatusEnum(String key,String caption) {
		this.key = key;
		this.caption = caption;
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
}
