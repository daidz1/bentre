package ws.core.repository.imp;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import ws.core.model.User;
import ws.core.model.filter.UserFilter;
import ws.core.repository.UserRepositoryCustom;

@Repository
public class UserRepositoryCustomImp implements UserRepositoryCustom{
	@Autowired
	protected MongoTemplate mongoTemplate;
	
	@Autowired
    protected PasswordEncoder passwordEncoder;
	
	private List<Criteria> createCriteria(UserFilter userFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		
		if(userFilter._id!=null) {
			criteriaList.add(Criteria.where("_id").is(new ObjectId(userFilter._id)));
		}
		
		/* Lấy các userIds */
		if(userFilter.userIds.size()>0) {
			List<Criteria> criterias = new ArrayList<>();
			for (String id : userFilter.userIds) {
				Criteria findId = Criteria.where("_id").in(new ObjectId(id)); 
				criterias.add(findId);
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		/* Loại trừ các userIds */
		if(userFilter.excludeUserIds.size()>0) {
			List<Criteria> criterias = new ArrayList<>();
			for (String id : userFilter.excludeUserIds) {
				Criteria findId = Criteria.where("_id").nin(new ObjectId(id)); 
				criterias.add(findId);
			}
			criteriaList.add(new Criteria().andOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		if(userFilter.creatorId!=null) {
			criteriaList.add(Criteria.where("creatorId").is(userFilter.creatorId));
		}
		
		if(userFilter.keySearch!=null && !userFilter.keySearch.isEmpty()) {
			List<Criteria> criterias = new ArrayList<>();
			Criteria findUsername = Criteria.where("username").regex(".*"+userFilter.keySearch+".*", "i"); 
			criterias.add(findUsername);
			
			Criteria findFullname = Criteria.where("fullName").regex(".*"+userFilter.keySearch+".*", "i"); 
			criterias.add(findFullname);
			
			Criteria findJobtitle = Criteria.where("jobTitle").regex(".*"+userFilter.keySearch+".*", "i"); 
			criterias.add(findJobtitle);
			
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		if(userFilter.organizationEmpty!=null && !userFilter.organizationEmpty.isEmpty()) {
			if(userFilter.organizationEmpty.equalsIgnoreCase("true")) {
				criteriaList.add(Criteria.where("organizationIds").size(0));
			}else if(userFilter.organizationEmpty.equalsIgnoreCase("false")) {
				criteriaList.add(Criteria.where("organizationIds").not().size(0));
			}
		}
		
		if(userFilter.organizationIds.size()>0) {
			criteriaList.add(Criteria.where("organizations.organizationId").in(userFilter.organizationIds));
		}
		
		/* Loại trừ các organizationId */ 
		if(userFilter.excludeOrganizationIds.size()>0) {
			criteriaList.add(Criteria.where("organizations.organizationId").nin(userFilter.excludeOrganizationIds));
		}
		
		if(userFilter.leader!=null) {
			criteriaList.add(Criteria.where("leaders.userId").is(userFilter.leader.userId));
			criteriaList.add(Criteria.where("leaders.organizationId").is(userFilter.leader.organizationId));
		}
		
		if(userFilter.existsAccountDomino!=null) {
			criteriaList.add(Criteria.where("accountDomino").exists(Boolean.parseBoolean(userFilter.existsAccountDomino)));
		}
		
		return criteriaList;
	}
	
	@Override
	public List<User> findAll(UserFilter userFilter, int skip, int limit) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(userFilter);
		
		for (Criteria criteria : criteriaList) {
			System.out.println("+ "+criteria.getKey()+": "+criteria.getCriteriaObject().toJson());
		}
		
		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(skip>=0 && limit>0) {
			query.skip(skip);
			query.limit(limit);
		}
		query.with(Sort.by(Sort.Direction.ASC, "fullName"));
		
		return this.mongoTemplate.find(query, User.class);
	}

	@Override
	public int countAll(UserFilter userFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(userFilter);
		
		for (Criteria criteria : criteriaList) {
			System.out.println("+ "+criteria.getKey()+": "+criteria.getCriteriaObject().toJson());
		}
		
		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return (int) this.mongoTemplate.count(query, User.class);
	}

	@Override
	public boolean checkPassword(String userIdOrUsername, String password){
		User user=null;
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = new ArrayList<>();
		if(ObjectId.isValid(userIdOrUsername)) {
			criteriaList.add(Criteria.where("_id").is(new ObjectId(userIdOrUsername)));
		}else {
			criteriaList.add(Criteria.where("username").is(userIdOrUsername));
		}
		
		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		
		user = this.mongoTemplate.findOne(query, User.class);
		
		/* Check password */
		if(user!=null && passwordEncoder.matches(password, user.password)) {
			return true;
		}
		return false;
	}
}
