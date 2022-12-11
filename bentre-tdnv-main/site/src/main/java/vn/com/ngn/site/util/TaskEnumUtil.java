package vn.com.ngn.site.util;

import vn.com.ngn.site.enums.TaskAssignmentStatusEnum;
import vn.com.ngn.site.enums.TaskAssignmentTypeEnum;
import vn.com.ngn.site.enums.TaskStatusEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;

public class TaskEnumUtil {
	public static String createKeyByTaskTypeTaskStatus(TaskTypeEnum type, TaskStatusEnum status){
		System.out.println("===createKeyByTaskTypeTaskStatus===");
		return type.getKey()+"_"+status.getKey();
	}

	public static String createKeyByTaskTypeTaskStatusAssignmentTypeAssignmentStatus(TaskTypeEnum type,
			TaskStatusEnum status, TaskAssignmentTypeEnum assignmentType, TaskAssignmentStatusEnum assignmentStatus) {
		System.out.println("===createKeyByTaskTypeTaskStatusAssignmentTypeAssignmentStatus===");
		return type.getKey()+"_"+status.getKey()+"_"+assignmentType.getKey()+"_"+assignmentStatus.getKey();
	}
}
