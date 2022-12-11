package ws.core.module.ioffice;

public class IOfficeUserModel {
	private int userId=0;
	private String screenName="";
	private String fullName="";
	private String userIOffice="";
	private String email="";
	
	public IOfficeUserModel() {
		
	}
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getUserIOffice() {
		return userIOffice;
	}
	public void setUserIOffice(String userIOffice) {
		this.userIOffice = userIOffice;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
