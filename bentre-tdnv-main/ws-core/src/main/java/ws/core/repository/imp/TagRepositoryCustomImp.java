package ws.core.repository.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ws.core.model.Tag;
import ws.core.model.filter.TagFilter;
import ws.core.repository.TagRepositoryCustom;
import ws.core.repository.TaskRepositoryCustom;

@Repository
public class TagRepositoryCustomImp implements TagRepositoryCustom{

	@Autowired
	protected MongoTemplate mongoTemplate;
	
	@Autowired
	protected TaskRepositoryCustom taskRepositoryCustom;
	
	private List<Criteria> createCriteria(TagFilter tagFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		
		/* Tìm với loại */
		if(tagFilter._id!=null) {
			criteriaList.add(Criteria.where("_id").in(new ObjectId(tagFilter._id)));
		}
		
		/* Tìm theo khung thời gian giao */
		if(tagFilter.fromDate>0 && tagFilter.toDate>0) {
			Date fromDate=new Date(tagFilter.fromDate);
			Date toDate=new Date(tagFilter.toDate);
			criteriaList.add(Criteria.where("createdTime").gte(fromDate).lte(toDate));
		}else if(tagFilter.fromDate>0) {
			Date fromDate=new Date(tagFilter.fromDate);
			criteriaList.add(Criteria.where("createdTime").gte(fromDate));
		}else if(tagFilter.toDate>0) {
			Date toDate=new Date(tagFilter.toDate);
			criteriaList.add(Criteria.where("createdTime").lte(toDate));
		}
		
		/* Tìm theo người tạo*/
		if(tagFilter.creator!=null && tagFilter.creator.userId!=null && tagFilter.creator.organizationId!=null) {
			criteriaList.add(Criteria.where("creator.userId").is(tagFilter.creator.userId));
			criteriaList.add(Criteria.where("creator.organizationId").is(tagFilter.creator.organizationId));
		}
		
		/* Tìm với acction exclude */
		if(tagFilter.keySearch!=null) {
			criteriaList.add(Criteria.where("name").regex(".*"+tagFilter.keySearch+".*", "i"));
		}
		
		/* Tìm với loại */
		if(tagFilter.taskIds!=null) {
			criteriaList.add(Criteria.where("taskIds").in(tagFilter.taskIds));
		}
		
		return criteriaList;
	}
	
	@Override
	public long countAll(TagFilter tagFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(tagFilter);

		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}

		return this.mongoTemplate.count(query, Tag.class);
	}

	@Override
	public List<Tag> findAll(TagFilter tagFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(tagFilter);

		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}

		if(tagFilter.skipLimitFilter!=null && tagFilter.skipLimitFilter.skip>=0 && tagFilter.skipLimitFilter.limit>0) {
			query.skip(tagFilter.skipLimitFilter.skip);
			query.limit(tagFilter.skipLimitFilter.limit);
		}
		query.with(Sort.by(Sort.Direction.DESC, "createdTime"));
		
		return this.mongoTemplate.find(query, Tag.class);
	}
	
	@Override
	public Optional<Tag> findOne(TagFilter tagFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(tagFilter);

		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, Tag.class));
	}
}
