package ws.core.repository.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ws.core.model.LogRequest;
import ws.core.model.filter.LogRequestFilter;
import ws.core.repository.LogRequestRepositoryCustom;

@Repository
public class LogRequestRepositoryCustomImp implements LogRequestRepositoryCustom{
	@Autowired
    protected MongoTemplate mongoTemplate;

	private List<Criteria> createCriteria(LogRequestFilter logRequestFilter){
		List<Criteria> criteriaList = new ArrayList<>();

		/* Tìm action */
		if(logRequestFilter.action!=null) {
			criteriaList.add(Criteria.where("action").is(logRequestFilter.action));
		}

		/* Tìm access */
		if(logRequestFilter.access!=null) {
			criteriaList.add(Criteria.where("access").is(logRequestFilter.access));
		}
		
		/* Tìm userIdRequest */
		if(logRequestFilter.userIdRequest!=null) {
			criteriaList.add(Criteria.where("userRequest.userId").is(logRequestFilter.userIdRequest));
		}
		
		/* Tìm theo khung thời gian giao */
		if(logRequestFilter.fromDate>0 && logRequestFilter.toDate>0) {
			Date fromDate=new Date(logRequestFilter.fromDate);
			Date toDate=new Date(logRequestFilter.toDate);
			criteriaList.add(Criteria.where("createdTime").gte(fromDate).lte(toDate));
		}else if(logRequestFilter.fromDate>0) {
			Date fromDate=new Date(logRequestFilter.fromDate);
			criteriaList.add(Criteria.where("createdTime").gte(fromDate));
		}else if(logRequestFilter.toDate>0) {
			Date toDate=new Date(logRequestFilter.toDate);
			criteriaList.add(Criteria.where("createdTime").lte(toDate));
		}
		
		if(logRequestFilter.keySearch!=null) {
			List<Criteria> criterias = new ArrayList<>();
			
			String [] listKeys= {"addremote","requestURL","method","protocol"};
			for (String key : listKeys) {
				Criteria findUsername = Criteria.where(key).regex(".*"+logRequestFilter.keySearch+".*", "i"); 
				criterias.add(findUsername);
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}

		return criteriaList;
	}
	
	@Override
	public List<LogRequest> findAll(LogRequestFilter logRequestFilter, int skip, int limit) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(logRequestFilter);

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
		
		return this.mongoTemplate.find(query, LogRequest.class);
	}

	@Override
	public Long countAll(LogRequestFilter logRequestFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(logRequestFilter);

		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return this.mongoTemplate.count(query, LogRequest.class);
	}
	
}
