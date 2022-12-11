package vn.com.ngn.site.views.main;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import vn.com.ngn.site.dialog.ChangeOrgDialog;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.views.dashboard.DashboardNewView;
import vn.com.ngn.site.views.login.LoginView;

@SuppressWarnings("serial")
@Route("blank")
public class BlankView extends VerticalLayout implements BeforeEnterObserver{
	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		if(SessionUtil.isLogin()) {
			if(SessionUtil.getOrg()!=null) {
				event.rerouteTo(DashboardNewView.class);
			} else {
				ChangeOrgDialog dialogOrg = new ChangeOrgDialog();
				
				dialogOrg.open();
			}
		} else {
			event.rerouteTo(LoginView.class);
		}
	}
}
