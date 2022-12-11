package ws.core.repository.imp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ws.core.model.OrganizationRole;
import ws.core.model.RoleTemplate;
import ws.core.repository.RoleTemplateRepositoryCustom;


@Repository
public class RoleTemplateRepositoryCustomImp implements RoleTemplateRepositoryCustom{
	@Autowired
    MongoTemplate mongoTemplate;
	
	@Override
	public List<RoleTemplate> findAll(int skip, int limit, String keyword) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = new ArrayList<>();
		criteriaList.add(Criteria.where("name").exists(true));
		
		/* Tìm like và hoặc */
		if(!keyword.isEmpty()) {
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
		query.skip(skip);
		query.limit(limit);
		query.with(Sort.by(Sort.Direction.ASC, "_id"));
		
		return this.mongoTemplate.find(query, RoleTemplate.class);
	}

	@Override
	public int countAll(String keyword) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = new ArrayList<>();
		criteriaList.add(Criteria.where("name").exists(true));
		
		/* Tìm like và hoặc */
		if(!keyword.isEmpty()) {
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
}
