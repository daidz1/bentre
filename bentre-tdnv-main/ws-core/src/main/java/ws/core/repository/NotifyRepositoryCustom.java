package ws.core.repository;

import java.util.List;

import ws.core.model.Notify;
import ws.core.model.filter.NotifyFilter;

// TODO: Auto-generated Javadoc
/**
 * The Interface TaskNotifyRepositoryCustom.
 */
public interface NotifyRepositoryCustom{
	
	/**
	 * Count all.
	 *
	 * @param taskNotifyFilter the task notify filter
	 * @return the list
	 */
	int countAll(NotifyFilter taskNotifyFilter);
	
	/**
	 * Find all.
	 *
	 * @param taskNotifyFilter the task notify filter
	 * @param skip the skip
	 * @param limit the limit
	 * @return the list
	 */
	List<Notify> findAll(NotifyFilter taskNotifyFilter, int skip, int limit);
	
	/**
	 * Sets the mark all.
	 *
	 * @param notifyFilter the notify filter
	 * @return the long
	 */
	long setMarkAll(NotifyFilter notifyFilter);
}
