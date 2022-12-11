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
public class ReqTaskUnSetAssigneeUser {
	@Schema(name = "reason", description = "Lý do hủy", required = true, example = "Cần chờ xét lại")
	@NotNull(message = "reason không được trống")
	@Field(value = "reason")
	public String reason;
	
	@Schema(name = "unassignmentBy", description = "Object user-organization người hành động (lưu vào nhật ký)", required = true)
	@NotNull(message = "unassignmentBy không được trống")
	@Field(value = "unassignmentBy")
	public ReqUserOrganization unassignmentBy;
	
	public ReqTaskUnSetAssigneeUser() {
		
	}
}
