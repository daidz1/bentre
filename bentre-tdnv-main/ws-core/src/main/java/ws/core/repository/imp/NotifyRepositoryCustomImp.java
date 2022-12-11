package ws.core.repository.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

import ws.core.model.Notify;
import ws.core.model.Task;
import ws.core.model.filter.NotifyFilter;
import ws.core.model.filter.TaskFilter;
import ws.core.repository.NotifyRepositoryCustom;
import ws.core.repository.TaskRepositoryCustom;

@Repository
public class NotifyRepositoryCustomImp implements NotifyRepositoryCustom{

	/** The mongo template. */
	@Autowired
	protected MongoTemplate mongoTemplate;
	
	@Autowired
	protected TaskRepositoryCustom taskRepositoryCustom;
	
	/**
	 * Creates the criteria.
	 *
	 * @param notifyFilter the task filter
	 * @return the list
	 */
	private List<Criteria> createCriteria(NotifyFilter notifyFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		
		/* Tìm theo người tạo thông báo */
		if(notifyFilter.creator!=null && notifyFilter.creator.userId!=null && notifyFilter.creator.organizationId!=null) {
			criteriaList.add(Criteria.where("creator.userId").is(notifyFilter.creator.userId));
			criteriaList.add(Criteria.where("creator.organizationId").is(notifyFilter.creator.organizationId));
		}
		
		/* Tìm theo người nhận thông báo */
		if(notifyFilter.receiver!=null && notifyFilter.receiver.userId!=null && notifyFilter.receiver.organizationId!=null) {
			List<Criteria> orTemCriterias = new ArrayList<>();
			
			List<Criteria> receiverCriterias = new ArrayList<>();
			receiverCriterias.add(Criteria.where("receiver.userId").is(notifyFilter.receiver.userId));
			receiverCriterias.add(Criteria.where("receiver.organizationId").is(notifyFilter.receiver.organizationId));
			orTemCriterias.add(new Criteria().andOperator(receiverCriterias.toArray(new Criteria[receiverCriterias.size()])));
			
			List<Criteria> receiverCriteriasIgnoreOrg = new ArrayList<>();
			receiverCriteriasIgnoreOrg.add(Criteria.where("receiver.userId").is(notifyFilter.receiver.userId));
			receiverCriteriasIgnoreOrg.add(Criteria.where("receiver.organizationId").exists(false));
			orTemCriterias.add(new Criteria().andOperator(receiverCriteriasIgnoreOrg.toArray(new Criteria[receiverCriteriasIgnoreOrg.size()])));
			
			criteriaList.add(new Criteria().orOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()])));
		}
		
		/* Tìm theo khung thời gian giao */
		if(notifyFilter.fromDate>0 && notifyFilter.toDate>0) {
			Date fromDate=new Date(notifyFilter.fromDate);
			Date toDate=new Date(notifyFilter.toDate);
			criteriaList.add(Criteria.where("createdTime").gte(fromDate).lte(toDate));
		}else if(notifyFilter.fromDate>0) {
			Date fromDate=new Date(notifyFilter.fromDate);
			criteriaList.add(Criteria.where("createdTime").gte(fromDate));
		}else if(notifyFilter.toDate>0) {
			Date toDate=new Date(notifyFilter.toDate);
			criteriaList.add(Criteria.where("createdTime").lte(toDate));
		}
		
		/* Tìm với acction exclude */
		if(notifyFilter.excludeActions.size()>0) {
			criteriaList.add(Criteria.where("action").in(notifyFilter.excludeActions));
		}
		
		/* Tìm với acction include */
		if(notifyFilter.includeActions.size()>0) {
			criteriaList.add(Criteria.where("action").in(notifyFilter.includeActions));
		}
		
		/* Tìm đã đọc hay chưa */
		if(notifyFilter.viewed!=null && !notifyFilter.viewed.isEmpty()) {
			if(notifyFilter.viewed.equalsIgnoreCase("true")) {
				criteriaList.add(Criteria.where("viewed").is(true));
			}else if(notifyFilter.viewed.equalsIgnoreCase("false")) {
				criteriaList.add(Criteria.where("viewed").is(false));
			}
		}
		
		/* Tìm với loại */
		if(notifyFilter.taskCategory!=null) {
			TaskFilter taskFilter=new TaskFilter();
			taskFilter.taskCategory=notifyFilter.taskCategory;
			taskFilter.userTask=notifyFilter.receiver;
			
			List<String> taskIds=new ArrayList<String>();
			List<Task> tasks=taskRepositoryCustom.findAll(taskFilter, 0, 0);
			for (Task task : tasks) {
				taskIds.add(task.getId());
				System.out.println(task.getId());
			}
			
			criteriaList.add(Criteria.where("taskId").in(taskIds));
		}
		
		return criteriaList;
	}
	
	@Override
	public int countAll(NotifyFilter taskNotifyFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(taskNotifyFilter);

		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}

		return (int) this.mongoTemplate.count(query, Notify.class);
	}

	@Override
	public List<Notify> findAll(NotifyFilter taskNotifyFilter, int skip, int limit) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(taskNotifyFilter);

		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}

		if(skip>=0 && limit>0) {
			query.skip(skip);
			query.limit(limit);
		}
		query.with(Sort.by(Sort.Direction.DESC, "createdTime"));
		
		return this.mongoTemplate.find(query, Notify.class);
	}

	@Override
	public long setMarkAll(NotifyFilter notifyFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(notifyFilter);

		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		Update update=new Update();
		update.set("viewed", true);
		
		UpdateResult updateResult=mongoTemplate.updateMulti(query, update, Notify.class);
		return updateResult.getModifiedCount();
	}

}
