package ws.core.model.request;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ws.core.model.fields.Config;

@Getter
@Setter
@ToString
public class ReqUserEditAdmin {
	@Field(value = "updatedTime")
	public Date updatedTime;
	
	@NotBlank(message = "username không được trống")
	@Field(value="username")
	public String username;
	
	@NotBlank(message = "email không được trống")
	@Email(message = "email không đúng định dạng")
	@Field(value="email")
	public String email;
	
	@Field(value="phone")
	public String phone;
	
	@NotBlank(message = "fullName không được trống")
	@Field(value = "fullName")
	public String fullName;
	
	@Field(value = "jobTitle")
	public String jobTitle;
	
	@Field(value="active")
	public boolean active;
	
	@Field(value = "accountDomino")
	public String accountDomino;
	
	@Field(value = "activeCode")
	public String activeCode;
	
	@Field(value = "config")
	public Config config;
	
	public ReqUserEditAdmin() {
		this.updatedTime=new Date();
		this.accountDomino=null;
		this.config=null;
	}
	
	public long getUpdatedTime() {
		return this.updatedTime.getTime();
	}
}
