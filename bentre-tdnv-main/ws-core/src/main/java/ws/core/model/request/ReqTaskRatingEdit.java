package ws.core.model.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ws.core.model.UserOrganization;

@Getter
@Setter
@ToString
public class ReqTaskRatingEdit {
	@NotNull(message = "creator không được trống")
	@Field(value = "creator")
	public UserOrganization creator;
	
	@Min(value = 1, message = "star không được trống")
	@Field(value = "star")
	public int star;
	
	@NotEmpty(message = "comment không được trống")
	@Field(value = "comment")
	public String comment;
	
	public ReqTaskRatingEdit() {
		this.creator=null;
		this.star=1;
		this.comment="";
	}
}
