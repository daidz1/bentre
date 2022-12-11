package ws.core.model;

import java.util.Date;
import java.util.LinkedList;

import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Data;
import lombok.ToString;
import ws.core.model.embeded.UserOrganizationExpand;
import ws.core.model.fields.Config;
import ws.core.security.JwtTokenProvider;

@Data
@ToString
@Document(collection = "user")
public class User {
	@Indexed
	@Field(value = "createdTime")
	public Date createdTime;
	
	@Indexed
	@Field(value = "updatedTime")
	public Date updatedTime;
	
	@Id
	@Field(value = "_id")
	public ObjectId id;
	
	@Indexed
	@NotBlank(message = "username không được trống")
	@Field(value="username")
	public String username;
	
	@Indexed
	@NotBlank(message = "email không được trống")
	@Field(value="email")
	public String email;
	
	@Indexed
	@Field(value="phone")
	public String phone;
	
	@NotBlank(message = "password không được trống")
	@Field(value = "password")
	public String password;
	
	@Indexed
	@Field(value = "fullName")
	public String fullName;
	
	@Indexed
	@Field(value = "jobTitle")
	public String jobTitle;
	
	@Indexed
	@Field(value="active")
	public boolean active;
	
	@Field(value = "lastDateLogin")
	public Date lastDateLogin;
	
	@Field(value = "lastIPLogin")
	public String lastIPLogin;

	@Indexed
	@Field(value = "creatorId")
	public String creatorId;
	
	@Indexed
	@Field(value="creatorName")
	public String creatorName;
	
	@Indexed
	@Field(value="organizations")
	public LinkedList<UserOrganizationExpand> organizations;
	
	@Indexed
	@Field(value="leaders")
	public LinkedList<UserOrganization> leaders;
	
	@Indexed
	@Field(value="accountDomino")
	public String accountDomino;
	
	@Indexed
	@Field(value="activeCode")
	public String activeCode;
	
	@Indexed
	@Field(value="config")
	public Config config;
	
	@Field(value="loginFail")
	public int loginFail;
	
	@Indexed
	@Field(value="userIdMysql")
	public String userIdMysql;
	
	@Field(value="statusMysql")
	public String statusMysql;
	
	@Indexed
	@Field(value="refreshToken")
	public String refreshToken;
	
	@Field(value = "refreshTokenExpire")
	public Date refreshTokenExpire;
	
	public User() {
		this.id=ObjectId.get();
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this.phone="";
		this.jobTitle="";
		this.organizations=new LinkedList<UserOrganizationExpand>();
		this.leaders=new LinkedList<UserOrganization>();
		this.accountDomino=null;
		this.config=new Config();
		this.loginFail=0;
	}
	
	public String getId() {
		return id.toHexString();
	}
	
	public long getCreatedTime() {
		return this.createdTime.getTime();
	}
	
	public long getUpdatedTime() {
		return this.updatedTime.getTime();
	}
	
	@Hidden
	public String createRefreshToken() {
		JwtTokenProvider jwtTokenProvider=new JwtTokenProvider();
		this.refreshToken=jwtTokenProvider.getJWTRefreshToken();
		this.refreshTokenExpire=jwtTokenProvider.getJWTRefreshExpirationDate();
		return this.refreshToken;
	}
}
