package ws.core.model.request;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Field;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ws.core.enums.TaskAssignmentType;
import ws.core.model.Attachment;
import ws.core.model.UserOrganization;

@Getter
@Setter
@ToString
public class ReqTaskCreate {
	@Schema(name = "ownerTask", description = "Object user-organization người giao nhiệm vụ", required = true)
	@NotNull(message = "ownerTask không được trống")
	@Field(value = "ownerTask")
	public UserOrganization ownerTask;
	
	@Schema(name = "assistantTask", description = "Object user-organization người giao thay nhiệm vụ", required = false)
	@Field(value = "assistantTask")
	public UserOrganization assistantTask;
	
	@Schema(name = "assigneeTask", description = "Object user-organization người xử lý", required = true)
	@NotNull(message = "assigneeTask không được trống")
	@Field(value = "assigneeTask")
	public UserOrganization assigneeTask;
	
	@Schema(name = "addFollowersTask", description = "Danh sách object user-organization người theo dõi (hỗ trợ)", required = false)
	@Field(value="addFollowersTask")
	public LinkedList<UserOrganization> addFollowersTask;
	
	@Schema(name = "title", description = "Tiêu đề", required = true, example = "Giao nhiệm vụ A")
	@NotBlank(message = "title không được trống")
	@Field(value = "title")
	public String title;
	
	@Schema(name = "description", description = "Mô tả", required = true, example = "Mô tả nhiệm vụ A")
	@NotBlank(message = "description không được trống")
	@Field(value = "description")
	public String description;
	
	@Schema(name = "priority", description = "Độ khẩn", required = false, example = "1")
	@Field(value = "priority")
	public int priority;
	
	@Schema(name = "endTime", description = "Hạn xử lý", required = false, example = "0")
	@Field(value = "endTime")
	public long endTime;
	
	@Schema(name = "parentId", description = "ID nhiệm vụ cha", required = false)
	@Field(value = "parentId")
	public String parentId;
	
	@Schema(name = "docId", description = "ID văn bản", required = false)
	@Field(value = "docId")
	public String docId;
	
	@Schema(name = "addAttachments", description = "Thêm danh sách đính kèm", required = false)
	@Field(value="addAttachments")
	public List<Attachment> addAttachments;
	
	@Schema(name = "assignmentType", description = "Loại nhiệm vụ", required = false)
	@NotBlank(message = "assignmentType không được trống")
	@Field(value = "assignmentType")
	public String assignmentType;
	
	public ReqTaskCreate() {
		this.assistantTask=null;
		this.addFollowersTask=new LinkedList<UserOrganization>();
		this.endTime=0;
		this.priority=1;
		this.parentId="";
		this.addAttachments=new ArrayList<Attachment>();
		this.assignmentType=TaskAssignmentType.User.getKey();
	}
}
