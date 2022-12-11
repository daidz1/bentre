package vn.com.ngn.site.model;

public class UserModel {
	private String id;
	private String username;
	private String email;
	private String fullname;
	private String jobTitle;
	private String accountDomino;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	public String getAccountDomino() {
		return accountDomino;
	}
	public void setAccountDomino(String accountDomino) {
		this.accountDomino = accountDomino;
	}
}
