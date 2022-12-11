package vn.com.ngn.site.model.docimport;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.Component;

public class DocImportGridModel {
	private Component comDocSummary;
	private Component comDocSymbol;
	private Component comDocSignal;
	private Component comDocDate;
	private Component comAction;
	private JsonObject jsonDoc;
	
	public Component getComDocSummary() {
		return comDocSummary;
	}
	public void setComDocSummary(Component comDocSummary) {
		this.comDocSummary = comDocSummary;
	}
	public Component getComDocSymbol() {
		return comDocSymbol;
	}
	public void setComDocSymbol(Component comDocSymbol) {
		this.comDocSymbol = comDocSymbol;
	}
	public Component getComDocSignal() {
		return comDocSignal;
	}
	public void setComDocSignal(Component comDocSignal) {
		this.comDocSignal = comDocSignal;
	}
	public Component getComDocDate() {
		return comDocDate;
	}
	public void setComDocDate(Component comDocDate) {
		this.comDocDate = comDocDate;
	}
	public Component getComAction() {
		return comAction;
	}
	public void setComAction(Component comAction) {
		this.comAction = comAction;
	}
	public JsonObject getJsonDoc() {
		return jsonDoc;
	}
	public void setJsonDoc(JsonObject jsonDoc) {
		this.jsonDoc = jsonDoc;
	}
}
