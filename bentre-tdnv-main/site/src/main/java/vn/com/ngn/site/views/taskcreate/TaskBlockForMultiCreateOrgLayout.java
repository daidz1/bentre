package vn.com.ngn.site.views.taskcreate;

import java.util.ArrayList;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.dialog.task.ChooseOrgGroupDialog;
import vn.com.ngn.site.model.taskcreate.TaskAssigneeOrgModel;
import vn.com.ngn.site.model.taskcreate.TaskDataForMultiCreateOrgModel;

@SuppressWarnings("serial")
public class TaskBlockForMultiCreateOrgLayout extends VerticalLayout implements LayoutInterface{
	private Board board = new Board();
	private Row row = new Row();
	private MenuBar menuId = new MenuBar();
	private MenuItem menuItemId;
	private MenuItem menuItemClone;
	private MenuItem menuItemDelete;
	private Board boardTaskInfo = new Board();
	private TaskInfoLayout layoutTaskInfo = new TaskInfoLayout(null);

	private Button btnChooseOrgTemplate = new Button("Chọn nhóm cơ quan/đơn vị",VaadinIcon.AREA_SELECT.create());
	private TaskAssigneeOrgLayout layoutTaskAssignee = new TaskAssigneeOrgLayout(null);
	private TaskSupportOrgLayout layoutTaskSupport = new TaskSupportOrgLayout(null);

	private int formId;

	private boolean isSpecific = true;

	private boolean isFirstInit = true;

	public void initForm() {
		buildLayout();

		if(isFirstInit) {
			configComponent();
		}
	}

	@Override
	public void buildLayout() {
		this.add(menuId);
		this.add(board);

		board.addRow(row);

		if(isSpecific) {
			row.add(boardTaskInfo,2);
			row.add(buildChooseUserBlock());

			buildIdBlock();
			buildTaskInfoLayout();
		} else {
			row.add(buildChooseUserBlock());

			buildIdBlock();
		}

		this.setSpacing(false);
		this.setWidthFull();
		this.getStyle().set("border-bottom", "1px solid #f3eded");
	}

	@Override
	public void configComponent() {
		btnChooseOrgTemplate.addClickListener(e->{
			ChooseOrgGroupDialog dialog = new ChooseOrgGroupDialog();

			dialog.open();

			dialog.addOpenedChangeListener(eClosed->{
				if(dialog.getJsonAssign()!=null) {
					layoutTaskAssignee.initOldValue(dialog.getJsonAssign());
					layoutTaskAssignee.displayResult();

					layoutTaskSupport.initOldValue(dialog.getJsonFollow());
					layoutTaskSupport.displayResult();
				}
			});
		});
	}

	private void buildIdBlock() {
		if(isFirstInit) {
			menuItemId = menuId.addItem("#"+formId);
			SubMenu subMenu = menuItemId.getSubMenu();
			menuItemClone = subMenu.addItem("Nhân bản nhiệm vụ");
			menuItemDelete = subMenu.addItem("Xóa nhiệm vụ");
		}
	}

	private void buildTaskInfoLayout() {
		Row rowTaskInfo = new Row();

		VerticalLayout vTaskInfo2 = new VerticalLayout();

		rowTaskInfo.add(layoutTaskInfo);
		rowTaskInfo.add(vTaskInfo2);

		layoutTaskInfo.getDpCreateTime().setVisible(false);
		layoutTaskInfo.getTxtTaskDescription().setHeight("200px");

		layoutTaskInfo.setSpacing(false);
		layoutTaskInfo.setPadding(true);
		layoutTaskInfo.getStyle().set("padding-top", "0");
		layoutTaskInfo.getStyle().set("padding-left", "0");

		vTaskInfo2.add(layoutTaskInfo.getBoardEndtime());
		vTaskInfo2.add(layoutTaskInfo.getCmbPriority());
		vTaskInfo2.add(layoutTaskInfo.getUpload());
		layoutTaskInfo.getUpload().getStyle().set("margin-top", "10px");

		vTaskInfo2.getStyle().set("padding-top", "0");
		vTaskInfo2.setSpacing(false);

		boardTaskInfo.add(rowTaskInfo);
	}

	private Component buildChooseUserBlock() {
		Component comLayout;

		layoutTaskAssignee.simplifyDisplay();
		layoutTaskAssignee.setTaskSupportOrgLayout(layoutTaskSupport);

		layoutTaskSupport.simplifyDisplay();
		layoutTaskSupport.setTaskAssigneeOrgLayout(layoutTaskAssignee);
		if(isSpecific) {
			VerticalLayout layout = new VerticalLayout();

			layout.add(layoutTaskAssignee);
			layout.add(layoutTaskSupport);

			//layout.setPadding(false);
			layoutTaskAssignee.gethWrapCaption().setVisible(true);
			layoutTaskAssignee.gethWrapCaption().add(btnChooseOrgTemplate);

			comLayout = layout;
		} else {
			VerticalLayout vLayout = new VerticalLayout();
			HorizontalLayout layout = new HorizontalLayout();

			layout.add(layoutTaskAssignee);
			layout.add(layoutTaskSupport);

			layoutTaskAssignee.setPadding(false);
			layoutTaskSupport.setPadding(false);

			layoutTaskAssignee.getStyle().set("border-bottom", "none");
			layoutTaskAssignee.getStyle().set("padding-bottom", "0");
			layoutTaskSupport.getStyle().set("padding-bottom", "0");

			layoutTaskAssignee.gethWrapCaption().setVisible(false);

			layout.setWidthFull();

			vLayout.add(btnChooseOrgTemplate);
			vLayout.add(layout);

			comLayout = vLayout;
		}

		return comLayout;
	}

	public void setData(TaskDataForMultiCreateOrgModel model) {
		layoutTaskInfo.initValueForm(model.getModelTaskInfo());
		layoutTaskAssignee.initValueForm(model.getModelOrgAssignee());
		layoutTaskSupport.initValueForm(model.getListOrgSupport());
	}

	public TaskDataForMultiCreateOrgModel getData() {
		TaskDataForMultiCreateOrgModel model = new TaskDataForMultiCreateOrgModel();

		model.setModelTaskInfo(layoutTaskInfo.getFormData());
		model.setModelOrgAssignee(layoutTaskAssignee.getOrgModel());
		model.setListOrgSupport(new ArrayList<TaskAssigneeOrgModel>(layoutTaskSupport.getMapOrg().values()));

		return model;
	}

	public boolean validateForm() {
		if(isSpecific && !layoutTaskInfo.validateForm()) {
			return false;
		}
		if(!layoutTaskAssignee.validateForm()) {
			return false;
		}

		return true;
	}

	public void reloadForm() {
		this.isFirstInit = false;

		this.removeAll();
		board.removeAll();
		row.removeAll();
		boardTaskInfo.removeAll();

		initForm();
	}

	public int getFormId() {
		return formId;
	}
	public void setFormId(int formId) {
		this.formId = formId;
	}
	public void setSpecific(boolean isSpecific) {
		this.isSpecific = isSpecific;
	}
	public MenuItem getMenuItemClone() {
		return menuItemClone;
	}
	public MenuItem getMenuItemDelete() {
		return menuItemDelete;
	}
}
