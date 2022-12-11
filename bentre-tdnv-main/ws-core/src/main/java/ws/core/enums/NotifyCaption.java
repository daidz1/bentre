package ws.core.enums;

public enum NotifyCaption {
	TaoNhiemVuMoi("Giao nhiệm vụ mới","nhiemvumoi"),
	CapNhatNhiemVu("Cập nhật nhiệm vụ","capnhatnhiemvu"),
	ThayDoiChuTri("Thay đổi chủ trì","thaydoichutri"),
	ThayDoiNguoiGiao("Thay đổi người giao","thaydoinguoigiao"),
	CapNhatTienDo("Cập nhật tiến độ","capnhattiendo"),
	YKienVaPhanHoi("Thêm ý kiến và phản hồi","ykienvaphanhoi"),
	NhacNhoNhiemVu("Nhắc nhở nhiệm vụ","nhacnhonhiemvu"),
	BatDauThucHienNhiemVu("Bắt đầu thực hiện nhiệm vụ","batdauthuchiennhiemvu"),
	HoanThanhNhiemVu("Hoàn thành nhiệm vụ","hoanthanhnhiemvu"),
	TrieuHoiNhiemVu("Triệu hồi nhiệm vụ","trieuhoinhiemvu"),
	DanhGiaNhiemVu("Đánh giá nhiệm vụ","danhgianhiemvu"),
	XoaNhiemVu("Xóa nhiệm vụ","daxoanhiemvu"),
	NhiemVuSapQuaHan("Nhiệm vụ sắp quá hạn", "nhiemvusapquahan"),
	NhiemVuDaQuaHan("Nhiệm vụ đã quá hạn","nhiemvudaquahan"),
	LoginWeb("Đăng nhập trên web","loginweb"),
	LoginFail("Đăng nhập thất bại","loginfail"),
	PhanNhiemVuDonVi("Phân nhiệm vụ đơn vị","phannhiemvudonvi"),
	HuyPhanNhiemVuDonVi("Hủy phân nhiệm vụ đơn vị","huyphannhiemvudonvi");
	
	private String title;
	private String action;
	
	NotifyCaption(String title, String action){
		this.title=title;
		this.action=action;
	}
	
	public String getTitle() {
		return this.title;
	}

	public String getAction() {
		return action;
	}
}
