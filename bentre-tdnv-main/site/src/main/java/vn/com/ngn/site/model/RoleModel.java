package vn.com.ngn.site.model;

import java.util.ArrayList;
import java.util.List;

public class RoleModel {
	private String id;
	private String name;
	private String description;
	private List<String> permissionKeys = new ArrayList<String>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<String> getPermissionKeys() {
		return permissionKeys;
	}
	public void setPermissionKeys(List<String> permissionKeys) {
		this.permissionKeys = permissionKeys;
	}
}
