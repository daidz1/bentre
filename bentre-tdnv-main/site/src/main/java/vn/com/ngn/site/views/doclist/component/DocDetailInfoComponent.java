package vn.com.ngn.site.views.doclist.component;

import com.google.gson.JsonObject;

@SuppressWarnings("serial")
public class DocDetailInfoComponent extends DocInfoComponent{
	public DocDetailInfoComponent(JsonObject jsonDoc) {
		jsonObject = jsonDoc;
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();

		String strDocSignal = jsonObject.get("docSignal").getAsString();
		DocSignalComponent comDocSignal = new DocSignalComponent(strDocSignal);
		this.add(comDocSignal);
		
		DocOrgComponent comOrg = new DocOrgComponent(jsonObject);
		this.add(comOrg);
		
		String strDocSummary = jsonObject.get("docSummary").getAsString();
		DocSummaryComponent comDocSummary = new DocSummaryComponent(strDocSummary);
		this.add(comDocSummary);
		
		DocDateComponent comTaskDate = new DocDateComponent(jsonObject);
		this.add(comTaskDate);
		
		String strDocSignerName = jsonObject.get("docSigner").getAsString();
		DocSignerNameComponent comDocSignerName = new DocSignerNameComponent(strDocSignerName);
		this.add(comDocSignerName);
		
		String strDocBossName = jsonObject.get("creatorName").getAsString();
		DocBossNameComponent comDocBossName = new DocBossNameComponent(strDocBossName);
		this.add(comDocBossName);
		
//		DocNameG3Component comDocG3Name = new DocNameG3Component(jsonObject.getAsJsonArray("norNameG3"));
//		this.add(comDocG3Name);
	}
	
	@Override
	public void configComponent() {
		super.configComponent();

	}
}
