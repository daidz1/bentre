package ws.core.model.request;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Field;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ReqUserOrganizationExpandEdit {
	@Schema(name = "organizationId", description = "Id đơn vị", required = true, example = "605bfeb9d9b8222a8db47ed8")
	@NotNull(message = "organizationId không được trống")
	@Field(value = "organizationId")
	public String organizationId;
	
	@Schema(name = "accountIOffice", description = "Tài khoản IOffice", required = false, example = "ioffice-vpubnd")
	@Field(value = "accountIOffice")
	public String accountIOffice;
	
	@Schema(name = "jobTitle", description = "Chức vụ", required = false, example = "Chuyên viên")
	@Field(value = "jobTitle")
	public String jobTitle;
	
	@Schema(name = "numberOrder", description = "Số thứ tự", required = false, example = "1")
	@Field(value = "numberOrder")
	public long numberOrder;
	
	public ReqUserOrganizationExpandEdit() {
	}
	
}
