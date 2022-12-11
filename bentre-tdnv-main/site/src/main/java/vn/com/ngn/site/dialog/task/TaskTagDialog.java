package vn.com.ngn.site.dialog.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;

import vn.com.ngn.site.dialog.DialogTemplate;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.model.TaskTagGridModel;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskTagServiceUtil;

@SuppressWarnings("serial")
public class TaskTagDialog extends DialogTemplate{
	private HorizontalLayout hAddTag = new HorizontalLayout();
	private TextField txtNewTag = new TextField("Tên thẻ");
	private Button btnAddTag = new Button("Thêm thẻ",VaadinIcon.PLUS.create());

	private TextField txtSearch = new TextField();

	private Grid<TaskTagGridModel> grid = new Grid<TaskTagGridModel>();

	private String taskId;
	private String tagIdForm;
	
	private ShortcutRegistration regisShortcut = null;

	public TaskTagDialog(String taskId) {
		this.taskId = taskId;

		buildLayout();
		configComponent();

		loadData();
	}

	@Override
	public void buildLayout() {
		super.buildLayout();
		caption.setText("Quản lý thẻ");

		vMain.add(hAddTag);
		vMain.add(txtSearch);
		vMain.add(grid);

		txtSearch.setPlaceholder("Nhập vào từ khóa để tìm kiếm...");

		txtSearch.setWidthFull();

		this.setWidth("700px");

		buildAddLayout();
		buildGrid();
	}

