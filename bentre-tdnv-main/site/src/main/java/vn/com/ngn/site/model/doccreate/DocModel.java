package vn.com.ngn.site.model.doccreate;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class DocModel implements Serializable{
	  private String docCategory="";
	  private int docSecurity=0;
	  private int docNumber=0;
	  private String docSymbol="";
	  private long docRegDate=new Date().getTime();
	  private String docType="";
	  private String docSigner="";
	  private String docOrgReceived="";
	  private String docOrgCreated="";
	  private String docSummary="";
	public String getDocCategory() {
		return docCategory;
	}
	public void setDocCategory(String docCategory) {
		this.docCategory = docCategory;
	}
	public int getDocSecurity() {
		return docSecurity;
	}
	public void setDocSecurity(int docSecurity) {
		this.docSecurity = docSecurity;
	}
	public int getDocNumber() {
		return docNumber;
	}
	public void setDocNumber(int docNumber) {
		this.docNumber = docNumber;
	}
	public String getDocSymbol() {
		return docSymbol;
	}
	public void setDocSymbol(String docSymbol) {
		this.docSymbol = docSymbol;
	}
	public long getDocRegDate() {
		return docRegDate;
	}
	public void setDocRegDate(long docRegDate) {
		this.docRegDate = docRegDate;
	}
	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	public String getDocSigner() {
		return docSigner;
	}
	public void setDocSigner(String docSigner) {
		this.docSigner = docSigner;
	}
	public String getDocOrgReceived() {
		return docOrgReceived;
	}
	public void setDocOrgReceived(String docOrgReceived) {
		this.docOrgReceived = docOrgReceived;
	}
	public String getDocOrgCreated() {
		return docOrgCreated;
	}
	public void setDocOrgCreated(String docOrgCreated) {
		this.docOrgCreated = docOrgCreated;
	}
	public String getDocSummary() {
		return docSummary;
	}
	public void setDocSummary(String docSummary) {
		this.docSummary = docSummary;
	}
	  
}
