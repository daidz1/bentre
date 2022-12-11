package ws.core.model.request;

import java.util.LinkedList;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ws.core.anotation.ValidPassword;
import ws.core.model.UserOrganization;

@Getter
@Setter
@ToString
public class ReqCheckValidSample {
	@NotNull(message = "ownerTask không được trống")
	@Field(value = "ownerTask")
	public UserOrganization ownerTask;
	
	@Field(value="addFollowersTask")
	public LinkedList<UserOrganization> addFollowersTask;
	
	@NotBlank(message = "title không được trống")
	@Field(value = "title")
	public String title;
	
	@NotNull(message = "email không được trống")
	@Email(message = "email không đúng định dạng")
	@Field(value = "email")
	public String email;
	
	@Field(value = "number")
	public int number;
	
	@Field(value = "time")
	public long time;
	
	@ValidPassword(message = "password không đúng yêu cầu")
	@Field(value = "password")
	public String password;
	
	public ReqCheckValidSample() {
		
	}
}
