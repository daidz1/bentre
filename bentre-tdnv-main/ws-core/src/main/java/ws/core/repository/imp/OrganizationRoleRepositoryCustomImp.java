package ws.core.repository.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ws.core.model.OrganizationRole;
import ws.core.model.filter.OrganizationRoleFilter;
import ws.core.repository.OrganizationRoleRepositoryCustom;


@Repository
public class OrganizationRoleRepositoryCustomImp implements OrganizationRoleRepositoryCustom{
	@Autowired
    protected MongoTemplate mongoTemplate;
	
	private List<Criteria> createCriteria(OrganizationRoleFilter organizationRoleFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		
		if(organizationRoleFilter._id!=null) {
			criteriaList.add(Criteria.where("_id").is(new ObjectId(organizationRoleFilter._id)));
		}
		
		/* Tìm các userIds */
		if(organizationRoleFilter.userIds!=null && organizationRoleFilter.userIds.size()>0) {
			List<Criteria> criterias = new ArrayList<>();
			for (String id : organizationRoleFilter.userIds) {
				Criteria findId = Criteria.where("userIds").in(id); 
				criterias.add(findId);
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		/* Loại trừ các userIds */
		if(organizationRoleFilter.excludeUserIds!=null && organizationRoleFilter.excludeUserIds.size()>0) {
			List<Criteria> criterias = new ArrayList<>();
			for (String id : organizationRoleFilter.excludeUserIds) {
				Criteria findId = Criteria.where("userIds").nin(id); 
				criterias.add(findId);
			}
			criteriaList.add(new Criteria().andOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		if(organizationRoleFilter.creatorId!=null) {
			criteriaList.add(Criteria.where("creatorId").is(organizationRoleFilter.creatorId));
		}
		
		if(organizationRoleFilter.keySearch!=null && !organizationRoleFilter.keySearch.isEmpty()) {
			List<Criteria> criterias = new ArrayList<>();
			Criteria findUsername = Criteria.where("name").regex(".*"+organizationRoleFilter.keySearch+".*", "i"); 
			criterias.add(findUsername);
			
			Criteria findFullname = Criteria.where("description").regex(".*"+organizationRoleFilter.keySearch+".*", "i"); 
			criterias.add(findFullname);
			
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		/* Tìm với các organizationId */
		if(organizationRoleFilter.organizationIds!=null && organizationRoleFilter.organizationIds.size()>0) {
			List<Criteria> criterias = new ArrayList<>();
			for (String id : organizationRoleFilter.organizationIds) {
				Criteria findId = Criteria.where("organizationId").is(id); 
				criterias.add(findId);
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		/* Loại trừ với organizationId */
		if(organizationRoleFilter.excludeOrganizationIds!=null && organizationRoleFilter.excludeOrganizationIds.size()>0) {
			List<Criteria> criterias = new ArrayList<>();
			for (String id : organizationRoleFilter.excludeOrganizationIds) {
				Criteria findId = Criteria.where("organizationId").ne(id); 
				criterias.add(findId);
			}
			criteriaList.add(new Criteria().andOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		/* Tìm các permissionKeys */
		if(organizationRoleFilter.permissionKeys!=null && organizationRoleFilter.permissionKeys.size()>0) {
			List<Criteria> criterias = new ArrayList<>();
			for (String id : organizationRoleFilter.permissionKeys) {
				Criteria findId = Criteria.where("permissionKeys").in(id); 
				criterias.add(findId);
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		/* Loại trừ các permissionKeys */
		if(organizationRoleFilter.excludePermissionKeys!=null && organizationRoleFilter.excludePermissionKeys.size()>0) {
			List<Criteria> criterias = new ArrayList<>();
			for (String id : organizationRoleFilter.excludePermissionKeys) {
				Criteria findId = Criteria.where("permissionKeys").nin(id); 
				criterias.add(findId);
			}
			criteriaList.add(new Criteria().andOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		return criteriaList;
	}
	
	@Override
	public List<OrganizationRole> findAll(OrganizationRoleFilter organizationRoleFilter, int skip, int limit) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(organizationRoleFilter);
		
		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		if(skip>=0 && limit>0) {
			query.skip(skip);
			query.limit(limit);
		}
		query.with(Sort.by(Sort.Direction.ASC, "_id"));
		
		return this.mongoTemplate.find(query, OrganizationRole.class);
	}

	@Override
	public int countAll(OrganizationRoleFilter organizationRoleFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(organizationRoleFilter);
		
		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return (int) this.mongoTemplate.count(query, OrganizationRole.class);
	}

	@Override
	public Optional<OrganizationRole> findOne(OrganizationRoleFilter organizationRoleFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(organizationRoleFilter);
		
		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, OrganizationRole.class));
	}

	@Override
	public List<OrganizationRole> getRolesOrganizationUser(String organizationId, String userId) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = new ArrayList<>();
		criteriaList.add(Criteria.where("organizationId").is(organizationId));
		
		criteriaList.add(Criteria.where("userIds").in(userId));
		
		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		query.with(Sort.by(Sort.Direction.ASC, "_id"));
		
		return this.mongoTemplate.find(query, OrganizationRole.class);
	}

	@Override
	public List<OrganizationRole> getRolesOrganization(String organizationId, String keyword) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = new ArrayList<>();
		criteriaList.add(Criteria.where("organizationId").is(organizationId));
		
		/* Tìm like và hoặc */
		if(keyword!=null && !keyword.isEmpty()) {
			List<Criteria> criterias = new ArrayList<>();
			Criteria findUsername = Criteria.where("name").regex(".*"+keyword+".*", "i"); 
			criterias.add(findUsername);
			
			Criteria findFullname = Criteria.where("description").regex(".*"+keyword+".*", "i"); 
			criterias.add(findFullname);
			
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		query.with(Sort.by(Sort.Direction.ASC, "_id"));
		
		return this.mongoTemplate.find(query, OrganizationRole.class);
	}

	@Override
	public int countRolesOrganization(String organizationId, String keyword) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = new ArrayList<>();
		criteriaList.add(Criteria.where("organizationId").is(organizationId));
		
		/* Tìm like và hoặc */
		if(keyword!=null && !keyword.isEmpty()) {
			List<Criteria> criterias = new ArrayList<>();
			Criteria findUsername = Criteria.where("name").regex(".*"+keyword+".*", "i"); 
			criterias.add(findUsername);
			
			Criteria findFullname = Criteria.where("description").regex(".*"+keyword+".*", "i"); 
			criterias.add(findFullname);
			
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		
		return (int) this.mongoTemplate.count(query, OrganizationRole.class);
	}

	@Override
	public String getRolesOrganizationUserString(String organizationId, String userId) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = new ArrayList<>();
		criteriaList.add(Criteria.where("organizationId").is(organizationId));
		criteriaList.add(Criteria.where("userIds").in(userId));
		
		MatchOperation matchStage = Aggregation.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		ProjectionOperation projectStage = Aggregation.project("name");
		
		Aggregation aggregation = Aggregation.newAggregation(matchStage, projectStage);
		AggregationResults<Document> output = mongoTemplate.aggregate(aggregation, OrganizationRole.class, Document.class);
		if(output.getMappedResults().size()>0) {
			List<String> listRoles=new ArrayList<String>();
			for(Document string:output.getMappedResults()) {
				listRoles.add(string.getString("name"));
			}
			return String.join(", ", listRoles);
		}
			
		return "";
	}
}
