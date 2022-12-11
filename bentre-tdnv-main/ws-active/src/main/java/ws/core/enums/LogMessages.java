package ws.core.enums;

public enum LogMessages {
	OK("Success"),
	INTERNAL_SERVER_ERROR("Lỗi hệ thống, vui lòng liên hệ quản trị viên."),
	NOT_FOUND("Không tìm thấy hoặc bảo toàn dữ liệu không chính xác"),
	CONFLICT("Dữ liệu bị xung đột với hệ thống"),
	NOT_ACCEPTABLE("Không chấp nhận dữ liệu"),
	BAD_REQUEST("Dữ liệu không đúng hoặc thiếu dữ liệu"),
	FORBIDDEN("Bị cấm truy cập, hoặc không đủ quyền truy cập"),
	UPDATE_SUCCESS("Cập nhật thành công");
	
	private String message;
	LogMessages(String message){
		this.message=message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
