package ws.core.enums;

public enum Permission {
	quanly_nguoidung("Quản lý người dùng", "Được phép xem, thêm, xóa người dùng"),
	quanly_donvi("Quản lý đơn vị", "Được phép xem, thêm, xóa, sửa đơn vị"),
	quanly_vaitro("Quản lý vai trò", "Được phép xem, thêm, xóa, sửa vai trò"),
	xem_nguoidung("Xem danh sách người dùng", "Được phép xem danh sách người dùng"),
	them_nguoidung("Thêm người dùng", "Được phép thêm người dùng"),
	xoa_nguoidung("Xóa người dùng", "Được phép xóa người dùng"),
	capnhat_nguoidung("Cập nhật người dùng", "Được phép cập nhật người dùng"),
	giaonhiemvu("Giao nhiệm vụ", "Được phép giao nhiệm vụ"),
	truongdonvi("Trưởng đơn vị", "Trưởng đơn vị"),
	photruongdonvi("Phó trưởng đơn vị", "Phó trưởng đơn vị"),
	khongnhanviec("Không nhận việc", "Không hiển thị trên màn hình giao việc"),
	phannhiemvudonvi("Phân nhiệm vụ đơn vị cho cán bộ", "Phần nhiệm vụ được giao cho đơn vị nhưng chưa phân hoặc chọn tài khoản đích danh để xử lý"),
	themvanban("Thêm văn bản", "Được phép thêm văn bản vào hệ thống để giao nhiệm vụ"),
	xemvanban("Xem văn bản", "Xem văn bản liên quan đến tài khoản (thêm, lấy từ IOffice, ...)"),
	xemvanbandonvi("Xem văn bản đơn vị", "Được phép xem toàn bộ văn bản liên quan đến đơn vị"),
	xemnhiemvudonvi("Xem nhiệm vụ đơn vị", "Được phép xem tất cả nhiệm vụ trong đơn vị");
	
	private String name;
	private String description;
	
	Permission(String name, String description){
		this.name=name;
		this.description=description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
