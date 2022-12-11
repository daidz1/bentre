package ws.core.model.request;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Field;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReqTaskSetAssigneeUser {
	@Schema(name = "userId", description = "Id cán bộ được chọn gán xử lý nhiệm vụ cho đơn vị", required = true, example = "60ebe61ad5cfdf70fa559cda")
	@NotNull(message = "userId không được trống")
	@Field(value = "userId")
	public String userId;
	
	@Schema(name = "fullName", description = "Họ tên cán bộ được chọn gán xử lý nhiệm vụ cho đơn vị", required = true, example = "Cao Thị Mai Hồng")
	@NotNull(message = "fullName không được trống")
	@Field(value = "fullName")
	public String fullName;
	
	@Schema(name = "assignmentBy", description = "Object user-organization người hành động gán cán bộ cho đơn vị", required = true)
	@NotNull(message = "assignmentBy không được trống")
	@Field(value = "assignmentBy")
	public ReqUserOrganization assignmentBy;
	
	public ReqTaskSetAssigneeUser() {
		
	}
}
