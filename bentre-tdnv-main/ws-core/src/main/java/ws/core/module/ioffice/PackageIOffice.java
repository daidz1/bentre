package ws.core.module.ioffice;

import java.util.List;

public class PackageIOffice {
	private String idPackage=null;
	private boolean isAttachment = false;
	private List<VanbanDKModel> docAttachments;
	private VanbanModel doc;
	
	public String getIdGoiTin() {
		return idPackage;
	}
	public void setIdGoiTin(String idGoiTin) {
		this.idPackage = idGoiTin;
	}
	public boolean isAttachment() {
		return isAttachment;
	}
	public void setAttachment(boolean isAttachment) {
		this.isAttachment = isAttachment;
	}
	public List<VanbanDKModel> getModelVanBanDinhKem() {
		return docAttachments;
	}
	public void setModelVanBanDinhKem(List<VanbanDKModel> modelVanBanDinhKem) {
		this.docAttachments = modelVanBanDinhKem;
	}
	public VanbanModel getModelVanBanPhatHanh() {
		return doc;
	}
	public void setModelVanBanPhatHanh(VanbanModel modelVanBanPhatHanh) {
		this.doc = modelVanBanPhatHanh;
	}
}
