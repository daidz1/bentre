package ws.core.repository.imp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ws.core.model.Permission;
import ws.core.repository.PermissionRepositoryCustom;


@Repository
public class PermissionRepositoryCustomImp implements PermissionRepositoryCustom{
	@Autowired
    MongoTemplate mongoTemplate;
	

	@Override
	public List<Permission> getList(String permissionKeys) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = new ArrayList<>();
		
		/* permissionKeys */
		if(permissionKeys!=null && permissionKeys.isEmpty()==false) {
			List<Criteria> criterias = new ArrayList<>();
			String[] ids=permissionKeys.split(",");
			for (String id : ids) {
				Criteria findId = Criteria.where("key").in(id); 
				criterias.add(findId);
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}
		
		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		/* sort */
		List<Order> orders=new ArrayList<Order>();
		orders.add(new Order(Sort.Direction.ASC, "groupOrder"));
		orders.add(new Order(Sort.Direction.ASC, "order"));
		query.with(Sort.by(orders));
		
		return this.mongoTemplate.find(query, Permission.class);
	}
}
