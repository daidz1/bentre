package ws.core.repository;

import java.util.List;

import ws.core.model.Task;
import ws.core.model.UserTaskCount;
import ws.core.model.filter.TaskFilter;

/**
 * The Interface TaskRepositoryCustom.
 */
public interface TaskRepositoryCustom{
	
	/**
	 * Find all.
	 *
	 * @param taskFilter the task filter
	 * @param skip the skip
	 * @param limit the limit
	 * @return the list
	 */
	List<Task> findAll(TaskFilter taskFilter, int skip, int limit);
	
	/**
	 * Count all.
	 *
	 * @param taskFilter the task filter
	 * @return the int
	 */
	int countAll(TaskFilter taskFilter);
	
	/**
	 * Danh sách tài khoản đã giao.
	 *
	 * @param taskFilter the task filter
	 * @return list UserTaskCount
	 */
	List<UserTaskCount> getOwnerList(TaskFilter taskFilter);
	
	/**
	 * Danh sách tài khoản xử lý đã giao
	 *
	 * @param taskFilter the task filter
	 * @return list UserTaskCount
	 */
	List<UserTaskCount> getAssigneeList(TaskFilter taskFilter);
	
	/**
	 * Danh sách tài khoản hỗ trợ giao việc
	 *
	 * @param taskFilter the task filter
	 * @return list UserTaskCount
	 */
	List<UserTaskCount> getAssistantList(TaskFilter taskFilter);
	
	/**
	 * Danh sách tài khoản hỗ trợ đã giao
	 *
	 * @param taskFilter the task filter
	 * @return list UserTaskCount
	 */
	List<UserTaskCount> getSuportList(TaskFilter taskFilter);
	
	List<Task> getListTop(TaskFilter taskFilter, int skip, int limit);
}
