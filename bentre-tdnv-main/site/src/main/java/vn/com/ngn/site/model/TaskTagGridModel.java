package vn.com.ngn.site.model;

import com.vaadin.flow.component.Component;

public class TaskTagGridModel {
	private int stt;
	private String tagName;
	private Component comAction;
	
	public int getStt() {
		return stt;
	}
	public void setStt(int stt) {
		this.stt = stt;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public Component getComAction() {
		return comAction;
	}
	public void setComAction(Component comAction) {
		this.comAction = comAction;
	}
}
