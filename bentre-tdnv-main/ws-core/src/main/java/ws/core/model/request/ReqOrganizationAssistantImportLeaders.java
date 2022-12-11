package ws.core.model.request;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReqOrganizationAssistantImportLeaders {

	@NotNull(message = "organizationId không được trống")
	@Field(value = "organizationId")
	public String organizationId;
	
	@NotNull(message = "userId không được trống")
	@Field(value = "userId")
	public String userId;
	
	@NotNull(message = "leaderIds không được trống")
	@Field(value = "leaderIds")
	public List<String> leaderIds;
	
	public ReqOrganizationAssistantImportLeaders() {
		this.leaderIds=new ArrayList<String>();
	}
}
