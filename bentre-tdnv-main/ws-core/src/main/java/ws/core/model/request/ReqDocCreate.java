package ws.core.model.request;

import java.util.LinkedList;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReqDocCreate {
	@NotNull(message = "docCategory không được trống")
	@Field(value = "docCategory")
	public Object docCategory;
	
	@NotNull(message = "docFrom không được trống")
	@Field(value = "docFrom")
	public Object docFrom;
	
	@NotNull(message = "norNameBoss không được trống")
	@Field(value = "norNameBoss")
	public Object norNameBoss;
	
	@Field(value="norNameG3")
	public LinkedList<String> norNameG3;
	
	@NotNull(message = "docRegCode không được trống")
	@Field(value = "docRegCode")
	public Object docRegCode;
	
	@NotNull(message = "docSecurity không được trống")
	@Field(value = "docSecurity")
	public Object docSecurity;
	
	@NotNull(message = "docCategory không được trống")
	@Field(value = "docNumber")
	public Object docNumber;
	
	@NotNull(message = "docSymbol không được trống")
	@Field(value = "docSymbol")
	public Object docSymbol;
	
	@NotNull(message = "docSignal không được trống")
	@Field(value = "docSignal")
	public Object docSignal;
	
	@NotNull(message = "docDate không được trống")
	@Field(value = "docDate")
	public long docDate;
	
	@NotNull(message = "docRegDate không được trống")
	@Field(value = "docRegDate")
	public long docRegDate;
	
	@NotNull(message = "docType không được trống")
	@Field(value = "docType")
	public Object docType;
	
	@NotNull(message = "docSigner không được trống")
	@Field(value = "docSigner")
	public Object docSigner;
	
	@NotNull(message = "docCopies không được trống")
	@Field(value = "docCopies")
	public Object docCopies;
	
	@NotNull(message = "docPages không được trống")
	@Field(value = "docPages")
	public Object docPages;
	
	@Field(value = "docOrgReceived")
	public Object docOrgReceived;
	
	@Field(value = "docOrgCreated")
	public Object docOrgCreated;
	
	@NotNull(message = "docSummary không được trống")
	@Field(value = "docSummary")
	public Object docSummary;
	
	@Field(value = "docAttachments")
	public LinkedList<ReqDocAttachment> docAttachments;
	
	public ReqDocCreate() {
		this.docCategory=null;
		this.docFrom=null;
		this.norNameBoss=null;
		this.norNameG3=new LinkedList<String>();
		this.docRegCode=null;
		this.docSecurity=null;
		this.docNumber=null;
		this.docSymbol=null;
		this.docSignal=null;
		this.docDate=0;
		this.docRegDate=0;
		this.docType=null;
		this.docSigner=null;
		this.docCopies=0;
		this.docPages=0;
		this.docOrgReceived=null;
		this.docOrgCreated=null;
		this.docSummary=null;
		this.docAttachments=new LinkedList<ReqDocAttachment>();
	}
}