	@Override
	public void configComponent() {
		super.configComponent();
		
		txtNewTag.addFocusListener(e->{
			regisShortcut = btnAddTag.addClickShortcut(Key.ENTER);
		});
		
		txtNewTag.addBlurListener(e->{
			regisShortcut.remove();
		});

		btnAddTag.addClickListener(e->{
			try {
				if(!txtNewTag.isEmpty()) {
					String tagName = txtNewTag.getValue().trim();
					if(tagIdForm==null) {
						JsonObject jsonResponse = TaskTagServiceUtil.createTag(tagName);

						int rspCode = jsonResponse.get("status").getAsInt();
						if(rspCode==201) {
							loadData();
							NotificationUtil.showNotifi("Tạo thẻ "+tagName+" thành công", NotificationTypeEnum.SUCCESS);
						
							txtNewTag.setValue("");
						} else if(rspCode==409) {
							NotificationUtil.showNotifi("Thẻ đã tồn tại!", NotificationTypeEnum.ERROR);
						}
					} else {
						JsonObject jsonResponse = TaskTagServiceUtil.updateTag(tagIdForm, tagName);

						int rspCode = jsonResponse.get("status").getAsInt();
						if(rspCode==200) {
							loadData();
							NotificationUtil.showNotifi("Cập nhật thẻ "+tagName+" thành công", NotificationTypeEnum.SUCCESS);
						
							txtNewTag.setValue("");
							tagIdForm = null;
							btnAddTag.setText("Thêm thẻ mới");
							btnAddTag.setIcon(VaadinIcon.PLUS.create());
						} else {
							NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại!", NotificationTypeEnum.ERROR);
						}
					}
				} else {
					NotificationUtil.showNotifi("Vui lòng nhập vào tên thẻ.", NotificationTypeEnum.WARNING);
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		});
		
		txtSearch.addValueChangeListener(e->{
		});
	}

	private void buildAddLayout() {
		hAddTag.add(txtNewTag,btnAddTag);

		txtNewTag.setWidthFull();

		hAddTag.setDefaultVerticalComponentAlignment(Alignment.END);

		hAddTag.expand(txtNewTag);
		hAddTag.setWidthFull();
	}

	private void buildGrid() {
		grid.addColumn(TaskTagGridModel::getStt).setHeader("STT").setFlexGrow(1);
		grid.addColumn(TaskTagGridModel::getTagName).setHeader("Tên thẻ").setFlexGrow(6);
		grid.addComponentColumn(TaskTagGridModel::getComAction).setHeader("Hành động").setFlexGrow(1);
	}

	private void loadData() {
		List<TaskTagGridModel> listGrid = new ArrayList<TaskTagGridModel>();

		try {
			JsonObject jsonResponse = TaskTagServiceUtil.getTagList();
			JsonObject jsonResponseOfTask = TaskTagServiceUtil.getTagList(taskId,SessionUtil.getUserId(),SessionUtil.getOrgId());
			if(jsonResponse.get("status").getAsInt()==200 && jsonResponseOfTask.get("status").getAsInt()==200) {
				JsonArray jsonArray = jsonResponse.getAsJsonArray("result");
				JsonArray jsonArrayOfTask = jsonResponseOfTask.getAsJsonArray("result");

				List<String> listTagIdOfTask = new ArrayList<String>();
				for(JsonElement jsonEle : jsonArrayOfTask) {
					listTagIdOfTask.add(jsonEle.getAsJsonObject().get("id").getAsString());
				}

				int i = 1;
				for(JsonElement jsonEle : jsonArray) {
					JsonObject jsonTag = jsonEle.getAsJsonObject();
					String tagId = jsonTag.get("id").getAsString();
					String tagName = jsonTag.get("name").getAsString();

					HorizontalLayout hAction = new HorizontalLayout();

					Checkbox cbSelect = new Checkbox();
					Button btnEdit = new Button(VaadinIcon.EDIT.create());
					Button btnDelete = new Button(VaadinIcon.TRASH.create());

					hAction.add(cbSelect,btnEdit,btnDelete);

					btnEdit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
					btnDelete.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

					btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);

					hAction.setDefaultVerticalComponentAlignment(Alignment.CENTER);

					TaskTagGridModel modelGrid = new TaskTagGridModel();
					modelGrid.setStt(i++);
					modelGrid.setTagName(tagName);
					modelGrid.setComAction(hAction);

					listGrid.add(modelGrid);

					if(listTagIdOfTask.contains(tagId)) {
						cbSelect.setValue(true);
					}

					cbSelect.addValueChangeListener(e->{
						try {
							if(e.getValue()) {
								JsonObject jsonResponseSet = TaskTagServiceUtil.setTag(taskId, tagId);

								if(jsonResponseSet.get("status").getAsInt()==200) {
								} else {
									System.out.println(jsonResponseSet);
									NotificationUtil.showNotifi("Cập nhật không thành công, vui lòng thử lại!", NotificationTypeEnum.ERROR);
								}
							} else {
								JsonObject jsonResponseUnset = TaskTagServiceUtil.unsetTag(taskId, Collections.singletonList(tagId), SessionUtil.getUserId(), SessionUtil.getOrgId());

								if(jsonResponseUnset.get("status").getAsInt()==200) {

								} else {
									System.out.println(jsonResponseUnset);
									NotificationUtil.showNotifi("Cập nhật không thành công, vui lòng thử lại!", NotificationTypeEnum.ERROR);
								}
							}
						} catch (Exception e3) {
							e3.printStackTrace();
							NotificationUtil.showNotifi("Cập nhật không thành công, vui lòng thử lại!", NotificationTypeEnum.ERROR);
						}
					});
					
					btnEdit.addClickListener(e->{
						txtNewTag.setValue(tagName);
						tagIdForm = tagId;
						
						btnAddTag.setText("Cập nhật thẻ");
						btnAddTag.setIcon(VaadinIcon.EDIT.create());
					});
					
					btnDelete.addClickListener(e->{
						String title = "Xóa thẻ";
						String description = "Bạn muốn xóa thẻ này? Thẻ này sẽ tự động được xóa khỏi những nhiệm vụ đã gắn thẻ.";

						ConfirmDialog confDialog = new ConfirmDialog(title, description, 
								"Xác nhận", 
								eConfirm->{
									try {
										JsonObject jsonResponseDelete = TaskTagServiceUtil.deleteTag(tagId);

										if(jsonResponseDelete.get("status").getAsInt()==200) {
											NotificationUtil.showNotifi("Xóa thẻ thành công thành công.", NotificationTypeEnum.SUCCESS);
											
											listGrid.remove(modelGrid);
											grid.getDataProvider().refreshAll();
										} else {
											System.out.println(jsonResponseDelete);
											NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
										}
									} catch (IOException e1) {
										e1.printStackTrace();
									}
								},
								"Hủy",
								eCancel->{
									eCancel.getSource().close();
								});
						confDialog.open();
					});
				}
				
				ListDataProvider<TaskTagGridModel> dataProvider = new ListDataProvider<TaskTagGridModel>(listGrid);
				grid.setDataProvider(dataProvider);
			} else {
				System.out.println(jsonResponse);
				NotificationUtil.showNotifi("Không lấy được danh sách thẻ, vui lòng thử lại!", NotificationTypeEnum.ERROR);
			}
		} catch (Exception e2) {
			e2.printStackTrace();
			NotificationUtil.showNotifi("Không lấy được danh sách thẻ, vui lòng thử lại!", NotificationTypeEnum.ERROR);
		}
	}
}
