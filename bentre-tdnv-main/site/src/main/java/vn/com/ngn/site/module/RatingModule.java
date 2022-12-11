package vn.com.ngn.site.module;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.LayoutInterface;

@SuppressWarnings("serial")
@CssImport("/themes/site/components/rating.css")
public class RatingModule extends VerticalLayout implements LayoutInterface{
	private HorizontalLayout hStart = new HorizontalLayout();
	private Icon[] arrStar = new Icon[5];
	private int star = 1;
	
	public RatingModule() {
		buildLayout();
		configComponent();
		
		setStar(1);
	}
	
	@Override
	public void buildLayout() {
		this.add(hStart);
	}

	@Override
	public void configComponent() {
		for(int i = 0 ; i < 5 ; i++) {
			Icon iconStar = VaadinIcon.STAR.create();
			iconStar.addClassName("star-unselect");
			
			arrStar[i] = iconStar;
			
			hStart.add(iconStar);
			
			int index = i;
			int starValue = index+1;
			iconStar.addClickListener(e->{
				setStar(starValue);
			});
		}
	}
	
	public void setStar(int starInput) {
		this.star = starInput;
		
		int index = star - 1;
		
		for(int i = 0 ; i < arrStar.length ; i++) {
			if(i<=index) {
				arrStar[i].addClassName("star-selected");
			} else {
				arrStar[i].removeClassName("star-selected");
			}
		}
	}
	
	public int getStar() {
		return star;
	}
}
