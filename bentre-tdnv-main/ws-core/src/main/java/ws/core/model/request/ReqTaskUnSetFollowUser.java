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
public class ReqTaskUnSetFollowUser {
	@Schema(name = "organizationId", description = "Id đơn vị", required = true, example = "605bfeb9d9b8222a8db47ed8")
	@NotNull(message = "organizationId không được trống")
	@Field(value = "organizationId")
	public String organizationId;
	
	@Schema(name = "organizationName", description = "Tên đơn vị", required = true, example = "VỤ TỔ CHỨC CÁN BỘ")
	@NotNull(message = "organizationName không được trống")
	@Field(value = "organizationName")
	public String organizationName;
	
	@Schema(name = "reason", description = "Lý do hủy", required = true, example = "Cần chờ xét lại")
	@NotNull(message = "reason không được trống")
	@Field(value = "reason")
	public String reason;
	
	@Schema(name = "unassignmentBy", description = "Object user-organization người hành động (lưu vào nhật ký)", required = true)
	@NotNull(message = "unassignmentBy không được trống")
	@Field(value = "unassignmentBy")
	public ReqUserOrganization unassignmentBy;
	
	public ReqTaskUnSetFollowUser() {
		
	}
	
}
