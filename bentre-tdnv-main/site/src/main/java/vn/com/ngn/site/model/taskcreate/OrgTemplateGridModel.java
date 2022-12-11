package vn.com.ngn.site.model.taskcreate;

import com.vaadin.flow.component.Component;

public class OrgTemplateGridModel {
	private Component name;
	private Component description;
	private Component orgAssign;
	private Component orgSupport;
	private Component action;
	
	public Component getName() {
		return name;
	}
	public void setName(Component name) {
		this.name = name;
	}
	public Component getDescription() {
		return description;
	}
	public void setDescription(Component description) {
		this.description = description;
	}
	
	public Component getAction() {
		return action;
	}
	public void setAction(Component action) {
		this.action = action;
	}
	public Component getOrgAssign() {
		return orgAssign;
	}
	public void setOrgAssign(Component orgAssign) {
		this.orgAssign = orgAssign;
	}
	public Component getOrgSupport() {
		return orgSupport;
	}
	public void setOrgSupport(Component orgSupport) {
		this.orgSupport = orgSupport;
	}
	
}
