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

import ws.core.model.Media;
import ws.core.model.filter.MediaFilter;
import ws.core.repository.MediaRepositoryCustom;
import ws.core.repository.TaskRepositoryCustom;

@Repository
public class MediaRepositoryCustomImp implements MediaRepositoryCustom{

	@Autowired
	protected MongoTemplate mongoTemplate;
	
	@Autowired
	protected TaskRepositoryCustom taskRepositoryCustom;
	
	private List<Criteria> createCriteria(MediaFilter mediaFilter){
		List<Criteria> criteriaList = new ArrayList<>();
		
		/* Tìm với loại */
		if(mediaFilter._id!=null) {
			criteriaList.add(Criteria.where("_id").in(new ObjectId(mediaFilter._id)));
		}
		
		/* Tìm theo khung thời gian giao */
		if(mediaFilter.fromDate>0 && mediaFilter.toDate>0) {
			Date fromDate=new Date(mediaFilter.fromDate);
			Date toDate=new Date(mediaFilter.toDate);
			criteriaList.add(Criteria.where("createdTime").gte(fromDate).lte(toDate));
		}else if(mediaFilter.fromDate>0) {
			Date fromDate=new Date(mediaFilter.fromDate);
			criteriaList.add(Criteria.where("createdTime").gte(fromDate));
		}else if(mediaFilter.toDate>0) {
			Date toDate=new Date(mediaFilter.toDate);
			criteriaList.add(Criteria.where("createdTime").lte(toDate));
		}
		
		/* Tìm theo người tạo*/
		if(mediaFilter.creator!=null && mediaFilter.creator.userId!=null && mediaFilter.creator.organizationId!=null) {
			criteriaList.add(Criteria.where("creator.userId").is(mediaFilter.creator.userId));
			criteriaList.add(Criteria.where("creator.organizationId").is(mediaFilter.creator.organizationId));
		}
		
		/* Tìm với acction exclude */
		if(mediaFilter.keySearch!=null) {
			criteriaList.add(Criteria.where("name").regex(".*"+mediaFilter.keySearch+".*", "i"));
		}
		
		/* Tìm với loại */
		if(mediaFilter.taskIds!=null) {
			criteriaList.add(Criteria.where("taskIds").in(mediaFilter.taskIds));
		}
		
		return criteriaList;
	}
	
	@Override
	public long countAll(MediaFilter mediaFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(mediaFilter);

		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}

		return this.mongoTemplate.count(query, Media.class);
	}

	@Override
	public List<Media> findAll(MediaFilter mediaFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(mediaFilter);

		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}

		if(mediaFilter.skipLimitFilter!=null && mediaFilter.skipLimitFilter.skip>=0 && mediaFilter.skipLimitFilter.limit>0) {
			query.skip(mediaFilter.skipLimitFilter.skip);
			query.limit(mediaFilter.skipLimitFilter.limit);
		}
		query.with(Sort.by(Sort.Direction.DESC, "createdTime"));
		
		return this.mongoTemplate.find(query, Media.class);
	}
	
	@Override
	public Optional<Media> findOne(MediaFilter mediaFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(mediaFilter);

		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return Optional.ofNullable(this.mongoTemplate.findOne(query, Media.class));
	}
}
