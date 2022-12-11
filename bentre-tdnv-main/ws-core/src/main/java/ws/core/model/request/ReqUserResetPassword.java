package ws.core.model.request;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReqUserResetPassword {
	@Field(value = "updatedTime")
	public Date updatedTime;
	
	@NotBlank(message = "passwordNew không được trống")
	@Field(value="passwordNew")
	public String passwordNew;
	
	public ReqUserResetPassword() {
		this.updatedTime=new Date();
	}
}
