package ws.core.model;

import java.util.Date;
import java.util.LinkedList;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Document(collection = "doc")
public class Doc {
	
	/** The created time. */
	@Indexed
	@Field(value = "createdTime")
	public Date createdTime;
	
	/** The updated time. */
	@Indexed
	@Field(value = "updatedTime")
	public Date updatedTime;
	
	/** Id của văn bản. */
	@Id
	@Field(value = "_id")
	public ObjectId _id;
	
	/** Loại văn bản Đến - Đi. */
	@Indexed
	@Field(value = "docCategory")
	public Object docCategory;
	
	/** Văn bản từ. */
	@Indexed
	@Field(value = "docFrom")
	public Object docFrom;
	
	/** The nor name boss. */
	/*@Indexed
	@Field(value = "norNameBoss")
	public Object norNameBoss;*/
	
	/** The nor name G 3. */
	/*@Indexed
	@Field(value="norNameG3")
	public LinkedList<String> norNameG3;*/
	
	/** The doc reg code. */
	@Indexed
	@Field(value = "docRegCode")
	public Object docRegCode;
	
	/** The doc security. */
	@Indexed
	@Field(value = "docSecurity")
	public Object docSecurity;
	
	/** The doc number. */
	@Indexed
	@Field(value = "docNumber")
	public Object docNumber;
	
	/** The doc symbol. */
	@Indexed
	@Field(value = "docSymbol")
	public Object docSymbol;
	
	/** The doc signal. */
	@Indexed
	@Field(value = "docSignal")
	public Object docSignal;
	
	/** The doc date. */
	@Indexed
	@Field(value = "docDate")
	public Date docDate;
	
	/** The doc reg date. */
	@Indexed
	@Field(value = "docRegDate")
	public Date docRegDate;
	
	/** The doc type. */
	@Indexed
	@Field(value = "docType")
	public Object docType;
	
	/** The doc signer. */
	@Indexed
	@Field(value = "docSigner")
	public Object docSigner;
	
	/** The doc signer position. */
	@Indexed
	@Field(value = "docSignerPosition")
	public Object docSignerPosition;
	
	/** The doc copies. */
	@Indexed
	@Field(value = "docCopies")
	public Object docCopies;
	
	/** The doc pages. */
	@Indexed
	@Field(value = "docPages")
	public Object docPages;
	
	/** The doc org received. */
	@Indexed
	@Field(value = "docOrgReceived")
	public Object docOrgReceived;
	
	/** The doc org created. */
	@Indexed
	@Field(value = "docOrgCreated")
	public Object docOrgCreated;
	
	/** The doc summary. */
	@Indexed
	@Field(value = "docSummary")
	public Object docSummary;
	
	/** The doc attachments. */
	@Indexed
	@Field(value = "docAttachments")
	public LinkedList<DocAttachment> docAttachments;
	
	/** The active. */
	@Indexed
	@Field(value = "active")
	public boolean active;
	
	/** The creator id. */
	@Indexed
	@Field(value = "docCreator")
	public UserOrganizationCreator docCreator;
	
	/** The id package. */
	@Indexed
	@Field(value = "idPackage")
	public String idPackage;
	
	/** The id I office. */
	@Indexed
	@Field(value = "idIOffice")
	public String idIOffice;
	
	/**
	 * Instantiates a new doc.
	 */
	public Doc() {
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this._id=new ObjectId();
		
		this.docCategory=null;
		this.docFrom=null;
		/*this.norNameBoss=null;
		this.norNameG3=new LinkedList<String>();*/
		this.docRegCode=null;
		this.docSecurity=null;
		this.docNumber=null;
		this.docSymbol=null;
		this.docSignal=null;
		this.docDate=null;
		this.docRegDate=null;
		this.docType=null;
		this.docSigner=null;
		this.docCopies=1;
		this.docPages=1;
		this.docOrgReceived=null;
		this.docOrgCreated=null;
		this.docSummary=null;
		this.docAttachments=new LinkedList<DocAttachment>();
		this.active=true;
		this.docCreator=null;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return _id.toHexString();
	}
	
	/**
	 * Gets the created time.
	 *
	 * @return the created time
	 */
	public long getCreatedTime() {
		return this.createdTime.getTime();
	}
	
	/**
	 * Gets the updated time.
	 *
	 * @return the updated time
	 */
	public long getUpdatedTime() {
		return this.updatedTime.getTime();
	}
}
