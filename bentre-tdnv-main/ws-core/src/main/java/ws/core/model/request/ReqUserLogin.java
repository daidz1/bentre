package ws.core.model.request;

import javax.validation.constraints.NotBlank;

import org.springframework.data.mongodb.core.mapping.Field;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReqUserLogin {
	@NotBlank(message = "username không được trống")
	@Field(value="username")
	@Schema(name = "username", description = "Tài khoản người dùng", required = true, example = "khuetech")
	public String username;
	
	@NotBlank(message = "password không được trống")
	@Field(value="password")
	@Schema(name = "password", description = "Mật khẩu người dùng", required = true, example = "abc123")
	public String password;
	
	public ReqUserLogin() {
		
	}
}
