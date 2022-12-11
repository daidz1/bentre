package vn.com.ngn.site.model.taskcreate;

import com.vaadin.flow.component.Component;

public class UserTemplateGridModel {
	private Component name;
	private Component description;
	private Component userAssign;
	private Component userSupport;
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
	public Component getUserAssign() {
		return userAssign;
	}
	public void setUserAssign(Component userAssign) {
		this.userAssign = userAssign;
	}
	public Component getUserSupport() {
		return userSupport;
	}
	public void setUserSupport(Component userSupport) {
		this.userSupport = userSupport;
	}
	public Component getAction() {
		return action;
	}
	public void setAction(Component action) {
		this.action = action;
	}
}
