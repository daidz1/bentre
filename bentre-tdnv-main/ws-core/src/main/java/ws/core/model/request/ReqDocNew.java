package ws.core.model.request;

import java.util.LinkedList;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Field;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ws.core.model.UserOrganizationCreator;

@Getter
@Setter
@ToString
public class ReqDocNew {
	@Schema(name = "docCategory", description = "Loại văn bản đến hoặc đi [FrOfficialIn, FrOfficialOut]", required = true, example = "FrOfficialOut")
	@NotNull(message = "docCategory không được trống")
	@Field(value = "docCategory")
	public Object docCategory;
	
	@Schema(name = "docSecurity", description = "Độ mật văn bản", required = true, example = "0")
	@NotNull(message = "docSecurity không được trống")
	@Field(value = "docSecurity")
	public Object docSecurity;
	
	@Schema(name = "docNumber", description = "Số hiệu văn bản", required = true, example = "1245")
	@NotNull(message = "docNumber không được trống")
	@Field(value = "docNumber")
	public Object docNumber;
	
	@Schema(name = "docSymbol", description = "Ký hiệu văn bản", required = true, example = "UBND/VB")
	@NotNull(message = "docSymbol không được trống")
	@Field(value = "docSymbol")
	public Object docSymbol;
	
	@Schema(name = "docRegDate", description = "Ngày ban hành văn bản", required = true, example = "1657081498024")
	@NotNull(message = "docRegDate không được trống")
	@Field(value = "docRegDate")
	public long docRegDate;
	
	@Schema(name = "docType", description = "Thể loại văn bản", required = true, example = "Hành chính")
	@NotNull(message = "docType không được trống")
	@Field(value = "docType")
	public Object docType;
	
	@Schema(name = "docSigner", description = "Người ký văn bản", required = true, example = "Cao Văn Trọng")
	@NotNull(message = "docSigner không được trống")
	@Field(value = "docSigner")
	public Object docSigner;
	
	@Schema(name = "docOrgReceived", description = "Nơi nhận văn bản", required = false, example = "Trung tâm thông tin điện tử Bến Tre")
	@Field(value = "docOrgReceived")
	public Object docOrgReceived;
	
	@Schema(name = "docOrgCreated", description = "Tên đơn vị ban hành văn bản", required = true, example = "UBND Tỉnh Bến Tre")
	@NotNull(message = "docOrgCreated không được trống")
	@Field(value = "docOrgCreated")
	public Object docOrgCreated;
	
	@Schema(name = "docSummary", description = "Trích yếu", required = true, example = "V/v xây dựng hệ thống quản lý nhiệm vụ")
	@NotNull(message = "docSummary không được trống")
	@Field(value = "docSummary")
	public Object docSummary;
	
	@Schema(name = "docCreator", description = "Người tạo - Đơn vị tạo", required = true)
	@NotNull(message = "docCreator không được trống")
	@Field(value = "docCreator")
	public UserOrganizationCreator docCreator;
	
	@Field(value = "docAttachments")
	public LinkedList<ReqDocAttachment> docAttachments;
	
	public ReqDocNew() {
		this.docCategory=null;
		this.docSecurity=null;
		this.docNumber=null;
		this.docSymbol=null;
		this.docRegDate=0;
		this.docType=null;
		this.docSigner=null;
		this.docOrgReceived=null;
		this.docOrgCreated=null;
		this.docSummary=null;
		this.docAttachments=new LinkedList<ReqDocAttachment>();
		this.docCreator=new UserOrganizationCreator();
	}
}
