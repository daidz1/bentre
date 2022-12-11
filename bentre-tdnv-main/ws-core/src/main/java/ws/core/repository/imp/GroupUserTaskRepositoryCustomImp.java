package ws.core.repository.imp;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ws.core.model.GroupUserTask;
import ws.core.model.filter.GroupUserTaskFilter;
import ws.core.repository.GroupUserTaskRepositoryCustom;

@Repository
public class GroupUserTaskRepositoryCustomImp implements GroupUserTaskRepositoryCustom{
	
	@Autowired
	protected MongoTemplate mongoTemplate;

	private List<Criteria> createCriteria(GroupUserTaskFilter groupUserTaskFilter){
		List<Criteria> criteriaList = new ArrayList<>();

		/* Tìm task _id */
		if(groupUserTaskFilter._id!=null) {
			criteriaList.add(Criteria.where("_id").is(new ObjectId(groupUserTaskFilter._id)));
		}

		/* Tim keySearch */
		if(groupUserTaskFilter.keySearch!=null && !groupUserTaskFilter.keySearch.isEmpty()) {
			List<Criteria> criterias = new ArrayList<>();
			Criteria findName = Criteria.where("name").regex(".*"+groupUserTaskFilter.keySearch+".*", "i"); 
			criterias.add(findName);

			Criteria findDescription = Criteria.where("description").regex(".*"+groupUserTaskFilter.keySearch+".*", "i"); 
			criterias.add(findDescription);

			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		/* Tìm theo người tạo */
		if(groupUserTaskFilter.creator!=null) {
			criteriaList.add(Criteria.where("creator.userId").is(groupUserTaskFilter.creator.userId));
			criteriaList.add(Criteria.where("creator.organizationId").is(groupUserTaskFilter.creator.organizationId));
		}
		
		/* Tìm findAssignees */
		if(groupUserTaskFilter.findAssignees!=null && groupUserTaskFilter.findAssignees.isEmpty()==false) {
			List<Criteria> orTemCriterias = new ArrayList<>();
			String[] ids=groupUserTaskFilter.findAssignees.split(",");
			for (String id : ids) {
				if(id.contains("-")  && id.split("-").length==2) {
					String[] userTask=id.split("-");
					List<Criteria> iTemCriterias = new ArrayList<>();
					iTemCriterias.add(Criteria.where("assigneeTask.userId").is(userTask[0]));
					iTemCriterias.add(Criteria.where("assigneeTask.organizationId").is(userTask[1]));
					orTemCriterias.add(new Criteria().andOperator(iTemCriterias.toArray(new Criteria[iTemCriterias.size()])));
				}
			}
			if(orTemCriterias.size()>0) {
				criteriaList.add(new Criteria().orOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()])));
			}
		}
		
		/* Tìm findFollowers */
		if(groupUserTaskFilter.findFollowers!=null && groupUserTaskFilter.findFollowers.isEmpty()==false) {
			List<Criteria> orTemCriterias = new ArrayList<>();
			String[] ids=groupUserTaskFilter.findFollowers.split(",");
			for (String id : ids) {
				if(id.contains("-") && id.split("-").length==2) {
					String[] userTask=id.split("-");
					List<Criteria> iTemCriterias = new ArrayList<>();
					iTemCriterias.add(Criteria.where("followersTask.userId").is(userTask[0]));
					iTemCriterias.add(Criteria.where("followersTask.organizationId").is(userTask[1]));
					orTemCriterias.add(new Criteria().andOperator(iTemCriterias.toArray(new Criteria[iTemCriterias.size()])));
				}
			}
			if(orTemCriterias.size()>0) {
				criteriaList.add(new Criteria().orOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()])));
			}
		}
		
		/* Tìm assignmentType */
		if(groupUserTaskFilter.assignmentType!=null) {
			criteriaList.add(Criteria.where("assignmentType").is(groupUserTaskFilter.assignmentType));
		}
		
		return criteriaList;
	}

	@Override
	public List<GroupUserTask> findAll(GroupUserTaskFilter groupUserTaskFilter, int skip, int limit) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(groupUserTaskFilter);

		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}

		if(skip>=0 && limit>0) {
			query.skip(skip);
			query.limit(limit);
		}
		query.with(Sort.by(Sort.Direction.ASC, "sortBy"));
		
		return this.mongoTemplate.find(query, GroupUserTask.class);
	}

	@Override
	public long countAll(GroupUserTaskFilter groupUserTaskFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(groupUserTaskFilter);

		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return this.mongoTemplate.count(query, GroupUserTask.class);
	}
}
