package ws.core.repository.imp;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.AggregationSpELExpression;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import ws.core.model.Task;
import ws.core.model.UserTaskCount;
import ws.core.model.filter.TaskFilter;
import ws.core.repository.TaskRepositoryCustom;

/**
 * The Class TaskRepositoryCustomImp.
 */
@Repository
public class TaskRepositoryCustomImp implements TaskRepositoryCustom{
	
	/** The mongo template. */
	@Autowired
	protected MongoTemplate mongoTemplate;

	/**
	 * Creates the criteria.
	 *
	 * @param taskFilter the task filter
	 * @return the list
	 */
	private List<Criteria> createCriteria(TaskFilter taskFilter){
		List<Criteria> criteriaList = new ArrayList<>();

		/* Tìm task _id */
		if(taskFilter._id!=null) {
			criteriaList.add(Criteria.where("_id").is(new ObjectId(taskFilter._id)));
		}

		/* Tìm theo độ khẩn */
		if(taskFilter.priority>0) {
			criteriaList.add(Criteria.where("priority").is(taskFilter.priority));
		}
		
		/* Tìm theo khung thời gian giao */
		if(taskFilter.fromDate>0 && taskFilter.toDate>0) {
			Date fromDate=new Date(taskFilter.fromDate);
			Date toDate=new Date(taskFilter.toDate);
			criteriaList.add(Criteria.where("createdTime").gte(fromDate).lte(toDate));
		}else if(taskFilter.fromDate>0) {
			Date fromDate=new Date(taskFilter.fromDate);
			criteriaList.add(Criteria.where("createdTime").gte(fromDate));
		}else if(taskFilter.toDate>0) {
			Date toDate=new Date(taskFilter.toDate);
			criteriaList.add(Criteria.where("createdTime").lte(toDate));
		}
		
		/* Tìm theo khung thời gian hoàn thành */
		if(taskFilter.completedFromDate>0 && taskFilter.completedToDate>0) {
			Date fromDate=new Date(taskFilter.completedFromDate);
			Date toDate=new Date(taskFilter.completedToDate);
			criteriaList.add(Criteria.where("completedTime").exists(true));
			criteriaList.add(Criteria.where("completedTime").gte(fromDate).lte(toDate));
		}else if(taskFilter.completedFromDate>0) {
			Date fromDate=new Date(taskFilter.completedFromDate);
			criteriaList.add(Criteria.where("completedTime").exists(true));
			criteriaList.add(Criteria.where("completedTime").gte(fromDate));
		}else if(taskFilter.completedToDate>0) {
			Date toDate=new Date(taskFilter.completedToDate);
			criteriaList.add(Criteria.where("completedTime").exists(true));
			criteriaList.add(Criteria.where("completedTime").lte(toDate));
		}
		
		/* Tìm nhiệm vụ sắp quá hạn, hoặc đã quá hạn */
		if(taskFilter.soonExpireDate>0) {
			criteriaList.add(Criteria.where("completedTime").exists(false));
			
			criteriaList.add(Criteria.where("notifySoonExpire").is(false));
			criteriaList.add(Criteria.where("endTime").exists(true));
			criteriaList.add(Criteria.where("endTime").lt(new Date(taskFilter.soonExpireDate)));
			criteriaList.add(Criteria.where("endTime").gte(new Date()));
			
		}else if(taskFilter.hadExpireDate>0) {
			criteriaList.add(Criteria.where("completedTime").exists(false));
			
			criteriaList.add(Criteria.where("notifyHadExpire").is(false));
			criteriaList.add(Criteria.where("endTime").exists(true));
			criteriaList.add(Criteria.where("endTime").lt(new Date(taskFilter.hadExpireDate)));
		}
		
		/* Tìm theo loại giao nhiệm vụ */
		if(taskFilter.taskAssignmentType!=null) {
			criteriaList.add(Criteria.where("assignmentType").is(taskFilter.taskAssignmentType.getKey()));
		}
		
		/* Tìm theo category để lọc theo nhiệm vụ, nhiệm vụ đơn vị (chưa phân, đã phân), nhiệm vụ cá nhân (cả đơn vị)*/
		if(taskFilter.taskCategory!=null && taskFilter.userTask!=null) {
			switch (taskFilter.taskCategory) {
				case DAGIAO:{
					if(taskFilter.userTask.validUserId()) {
						criteriaList.add(Criteria.where("ownerTask.userId").is(taskFilter.userTask.userId));
					}
					
					if(taskFilter.userTask.validOrganizationId()) {
						criteriaList.add(Criteria.where("ownerTask.organizationId").is(taskFilter.userTask.organizationId));
					}
					
					break;
				}
				case DUOCGIAO:{
					if(taskFilter.userTask.validOrganizationId()) {
						criteriaList.add(Criteria.where("assigneeTask.organizationId").is(taskFilter.userTask.organizationId));
					}
					
					/* Tìm theo phân loại nhiệm vụ đã giao cho đơn vị xử lý chính */
					if(taskFilter.taskAssignmentStatus!=null) {
						switch (taskFilter.taskAssignmentStatus) {
							case CHUAPHAN_CANBO:
								criteriaList.add(Criteria.where("assigneeTask.userId").exists(false));
								
								break;

							case DAPHAN_CANBO:
								criteriaList.add(Criteria.where("assigneeTask.userId").exists(true));
								break;
						}
					}
					/* Đặc biệt, ngược lại, thì sẽ tìm theo userId nếu có */
					else if(taskFilter.userTask.validUserId()) {
						criteriaList.add(Criteria.where("assigneeTask.userId").is(taskFilter.userTask.userId));
					}
					break;
				}
				case THEODOI:{
					List<Criteria> andElemMatchFollowers=new ArrayList<Criteria>();
					
					if(taskFilter.userTask.validOrganizationId()) {
						andElemMatchFollowers.add(Criteria.where("organizationId").is(taskFilter.userTask.organizationId));
					}

					/* Tìm theo phân loại nhiệm vụ đã giao cho đơn vị xử lý chính */
					if(taskFilter.taskAssignmentStatus!=null) {
						switch (taskFilter.taskAssignmentStatus) {
							case CHUAPHAN_CANBO:
								andElemMatchFollowers.add(Criteria.where("userId").exists(false));
								break;
							case DAPHAN_CANBO:
								andElemMatchFollowers.add(Criteria.where("userId").exists(true));
								break;
						}
					}
					/* Đặc biệt, ngược lại sẽ tìm userId nếu gán */
					else if(taskFilter.userTask.validUserId()) {
						andElemMatchFollowers.add(Criteria.where("userId").is(taskFilter.userTask.userId));
					}
					
					/* Phải tìm duyệt qua từng phần tử trong mảng để tìm chính xác item với elemMatch*/
					criteriaList.add(Criteria.where("followersTask").elemMatch(new Criteria().andOperator(andElemMatchFollowers.toArray(new Criteria[andElemMatchFollowers.size()]))));
					break;
				}
				case GIAOVIECTHAY:{
					if(taskFilter.userTask.validUserId()) {
						criteriaList.add(Criteria.where("assistantTask.userId").is(taskFilter.userTask.userId));
					}
					
					if(taskFilter.userTask.validOrganizationId()) {
						criteriaList.add(Criteria.where("assistantTask.organizationId").is(taskFilter.userTask.organizationId));
					}
					
					break;
				}
				case THEODOITHAY:{
					if(taskFilter.userTask.validUserId()) {
						criteriaList.add(Criteria.where("ownerTask.userId").is(taskFilter.userTask.userId));
					}
					
					if(taskFilter.userTask.validOrganizationId()) {
						criteriaList.add(Criteria.where("ownerTask.organizationId").is(taskFilter.userTask.organizationId));
					}
					
					criteriaList.add(Criteria.where("assistantTask").exists(true));
					criteriaList.add(Criteria.where("assistantTask.userId").exists(true));
					criteriaList.add(Criteria.where("assistantTask.organizationId").exists(true));
					break;
				}
			}
		}else if(taskFilter.taskCategory==null && taskFilter.userTask!=null && taskFilter.userTask.userId!=null && taskFilter.userTask.organizationId!=null) {
			List<Criteria> orTemCriterias = new ArrayList<>();
			
			List<Criteria> ownerTaskCriterias = new ArrayList<>();
			ownerTaskCriterias.add(Criteria.where("ownerTask.userId").is(taskFilter.userTask.userId));
			ownerTaskCriterias.add(Criteria.where("ownerTask.organizationId").is(taskFilter.userTask.organizationId));
			orTemCriterias.add(new Criteria().andOperator(ownerTaskCriterias.toArray(new Criteria[ownerTaskCriterias.size()])));
			
			List<Criteria> assigneeTaskCriterias = new ArrayList<>();
			assigneeTaskCriterias.add(Criteria.where("assigneeTask.userId").is(taskFilter.userTask.userId));
			assigneeTaskCriterias.add(Criteria.where("assigneeTask.organizationId").is(taskFilter.userTask.organizationId));
			orTemCriterias.add(new Criteria().andOperator(assigneeTaskCriterias.toArray(new Criteria[assigneeTaskCriterias.size()])));
			
			List<Criteria> assistantTaskCriterias = new ArrayList<>();
			assistantTaskCriterias.add(Criteria.where("assistantTask.userId").is(taskFilter.userTask.userId));
			assistantTaskCriterias.add(Criteria.where("assistantTask.organizationId").is(taskFilter.userTask.organizationId));
			orTemCriterias.add(new Criteria().andOperator(assistantTaskCriterias.toArray(new Criteria[assistantTaskCriterias.size()])));
			
			List<Criteria> followerTaskCriterias = new ArrayList<>();
			followerTaskCriterias.add(Criteria.where("followersTask.userId").is(taskFilter.userTask.userId));
			followerTaskCriterias.add(Criteria.where("followersTask.organizationId").is(taskFilter.userTask.organizationId));
			orTemCriterias.add(new Criteria().andOperator(followerTaskCriterias.toArray(new Criteria[followerTaskCriterias.size()])));
			
			criteriaList.add(new Criteria().orOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()])));
		}

		/* Tìm theo subcategory */
		if(taskFilter.taskSubCategory!=null) {
			switch (taskFilter.taskSubCategory) {
			case CHUATHUCHIEN:
				criteriaList.add(Criteria.where("acceptedTime").exists(false));
				criteriaList.add(Criteria.where("completedTime").exists(false));
				break;
			case CHUAHOANTHANH:
				criteriaList.add(Criteria.where("completedTime").exists(false));
				break;
			case CHUAHOANTHANH_TRONGHAN:
				criteriaList.add(Criteria.where("completedTime").exists(false));
				criteriaList.add(Criteria.where("endTime").exists(true));
				criteriaList.add(Criteria.where("endTime").gte(new Date()));
				break;
			case CHUAHOANTHANH_QUAHAN:
				criteriaList.add(Criteria.where("completedTime").exists(false));
				criteriaList.add(Criteria.where("endTime").exists(true));
				criteriaList.add(Criteria.where("endTime").lt(new Date()));
				break;
			case CHUAHOANTHANH_KHONGHAN:
				criteriaList.add(Criteria.where("completedTime").exists(false));
				criteriaList.add(Criteria.where("endTime").exists(false));
				break;
			case DAHOANTHANH:
				criteriaList.add(Criteria.where("completedTime").exists(true));
				break;
			case DAHOANTHANH_TRONGHAN:
				criteriaList.add(Criteria.where("completedTime").exists(true));
				criteriaList.add(Criteria.where("endTime").exists(true));
				break;
			case DAHOANTHANH_QUAHAN:
				criteriaList.add(Criteria.where("completedTime").exists(true));
				criteriaList.add(Criteria.where("endTime").exists(true));
				break;
			case DAHOANTHANH_KHONGHAN:
				criteriaList.add(Criteria.where("completedTime").exists(true));
				criteriaList.add(Criteria.where("endTime").exists(false));
				break;
			}
		}
		
		/* Tìm từ khóa tìm kiếm */
		if(taskFilter.keySearch!=null && !taskFilter.keySearch.isEmpty()) {
			List<Criteria> subCriteriasOr = new ArrayList<>();
			
			List<String> listFields=Arrays.asList("title","description");
			for (String fieldName : listFields) {
				subCriteriasOr.add(Criteria.where(fieldName).regex(".*"+taskFilter.keySearch+".*", "i"));
			}
			criteriaList.add(new Criteria().orOperator(subCriteriasOr.toArray(new Criteria[subCriteriasOr.size()])));
		}

		/* Tìm findOwners */
		if(taskFilter.findOwners!=null && taskFilter.findOwners.isEmpty()==false) {
			List<Criteria> orTemCriterias = new ArrayList<>();
			String[] ids=taskFilter.findOwners.split(",");
			for (String id : ids) {
				if(id.contains("-")  && id.split("-").length==2) {
					String[] userTask=id.split("-");
					List<Criteria> iTemCriterias = new ArrayList<>();
					iTemCriterias.add(Criteria.where("ownerTask.userId").is(userTask[0]));
					iTemCriterias.add(Criteria.where("ownerTask.organizationId").is(userTask[1]));
					orTemCriterias.add(new Criteria().andOperator(iTemCriterias.toArray(new Criteria[iTemCriterias.size()])));
				}else {
					orTemCriterias.add(Criteria.where("ownerTask.organizationId").is(id));
				}
			}
			if(orTemCriterias.size()>0) {
				criteriaList.add(new Criteria().orOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()])));
			}
		}
		
		/* Tìm findAssistants */
		if(taskFilter.findAssistants!=null && taskFilter.findAssistants.isEmpty()==false) {
			List<Criteria> orTemCriterias = new ArrayList<>();
			String[] ids=taskFilter.findAssistants.split(",");
			for (String id : ids) {
				if(id.contains("-")  && id.split("-").length==2) {
					String[] userTask=id.split("-");
					List<Criteria> iTemCriterias = new ArrayList<>();
					iTemCriterias.add(Criteria.where("assistantTask.userId").is(userTask[0]));
					iTemCriterias.add(Criteria.where("assistantTask.organizationId").is(userTask[1]));
					orTemCriterias.add(new Criteria().andOperator(iTemCriterias.toArray(new Criteria[iTemCriterias.size()])));
				}else {
					orTemCriterias.add(Criteria.where("assistantTask.organizationId").is(id));
				}
			}
			if(orTemCriterias.size()>0) {
				criteriaList.add(new Criteria().orOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()])));
			}
		}
		
		/* Tìm findAssignees */
		if(taskFilter.findAssignees!=null && taskFilter.findAssignees.isEmpty()==false) {
			List<Criteria> orTemCriterias = new ArrayList<>();
			String[] ids=taskFilter.findAssignees.split(",");
			for (String id : ids) {
				if(id.contains("-")  && id.split("-").length==2) {
					String[] userTask=id.split("-");
					List<Criteria> iTemCriterias = new ArrayList<>();
					iTemCriterias.add(Criteria.where("assigneeTask.userId").is(userTask[0]));
					iTemCriterias.add(Criteria.where("assigneeTask.organizationId").is(userTask[1]));
					orTemCriterias.add(new Criteria().andOperator(iTemCriterias.toArray(new Criteria[iTemCriterias.size()])));
				}else {
					orTemCriterias.add(Criteria.where("assigneeTask.organizationId").is(id));
				}
			}
			if(orTemCriterias.size()>0) {
				criteriaList.add(new Criteria().orOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()])));
			}
		}
		
		/* Tìm findFollowers */
		if(taskFilter.findFollowers!=null && taskFilter.findFollowers.isEmpty()==false) {
			List<Criteria> orTemCriterias = new ArrayList<>();
			String[] ids=taskFilter.findFollowers.split(",");
			for (String id : ids) {
				if(id.contains("-") && id.split("-").length==2) {
					String[] userTask=id.split("-");
					List<Criteria> iTemCriterias = new ArrayList<>();
					iTemCriterias.add(Criteria.where("followersTask.userId").is(userTask[0]));
					iTemCriterias.add(Criteria.where("followersTask.organizationId").is(userTask[1]));
					orTemCriterias.add(new Criteria().andOperator(iTemCriterias.toArray(new Criteria[iTemCriterias.size()])));
				}else {
					orTemCriterias.add(Criteria.where("followersTask.organizationId").is(id));
				}
			}
			if(orTemCriterias.size()>0) {
				criteriaList.add(new Criteria().orOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()])));
			}
		}
		
		/* Tìm parentId */
		if(taskFilter.parentId!=null) {
			criteriaList.add(Criteria.where("parentId").is(taskFilter.parentId));
		}
		
		/* Tìm docId */
		if(taskFilter.docId!=null) {
			criteriaList.add(Criteria.where("docId").is(taskFilter.docId));
		}
		
		return criteriaList;
	}

	/**
	 * Find all.
	 *
	 * @param taskFilter the task filter
	 * @param skip the skip
	 * @param limit the limit
	 * @return the list
	 */
	@SuppressWarnings({ "incomplete-switch", "deprecation" })
	@Override
	public List<Task> findAll(TaskFilter taskFilter, int skip, int limit) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(taskFilter);
		
		for (Criteria criteria : criteriaList) {
			System.out.println("+ "+criteria.getKey()+": "+criteria.getCriteriaObject().toJson());
		}
		
		ProjectionOperation projectStage = Aggregation.project(getListDeclaredFields());
		if(taskFilter.taskSubCategory!=null) {
			switch (taskFilter.taskSubCategory) {
			case DAHOANTHANH_QUAHAN:
				projectStage = Aggregation.project(getListDeclaredFields()).and(AggregationSpELExpression.expressionOf("cond(completedTime > endTime, 1, 0)", "")).as("sosanh");
				criteriaList.add(Criteria.where("sosanh").is(1));
				break;
			case DAHOANTHANH_TRONGHAN:
				projectStage = Aggregation.project(getListDeclaredFields()).and(AggregationSpELExpression.expressionOf("cond(completedTime <= endTime , 1, 0)", "")).as("sosanh");
				criteriaList.add(Criteria.where("sosanh").is(1));
				break;
			}
		}
		
		MatchOperation matchStage = Aggregation.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.DESC, "_id"));
		
		SkipOperation skipOperation = Aggregation.skip(0);
		LimitOperation limitOperation =Aggregation.limit(1000000);
		if(skip>=0 && limit>0) {
			skipOperation = Aggregation.skip(skip);
			limitOperation = Aggregation.limit(limit);
		}
		
		Aggregation aggregation = Aggregation.newAggregation(projectStage, matchStage, sortOperation, skipOperation, limitOperation);
		AggregationResults<Task> output = mongoTemplate.aggregate(aggregation, Task.class, Task.class);
		return output.getMappedResults();
	}

	/**
	 * Count all.
	 *
	 * @param taskFilter the task filter
	 * @return the int
	 */
	@SuppressWarnings({ "incomplete-switch" })
	@Override
	public int countAll(TaskFilter taskFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(taskFilter);
		
		for (Criteria criteria : criteriaList) {
			System.out.println("+ "+criteria.getKey()+": "+criteria.getCriteriaObject().toJson());
		}
		
		ProjectionOperation projectStage = Aggregation.project(Task.class);
		if(taskFilter.taskSubCategory!=null) {
			switch (taskFilter.taskSubCategory) {
			case DAHOANTHANH_QUAHAN:
				projectStage = Aggregation.project(Task.class).and(AggregationSpELExpression.expressionOf("cond(completedTime > endTime, 1, 0)", "")).as("sosanh");
				criteriaList.add(Criteria.where("sosanh").is(1));
				break;
			case DAHOANTHANH_TRONGHAN:
				projectStage = Aggregation.project(Task.class).and(AggregationSpELExpression.expressionOf("cond(completedTime <= endTime , 1, 0)", "")).as("sosanh");
				criteriaList.add(Criteria.where("sosanh").is(1));
				break;
			}
		}
		MatchOperation matchStage = Aggregation.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		GroupOperation groupByDateAndSum = Aggregation.group().count().as("countTask");
		
		Aggregation aggregation = Aggregation.newAggregation(projectStage, matchStage, groupByDateAndSum);
		AggregationResults<Document> output = mongoTemplate.aggregate(aggregation, Task.class, Document.class);
		if(output.getMappedResults().size()>0) {
			return output.getMappedResults().get(0).getInteger("countTask");
		}
		return 0;
	}

	/**
	 * Người giao
	 *
	 * @param taskFilter the task filter
	 * @return List<UserTaskCount>
	 */
	@Override
	public List<UserTaskCount> getOwnerList(TaskFilter taskFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(taskFilter);
		
		MatchOperation matchStage = Aggregation.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		ProjectionOperation projectStage = Aggregation.project("ownerTask");
		GroupOperation groupByDateAndSum = Aggregation.group("ownerTask").count().as("countTask");
		SortOperation sortByDateAsc = Aggregation.sort(Sort.by(Direction.DESC, "countTask"));
		Aggregation aggregation = Aggregation.newAggregation(matchStage, projectStage, groupByDateAndSum, sortByDateAsc);
		AggregationResults<UserTaskCount> output = mongoTemplate.aggregate(aggregation, Task.class, UserTaskCount.class);
		return output.getMappedResults();
	}

	
	/**
	 * Người xử lý
	 *
	 * @param taskFilter the task filter
	 * @return List<UserTaskCount>
	 */
	@Override
	public List<UserTaskCount> getAssigneeList(TaskFilter taskFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(taskFilter);
		
		MatchOperation matchStage = Aggregation.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		ProjectionOperation projectStage = Aggregation.project("assigneeTask");
		GroupOperation groupByDateAndSum = Aggregation.group("assigneeTask").count().as("countTask");
		SortOperation sortByDateAsc = Aggregation.sort(Sort.by(Direction.DESC, "countTask"));
		Aggregation aggregation = Aggregation.newAggregation(matchStage, projectStage, groupByDateAndSum, sortByDateAsc);
		AggregationResults<UserTaskCount> output = mongoTemplate.aggregate(aggregation, Task.class, UserTaskCount.class);
		return output.getMappedResults();
	}

	@Override
	public List<UserTaskCount> getAssistantList(TaskFilter taskFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(taskFilter);
		
		MatchOperation matchStage = Aggregation.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		ProjectionOperation projectStage = Aggregation.project("assistantTask");
		GroupOperation groupByDateAndSum = Aggregation.group("assistantTask").count().as("countTask");
		SortOperation sortByDateAsc = Aggregation.sort(Sort.by(Direction.DESC, "countTask"));
		Aggregation aggregation = Aggregation.newAggregation(matchStage, projectStage, groupByDateAndSum, sortByDateAsc);
		AggregationResults<UserTaskCount> output = mongoTemplate.aggregate(aggregation, Task.class, UserTaskCount.class);
		return output.getMappedResults();
	}

	/**
	 * Người hỗ trợ
	 *
	 * @param taskFilter the task filter
	 * @return the support list
	 */
	@Override
	public List<UserTaskCount> getSuportList(TaskFilter taskFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(taskFilter);
		
		/* find followersTask.size() >= 1 */
		criteriaList.add(Criteria.where("followersTask.0").exists(true));
		
		MatchOperation matchStage = Aggregation.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		AggregationOperation unwind = Aggregation.unwind("followersTask", true);
		ProjectionOperation projectStage = Aggregation.project("followersTask");
		GroupOperation groupByDateAndSum = Aggregation.group("followersTask").count().as("countTask");
		
		SortOperation sortByDateAsc = Aggregation.sort(Sort.by(Direction.DESC, "countTask"));
		Aggregation aggregation = Aggregation.newAggregation(matchStage, unwind, projectStage, groupByDateAndSum, sortByDateAsc);
		AggregationResults<UserTaskCount> output = mongoTemplate.aggregate(aggregation, Task.class, UserTaskCount.class);
		return output.getMappedResults();
	}
	
	@SuppressWarnings({ "deprecation", "incomplete-switch" })
	@Override
	public List<Task> getListTop(TaskFilter taskFilter, int skip, int limit) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(taskFilter);
		
		ProjectionOperation projectStage = Aggregation.project(getListDeclaredFields());
		if(taskFilter.taskSubCategory!=null) {
			switch (taskFilter.taskSubCategory) {
			case CHUAHOANTHANH:
				projectStage = Aggregation.project(getListDeclaredFields()).andExpression("[0] - endTime", new Date()).as("sosanh");
				criteriaList.add(Criteria.where("endTime").exists(true));
				break;
			}
		}
		
		MatchOperation matchStage = Aggregation.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.DESC, "sosanh"));
		
		SkipOperation skipOperation = Aggregation.skip(0);
		LimitOperation limitOperation =Aggregation.limit(1000000);
		if(skip>=0 && limit>0) {
			skipOperation = Aggregation.skip(skip);
			limitOperation = Aggregation.limit(limit);
		}
		
		Aggregation aggregation = Aggregation.newAggregation(projectStage, matchStage, sortOperation, skipOperation, limitOperation);
		AggregationResults<Task> output = mongoTemplate.aggregate(aggregation, Task.class, Task.class);
		return output.getMappedResults();
	}
	
	protected String[] getListDeclaredFields() {
		Field[] fields = Task.class.getDeclaredFields();
		String[] results = new String[fields.length];
		int count=0;
		for (Field field : fields) {
			results[count++]=field.getName();
		}
		return results;
	}
}
