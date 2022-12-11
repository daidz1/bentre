package ws.core.repository.imp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ws.core.model.Doc;
import ws.core.model.filter.DocFilter;
import ws.core.repository.DocRepositoryCustom;

@Repository
public class DocRepositoryCustomImp implements DocRepositoryCustom{
	
	@Autowired
	protected MongoTemplate mongoTemplate;

	private List<Criteria> createCriteria(DocFilter docFilter){
		List<Criteria> criteriaList = new ArrayList<>();

		/* Tìm task _id */
		if(docFilter._id!=null) {
			criteriaList.add(Criteria.where("_id").is(new ObjectId(docFilter._id)));
		}

		/* Tìm theo category */
		if(docFilter.docCategory!=null) {
			criteriaList.add(Criteria.where("docCategory").is(docFilter.docCategory));
		}
		
		/* Tìm accountIOffice */
		if(docFilter.accountDomino!=null) {
			criteriaList.add(Criteria.where("docFrom").is(docFilter.accountDomino));
			
			/* Đối với văn phòng trung ương thì lại dùng người xử lý, và người hỗ trợ để giao nhiệm vụ */
			/*List<Criteria> orTemCriterias = new ArrayList<>();
			orTemCriterias.add(Criteria.where("norNameBoss").is(docFilter.accountDomino));
			orTemCriterias.add(Criteria.where("norNameG3").is(docFilter.accountDomino));
			criteriaList.add(new Criteria().orOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()])));*/
		}
		
		/* Tìm theo creatorDoc */
		if(docFilter.docCreator!=null) {
			if(docFilter.docCreator.userId!=null) {
				criteriaList.add(Criteria.where("docCreator.userId").is(docFilter.docCreator.userId));
			}
			
			if(docFilter.docCreator.organizationId!=null) {
				criteriaList.add(Criteria.where("docCreator.organizationId").is(docFilter.docCreator.organizationId));
			}
		}
		
		/* Tìm theo khung thời gian giao */
		if(docFilter.fromDate>0 && docFilter.toDate>0) {
			Date fromDate=new Date(docFilter.fromDate);
			Date toDate=new Date(docFilter.toDate);
			criteriaList.add(Criteria.where("docDate").gte(fromDate).lte(toDate));
		}else if(docFilter.fromDate>0) {
			Date fromDate=new Date(docFilter.fromDate);
			criteriaList.add(Criteria.where("docDate").gte(fromDate));
		}else if(docFilter.toDate>0) {
			Date toDate=new Date(docFilter.toDate);
			criteriaList.add(Criteria.where("docDate").lte(toDate));
		}
		

		if(docFilter.keySearch!=null) {
			List<Criteria> criterias = new ArrayList<>();
			
			String [] listKeys= {"docFrom","docRegCode","docSecurity","docNumber","docSymbol","docSignal","docType","docSigner","docOrgReceived","docOrgCreated","docSummary"};
			for (String key : listKeys) {
				Criteria findUsername = Criteria.where(key).regex(".*"+docFilter.keySearch+".*", "i"); 
				criterias.add(findUsername);
			}
			criteriaList.add(new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}

		/* Tìm findDocFroms */
		if(docFilter.findDocFroms!=null) {
			List<Criteria> orTemCriterias = new ArrayList<>();
			String[] ids=docFilter.findDocFroms.split(",");
			orTemCriterias.add(Criteria.where("docFrom").in(Arrays.asList(ids)));
			if(orTemCriterias.size()>0) {
				criteriaList.add(new Criteria().orOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()])));
			}
		}
		
		/* Tìm findNorNameBosses */
		if(docFilter.findNorNameBosses!=null) {
			List<Criteria> orTemCriterias = new ArrayList<>();
			String[] ids=docFilter.findNorNameBosses.split(",");
			orTemCriterias.add(Criteria.where("norNameBoss").in(Arrays.asList(ids)));
			if(orTemCriterias.size()>0) {
				criteriaList.add(new Criteria().orOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()])));
			}
		}
		
		/* Tìm findNorNameG3s */
		if(docFilter.findNorNameG3s!=null) {
			List<Criteria> orTemCriterias = new ArrayList<>();
			String[] ids=docFilter.findNorNameG3s.split(",");
			orTemCriterias.add(Criteria.where("norNameG3").in(Arrays.asList(ids)));
			if(orTemCriterias.size()>0) {
				criteriaList.add(new Criteria().orOperator(orTemCriterias.toArray(new Criteria[orTemCriterias.size()])));
			}
		}
		
		/* Tìm theo active */
		if(docFilter.active!=null) {
			criteriaList.add(Criteria.where("active").is(docFilter.active));
		}
		
		/* Tìm theo creatorId */
//		if(docFilter.creatorId!=null) {
//			criteriaList.add(Criteria.where("creatorId").is(docFilter.creatorId));
//		}
		
		return criteriaList;
	}

	@Override
	public List<Doc> findAll(DocFilter docFilter, int skip, int limit) {
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
		query.with(Sort.by(Sort.Direction.DESC, "docDate"));
		
		return this.mongoTemplate.find(query, Doc.class);
	}

	@Override
	public int countAll(DocFilter docFilter) {
		/* Danh sách thoả điều kiện */
		List<Criteria> criteriaList = createCriteria(docFilter);

		/* Câu truy vấn tìm kiếm */
		Query query = new Query();
		if(criteriaList.size()>0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		
		return (int) this.mongoTemplate.count(query, Doc.class);
	}
}
