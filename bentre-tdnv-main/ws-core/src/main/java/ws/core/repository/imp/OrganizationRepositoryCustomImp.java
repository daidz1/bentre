package ws.core.repository.imp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ws.core.model.Organization;
import ws.core.model.filter.OrganizationFilter;
import ws.core.repository.OrganizationRepositoryCustom;

@Repository
public class OrganizationRepositoryCustomImp implements OrganizationRepositoryCustom{
	@Autowired
    private MongoTemplate mongoTemplate;
	
	private List<Criteria> createCriteria(OrganizationFilter organizationFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		
		if(organizationFilter._id!=null) {
			criteriaList.add(Criteria.where("_id").is(organizationFilter._id));
		}else if(organizationFilter._ids!=null && organizationFilter._ids.size()>0) {
			criteriaList.add(Criteria.where("_id").in(organizationFilter._ids));
		}
		
		if(organizationFilter.creatorId!=null) {
			criteriaList.add(Criteria.where("creatorId").is(organizationFilter.creatorId));
		}
		
		if(organizationFilter.name!=null) {
			criteriaList.add(Criteria.where("name").is(organizationFilter.name));
		}
		
		if(organizationFilter.keySearch!=null && !organizationFilter.keySearch.isEmpty()) {
			List<Criteria> subCriteriasOr = new ArrayList<>();
			
			List<String> listFields=Arrays.asList("name","description");
			for (String fieldName : listFields) {
				subCriteriasOr.add(Criteria.where(fieldName).regex(".*"+organizationFilter.keySearch+".*", "i"));
			}
			criteriaList.add(new Criteria().orOperator(subCriteriasOr.toArray(new Criteria[subCriteriasOr.size()])));
		}
		
		if(organizationFilter.parentId!=null) {
			criteriaList.add(Criteria.where("parentId").is(organizationFilter.parentId));
		}
		
		if(organizationFilter.active!=null) {
			criteriaList.add(Criteria.where("active").exists(Boolean.parseBoolean(organizationFilter.active)));
		}
		
		return criteriaList;
	}
	
	@Override
	public List<Organization> findAll(OrganizationFilter organizationFilter, int skip, int limit) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(organizationFilter);
		
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
		query.with(Sort.by(Sort.Direction.ASC, "numberOrder"));
		
		return this.mongoTemplate.find(query, Organization.class);
	}

	@Override
	public long countAll(OrganizationFilter organizationFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(organizationFilter);
		
		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return (int) this.mongoTemplate.count(query, Organization.class);
	}

	@Override
	public Optional<Organization> findOne(OrganizationFilter organizationFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(organizationFilter);
		
		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, Organization.class));
	}
}
