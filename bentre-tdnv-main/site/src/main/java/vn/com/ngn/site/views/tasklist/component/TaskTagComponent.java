package vn.com.ngn.site.views.tasklist.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.WrapMode;

import vn.com.ngn.site.dialog.task.TaskTagDialog;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.service.TaskTagServiceUtil;

@SuppressWarnings("deprecation")
public class TaskTagComponent extends TaskInfoComponent {
	private FlexLayout flexLayout = new FlexLayout();
	private Button btnAdd = new Button("Thêm thẻ",VaadinIcon.TAGS.create());
	private String idTask;
	
	private Button btnTrigger = new Button();

	public TaskTagComponent(String idTask) {
		this.idTask = idTask;
		
		buildLayout();
		configComponent();
		
		loadTagList();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		
		flexLayout.add(btnAdd);
		
		btnAdd.getStyle().set("margin-right", "20px");
		btnAdd.addThemeVariants(ButtonVariant.LUMO_SMALL);
		//btnAdd.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		
		flexLayout.setWrapMode(WrapMode.WRAP);
		
		//flexLayout.getStyle().set("height", "");
		
		btnTrigger.setVisible(false);
		this.add(flexLayout);
		this.add(btnTrigger);
	}
	
	@Override
	public void configComponent() {
		super.configComponent();
		
		btnAdd.addClickListener(e->{
			TaskTagDialog dialog = new TaskTagDialog(idTask);
			
			dialog.open();
			
			dialog.addOpenedChangeListener(eClose->{
				if(!eClose.isOpened()) {
					loadTagList();
				}
			});
		});
	}
	
	private void loadTagList() {
		flexLayout.removeAll();
		flexLayout.add(btnAdd);
		try {
			JsonObject jsonResponse = TaskTagServiceUtil.getTagList(idTask,SessionUtil.getUserId(),SessionUtil.getOrgId());

			if(jsonResponse.get("status").getAsInt()==200) {
				JsonArray jsonArrayOfTask = jsonResponse.getAsJsonArray("result");

				for(JsonElement jsonEle : jsonArrayOfTask) {
					JsonObject jsonTag = jsonEle.getAsJsonObject();
					
					flexLayout.add(createTagBlock(jsonTag.get("id").getAsString(), jsonTag.get("name").getAsString()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Component createTagBlock(String idTag, String tagName) {
		Button btnTag = new Button(tagName,VaadinIcon.TAG.create());
		
		btnTag.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnTag.addThemeVariants(ButtonVariant.LUMO_SMALL);
		
		btnTag.getStyle().set("font-size", "var(--lumo-font-size-xs)");
		btnTag.getStyle().set("margin-right", "15px");
		
		btnTag.addClickListener(e->{
			btnTrigger.setText(tagName);
			btnTrigger.click();
		});
		
		return btnTag;
	}

	public Button getBtnTrigger() {
		return btnTrigger;
	}
}