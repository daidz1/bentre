package ws.core.repository.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ws.core.model.AppMobi;
import ws.core.model.filter.AppMobiFilter;
import ws.core.repository.AppMobiRepositoryCustom;

@Repository
public class AppMobiRepositoryCustomImp implements AppMobiRepositoryCustom{
	
	@Autowired
	protected MongoTemplate mongoTemplate;

	private List<Criteria> createCriteria(AppMobiFilter appMobiFilter){
		List<Criteria> criteriaList = new ArrayList<>();

		/* Tìm task _id */
		if(appMobiFilter._id!=null) {
			criteriaList.add(Criteria.where("_id").is(new ObjectId(appMobiFilter._id)));
		}

		/* Tìm theo userId */
		if(appMobiFilter.userId!=null) {
			criteriaList.add(Criteria.where("userId").is(appMobiFilter.userId));
		}
		
		/* Tìm theo deviceId */
		if(appMobiFilter.deviceId!=null) {
			criteriaList.add(Criteria.where("deviceId").is(appMobiFilter.deviceId));
		}
		
		/* Tìm theo khung thời gian giao */
		if(appMobiFilter.fromDate>0 && appMobiFilter.toDate>0) {
			Date fromDate=new Date(appMobiFilter.fromDate);
			Date toDate=new Date(appMobiFilter.toDate);
			criteriaList.add(Criteria.where("docDate").gte(fromDate).lte(toDate));
		}else if(appMobiFilter.fromDate>0) {
			Date fromDate=new Date(appMobiFilter.fromDate);
			criteriaList.add(Criteria.where("docDate").gte(fromDate));
		}else if(appMobiFilter.toDate>0) {
			Date toDate=new Date(appMobiFilter.toDate);
			criteriaList.add(Criteria.where("docDate").lte(toDate));
		}
		
		if(appMobiFilter.keySearch!=null) {
			List<Criteria> criterias = new ArrayList<>();
			
			String [] listKeys= {"username","fullName","longitute","lagitute"};
			for (String key : listKeys) {
				Criteria findUsername = Criteria.where(key).regex(".*"+appMobiFilter.keySearch+".*", "i"); 
				criterias.add(findUsername);
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}

		/* Tìm theo active */
		if(appMobiFilter.active!=null) {
			criteriaList.add(Criteria.where("active").is(Boolean.valueOf(appMobiFilter.active)));
		}
		
		return criteriaList;
	}

	@Override
	public List<AppMobi> findAll(AppMobiFilter docFilter, int skip, int limit) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(docFilter);

		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}

		if(skip>=0 && limit>0) {
			query.skip(skip);
			query.limit(limit);
		}
		
		List<Order> orders=new ArrayList<Order>();
		orders.add(new Order(Sort.Direction.DESC, "userId"));
		orders.add(new Order(Sort.Direction.ASC, "deviceId"));
		orders.add(new Order(Sort.Direction.DESC, "_id"));
		query.with(Sort.by(orders));
		
		return this.mongoTemplate.find(query, AppMobi.class);
	}

	@Override
	public int countAll(AppMobiFilter docFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(docFilter);

		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return (int) this.mongoTemplate.count(query, AppMobi.class);
	}

	@Override
	public AppMobi get(String userId, String deviceId) {
		AppMobiFilter appMobiFilter=new AppMobiFilter();
		appMobiFilter.userId=userId;
		appMobiFilter.deviceId=deviceId;
		
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(appMobiFilter);

		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return this.mongoTemplate.findOne(query, AppMobi.class);
	}
}
