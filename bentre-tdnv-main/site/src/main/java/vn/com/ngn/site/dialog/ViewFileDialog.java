package vn.com.ngn.site.dialog;

import org.vaadin.alejandro.PdfBrowserViewer;

import com.vaadin.flow.server.StreamResource;

@SuppressWarnings("serial")
public class ViewFileDialog extends DialogTemplate {
	private String fileName = "";
	private StreamResource streamResource;
	
	public ViewFileDialog(String fileName,StreamResource streamResource) {
		this.fileName = fileName;
		this.streamResource = streamResource;
		
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		caption.setText(fileName);
		
		vMain.setWidthFull();
		vMain.setHeight("95%");
		this.setHeight("90%");
		this.setWidth("90%");
	}
	
	@Override
	public void configComponent() {
		super.configComponent();
		
		if(fileName.endsWith(".pdf")) {
			PdfBrowserViewer viewer = new PdfBrowserViewer(streamResource);
			viewer.setHeight("100%");
			
			vMain.add(viewer);
		}
	}
}
