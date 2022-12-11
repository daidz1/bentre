package ws.core.model.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReqUserOrganization {
	@Schema(name = "userId", description = "Id cán bộ", required = true, example = "60ebe61ad5cfdf70fa559cda")
	@Field(value = "userId")
	public String userId;
	
	@Schema(name = "fullName", description = "Họ tên cán bộ", required = true, example = "Cao Thị Mai Hồng")
	@Field(value = "fullName")
	public String fullName;
	
	@Schema(name = "organizationId", description = "Id đơn vị", required = true, example = "605bfeb9d9b8222a8db47ed8")
	@NotNull(message = "organizationId không được trống")
	@Field(value = "organizationId")
	public String organizationId;
	
	@Schema(name = "organizationName", description = "Tên đơn vị", required = true, example = "VỤ TỔ CHỨC CÁN BỘ")
	@NotNull(message = "organizationName không được trống")
	@Field(value = "organizationName")
	public String organizationName;
	
	public ReqUserOrganization() {

	}
	
	public ReqUserOrganization(@NotEmpty(message = "userId không được trống") String userId, @NotEmpty(message = "organizationId không được trống") String organizationId) {
		super();
		this.userId = userId;
		this.organizationId = organizationId;
	}

	public boolean compareTo(ReqUserOrganization userTaskOther) {
		if(!userId.equalsIgnoreCase(userTaskOther.userId))
			return false;
		if(!fullName.equalsIgnoreCase(userTaskOther.fullName))
			return false;
		if(!organizationId.equalsIgnoreCase(userTaskOther.organizationId))
			return false;
		if(!organizationName.equalsIgnoreCase(userTaskOther.organizationName))
			return false;
		return true;
	}
	
	public boolean validAll() {
		if(userId==null || fullName==null || organizationId==null || organizationName==null)
			return false;
		return true;
	}
	
	public boolean validIds() {
		if(StringUtils.isEmpty(this.userId) || StringUtils.isEmpty(this.organizationId))
			return false;
		return true;
	}
	
	public boolean validUserId() {
		if(StringUtils.isEmpty(this.userId))
			return false;
		return true;
	} 
	
	public boolean validUser() {
		if(StringUtils.isEmpty(this.userId) || StringUtils.isEmpty(this.fullName))
			return false;
		return true;
	}
	
	public boolean validOrganizationId() {
		if(StringUtils.isEmpty(this.organizationId))
			return false;
		return true;
	}
	
	public boolean validOrganization() {
		if(StringUtils.isEmpty(this.organizationId) || StringUtils.isEmpty(this.organizationName))
			return false;
		return true;
	}
	
	/* Dùng để kiểm tra đầu vào tối thiểu, cơ bản */
	public boolean validBasic() {
		if(StringUtils.isEmpty(this.organizationId) || StringUtils.isEmpty(this.organizationName))
			return false;
		return true;
	}
	
	/* Dùng để check điều kiện thông báo */
	public boolean validNotify() {
		if(StringUtils.isEmpty(this.userId))
			return false;
		return true;
	}
	
	@Hidden
	public String getText() {
		String text=null;
		if(StringUtils.isEmpty(this.fullName)==false && StringUtils.isEmpty(this.organizationName)==false) {
			text=this.fullName +" (" + this.organizationName +")";
		}else if(StringUtils.isEmpty(this.organizationName)==false) {
			text=this.organizationName;
		}
		return text;
	}
}
