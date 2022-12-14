package ws.core.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.enums.DocSecurity;
import ws.core.enums.LogMessages;
import ws.core.model.Doc;
import ws.core.model.DocAttachment;
import ws.core.model.UserOrganizationCreator;
import ws.core.model.filter.DocFilter;
import ws.core.model.filter.TaskFilter;
import ws.core.model.request.ReqDocAttachFile;
import ws.core.model.request.ReqDocAttachment;
import ws.core.model.request.ReqDocEdit;
import ws.core.model.request.ReqDocNew;
import ws.core.repository.DocRepository;
import ws.core.repository.DocRepositoryCustom;
import ws.core.repository.OrganizationRepository;
import ws.core.repository.OrganizationRoleRepository;
import ws.core.repository.OrganizationRoleRepositoryCustom;
import ws.core.repository.TaskRepository;
import ws.core.repository.TaskRepositoryCustom;
import ws.core.repository.UserRepository;
import ws.core.repository.UserRepositoryCustom;
import ws.core.repository.imp.OrganizationRepositoryCustomImp;
import ws.core.service.DocAttachmentService;
import ws.core.service.DocService;
import ws.core.util.ResponseCMS;

@RestController
@RequestMapping("/website")
public class DocControllerWebsite {

	private Logger log = LogManager.getLogger(TaskControllerWebsite.class);

	@Autowired
	protected DocRepository docRepository;

	@Autowired
	protected DocRepositoryCustom docRepositoryCustom;
	
	@Autowired
	protected TaskRepository taskRepository;

	@Autowired
	protected TaskRepositoryCustom taskRepositoryCustom;

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected UserRepositoryCustom userRepositoryCustom;

	@Autowired
	protected OrganizationRepository organizationRepository;

	@Autowired
	protected OrganizationRepositoryCustomImp organizationRepositoryCustom;

	@Autowired
	protected OrganizationRoleRepository organizationRoleRepository;

	@Autowired
	protected OrganizationRoleRepositoryCustom organizationRoleRepositoryCustom;

	@Autowired
	protected DocAttachmentService docAttachmentService;

	@Autowired
	protected DocService docService;
	
	@GetMapping("/doc/count")
	public Object getCount(
			@RequestParam(name = "userId", required = false) String userId, 
			@RequestParam(name = "organizationId", required = true) String organizationId, 
			//@RequestParam(name = "accountDomino", required = true) String accountDomino,
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "docCategory", required = false) String docCategory, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "findDocFroms", required = false) String findDocFroms,
			@RequestParam(name = "findNorNameBosses", required = false) String findNorNameBosses,
			@RequestParam(name = "findNorNameG3s", required = false) String findNorNameG3s,
			@RequestParam(name = "active", required = false) String active) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			UserOrganizationCreator creatorDoc=new UserOrganizationCreator();
			creatorDoc.userId=userId;
			creatorDoc.organizationId=organizationId;
			
			DocFilter docFilter=new DocFilter();
//			docFilter.accountDomino=accountDomino;
//			if(accountDomino.equalsIgnoreCase("xemtatcacongvan")) {
//				docFilter.accountDomino=null;
//			}
			docFilter.docCreator=creatorDoc;
			
			docFilter.fromDate=fromDate;
			docFilter.toDate=toDate;
			docFilter.docCategory=docCategory;
			docFilter.keySearch=keyword;
			docFilter.findDocFroms=findDocFroms;
			docFilter.findNorNameBosses=findNorNameBosses;
			docFilter.findNorNameG3s=findNorNameG3s;
			docFilter.active=active;
			
			int total=docRepositoryCustom.countAll(docFilter);
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setTotal(total);
			return responseCMS.build();
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setResult(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	@GetMapping("/doc/list")
	public Object getList(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "userId", required = false) String userId, 
			@RequestParam(name = "organizationId", required = true) String organizationId, 
			//@RequestParam(name = "accountDomino", required = true) String accountDomino,
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "docCategory", required = false) String docCategory, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "findDocFroms", required = false) String findDocFroms,
			@RequestParam(name = "findNorNameBosses", required = false) String findNorNameBosses,
			@RequestParam(name = "findNorNameG3s", required = false) String findNorNameG3s,
			@RequestParam(name = "active", required = false) String active) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			UserOrganizationCreator creatorDoc=new UserOrganizationCreator();
			creatorDoc.userId=userId;
			creatorDoc.organizationId=organizationId;
			
			DocFilter docFilter=new DocFilter();
//			docFilter.accountDomino=accountDomino;
//			if(accountDomino.equalsIgnoreCase("xemtatcacongvan")) {
//				docFilter.accountDomino=null;
//			}
			docFilter.docCreator=creatorDoc;
			
			docFilter.fromDate=fromDate;
			docFilter.toDate=toDate;
			docFilter.docCategory=docCategory;
			docFilter.keySearch=keyword;
			docFilter.findDocFroms=findDocFroms;
			docFilter.findNorNameBosses=findNorNameBosses;
			docFilter.findNorNameG3s=findNorNameG3s;
			docFilter.active=active;
			
			int total=docRepositoryCustom.countAll(docFilter);
			List<Doc> docs=docRepositoryCustom.findAll(docFilter, skip, limit);
			List<Document> results=new ArrayList<Document>();
			for (Doc item : docs) {
				results.add(convertDocList(item));
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setTotal(total);
			responseCMS.setResult(results);
			return responseCMS.build();
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setResult(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	@GetMapping("/doc/get/{docId}")
	public Object getDoc(@PathVariable(name = "docId", required = true) String docId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			Doc doc=null;
			try {
				doc=docRepository.findById(new ObjectId(docId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("docId kh??ng t???n t???i trong h??? th???ng");
				return responseCMS.build();
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertDocDetail(doc));
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	@PostMapping("/doc/new")
	public Object docNew(@RequestBody @Valid ReqDocNew docNew){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			//CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			/* Check valid tr?????c khi l??u (th???c ra l?? ????? show l???i chi ti???t) */
			try {
				docService.validForNew(docNew);
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage(e.getMessage());
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			/* L??u data */
			Doc doc=new Doc();
			try {
				/* Ghi data cho task */
				doc.docCreator=docNew.docCreator;
				
				/* T??i kho???n mapping v???i ioffice */
				//doc.docFrom=userRequest.getUser().accountDomino;
				
				/* Lo???i v??n b???n ?????n ho???c ??i */
				doc.docCategory=docNew.docCategory;
				
				/* T??i kho???n mapping v???i ioffice */
				//doc.norNameBoss=userRequest.getUser().accountDomino;
				
				/* ????? m???t */
				DocSecurity docSecurity=DocSecurity.getItem(Integer.parseInt(docNew.getDocSecurity().toString()));
				doc.docSecurity=docSecurity.getType();
				
				/* S??? hi???u v??n b???n */
				doc.docNumber=docNew.docNumber;
				
				/* K?? hi???u v??n b???n */
				doc.docSymbol=docNew.docSymbol;
				
				/* S??? k?? hi???u v??n b???n */
				doc.docSignal=docNew.docNumber+"-"+docNew.docSymbol;
				
				/* Ng??y ban h??nh v??n b???n */
				if(docNew.docRegDate>0) {
					doc.docRegDate=new Date(docNew.docRegDate);
					doc.docDate=new Date(docNew.docRegDate);
				}
				
				/* Th??? lo???i v??n b???n */
				doc.docType=docNew.docType;
				
				/* H??? t??n ng?????i k?? v??n b???n */
				doc.docSigner=docNew.docSigner;
				
				/* ????n v??? nh???n v??n b???n */
				doc.docOrgReceived=docNew.docOrgReceived;
				
				/* ????n v??? ph??t h??nh v??n b???n */
				doc.docOrgCreated=docNew.docOrgCreated;
				
				/* Tr??ch y???u v??n b???n */
				doc.docSummary=docNew.docSummary;
				
				/* N???u c?? ????nh k??m khi import */
				if(docNew.docAttachments.size()>0) {
					LinkedList<DocAttachment> docAttachments=new LinkedList<DocAttachment>();
					for (ReqDocAttachment attachment : docNew.docAttachments) {
						try {
							DocAttachment docAttachment=docAttachmentService.storeMedia(attachment.fileName, attachment.fileType, attachment.fileBase64);
							docAttachments.add(docAttachment);
						} catch (Exception e) {
							e.printStackTrace();
							log.debug(e.getMessage());
							responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
							responseCMS.setMessage("????nh k??m ["+attachment.fileName+"] b??? l???i t???p, vui l??ng th??? l???i file kh??c");
							responseCMS.setError(e.getMessage());
							return responseCMS.build();
						}
					}
					doc.docAttachments.addAll(docAttachments);
					/* Sau khi l??u ????nh k??m th??nh c??ng */
				}
				
				/* Save task v??o DB */
				doc=docRepository.save(doc);
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage(e.getMessage());
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			responseCMS.setStatus(HttpStatus.CREATED);
			responseCMS.setResult(convertDocDetail(doc));
			return responseCMS.build();
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		} 
	}
	
	@PostMapping("/doc/edit/{docId}")
	public Object docEdit(@PathVariable(name = "docId", required = true) String docId, 
			@RequestBody @Valid ReqDocEdit docEdit){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			//CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Doc doc=null;
			try {
				doc=docRepository.findById(new ObjectId(docId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("docId ["+docId+"] kh??ng t???n t???i trong h??? th???ng");
				return responseCMS.build();
			}
			
			/* Check valid tr?????c khi l??u (th???c ra l?? ????? show l???i chi ti???t) */
			try {
				docService.validForEdit(docEdit);
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage(e.getMessage());
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			/* Ghi data cho task */
			//doc.creatorId=userRequest.getUser().getId();
			
			/* T??i kho???n mapping v???i ioffice */
			//doc.docFrom=userRequest.getUser().accountDomino;
			
			/* Lo???i v??n b???n ?????n ho???c ??i */
			doc.docCategory=docEdit.docCategory;
			
			/* T??i kho???n mapping v???i ioffice */
			//doc.norNameBoss=userRequest.getUser().accountDomino;

			/* ????? m???t */
			DocSecurity docSecurity=DocSecurity.getItem(Integer.parseInt(docEdit.getDocSecurity().toString()));
			doc.docSecurity=docSecurity.getType();

			/* S??? hi???u v??n b???n */
			doc.docNumber=docEdit.docNumber;

			/* K?? hi???u v??n b???n */
			doc.docSymbol=docEdit.docSymbol;

			/* S??? k?? hi???u v??n b???n */
			doc.docSignal=docEdit.docNumber+"-"+docEdit.docSymbol;

			/* Ng??y ban h??nh v??n b???n */
			doc.docRegDate=new Date(docEdit.docRegDate);
			doc.docDate=new Date(docEdit.docRegDate);

			/* Th??? lo???i v??n b???n */
			doc.docType=docEdit.docType;

			/* H??? t??n ng?????i k?? v??n b???n */
			doc.docSigner=docEdit.docSigner;

			/* ????n v??? nh???n v??n b???n */
			doc.docOrgReceived=docEdit.docOrgReceived;

			/* ????n v??? ph??t h??nh v??n b???n */
			doc.docOrgCreated=docEdit.docOrgCreated;

			/* Tr??ch y???u v??n b???n */
			doc.docSummary=docEdit.docSummary;

			/* N???u c?? ????nh k??m khi import */
			if(docEdit.addDocAttachments.size()>0) {
				LinkedList<DocAttachment> docAttachments=new LinkedList<DocAttachment>();
				for (ReqDocAttachment attachment : docEdit.addDocAttachments) {
					try {
						DocAttachment docAttachment=docAttachmentService.storeMedia(attachment.fileName, attachment.fileType, attachment.fileBase64);
						docAttachments.add(docAttachment);
					} catch (Exception e) {
						e.printStackTrace();
						log.debug(e.getMessage());
						responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
						responseCMS.setMessage("????nh k??m ["+attachment.fileName+"] b??? l???i t???p, vui l??ng th??? l???i file kh??c");
						responseCMS.setError(e.getMessage());
						return responseCMS.build();
					}
				}
				doc.docAttachments.addAll(docAttachments);
				/* Sau khi l??u ????nh k??m th??nh c??ng */
			}

			/* X??a ????nh k??m n???u c?? */
			if(docEdit.deleteDocAttachments.size()>0 && doc.docAttachments.size()>0) {
				/* Duy???t t???ng attachment c???n x??a */
				for (String idAttachmentDelete : docEdit.deleteDocAttachments) {
					for(DocAttachment attachment: doc.docAttachments) {
						if(attachment.getId().equalsIgnoreCase(idAttachmentDelete)) {
							doc.docAttachments.remove(attachment);
							docAttachmentService.delete(attachment);
							break;
						}
					}
				}
			}
			
			/* Save task v??o DB */
			doc=docRepository.save(doc);

			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertDocDetail(doc));
			responseCMS.setMessage("C???p nh???t th??nh c??ng");
			return responseCMS.build();
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		} 
	}
	
	@PostMapping("/doc/add-attachment/{docId}")
	public Object addDocAttachment(HttpServletRequest request,
			@PathVariable(name = "docId", required = true) String docId,
			@ModelAttribute("myUploadForm") @Validated ReqDocAttachFile myUploadForm) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			Doc doc=null;
			try {
				doc=docRepository.findById(new ObjectId(docId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("docId ["+docId+"] kh??ng t???n t???i trong h??? th???ng");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			DocAttachment docAttachment=docAttachmentService.storeMedia(request, myUploadForm);
			doc.docAttachments.add(docAttachment);
			
			doc=docRepository.save(doc);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("Th??m ????nh k??m th??nh c??ng");
			responseCMS.setResult(doc);
			return responseCMS.build();
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	@DeleteMapping("/doc/delete/{docId}")
	public Object deleteTask(@PathVariable(name = "docId", required = true) String docId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Ki???m tra articleId */
			Doc docDelete=null;
			try {
				docDelete=docRepository.findById(new ObjectId(docId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("taskId kh??ng t???n t???i trong h??? th???ng");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			/* Ki???m tra doc c?? nhi???m v??? kh??ng */
			int countTask=0;
			try {
				TaskFilter taskFilter=new TaskFilter();
				taskFilter.docId=docDelete.getId();
				countTask=taskRepositoryCustom.countAll(taskFilter);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(countTask>0) {
				responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setMessage("V??n b???n kh??ng th??? x??a, v?? ???? ???????c d??ng ????? giao ("+countTask+") nhi???m v???");
				return responseCMS.build();
			}
			
			/* Xo?? task */
			docRepository.delete(docDelete);

			
			/* Ki???m tra v?? x??a c??c ????nh k??m li??n quan */
			try {
				if(docDelete.docAttachments.size()>0) {
					for(DocAttachment attachment: docDelete.docAttachments) {
						docAttachmentService.delete(attachment);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
			}

			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("???? x??a c??ng v??n ["+docDelete.docSummary+"] th??nh c??ng");
			return responseCMS.build();
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	
	/*------------------------------ Doc Attachment --------------------------*/
	@GetMapping("/doc/attachment/get/{docId}")
	public Object getDocAttachments(@PathVariable(name = "docId", required = true) String docId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			Doc doc=null;
			try {
				doc=docRepository.findById(new ObjectId(docId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("docId kh??ng t???n t???i trong h??? th???ng");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(doc.docAttachments);
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	@GetMapping("/doc/attachment/path")
	public Object getDocAttachment(@RequestParam(name = "path", required = true) String path) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			byte[] base64Encode=docAttachmentService.getFilePath(path);
			if(base64Encode==null) {
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("path kh??ng t???n t???i trong h??? th???ng");
				return responseCMS.build();
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(base64Encode);
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	/*---------------------------------- convert --------------------------------*/
	protected Document convertDocList(Doc doc) {
		Document document=new Document();
		document.append("createdTime", doc.getCreatedTime());
		document.append("id", doc.getId());
		document.append("docCategory", doc.docCategory);
		document.append("docFrom", doc.docFrom);
		/*document.append("norNameBoss", doc.norNameBoss);
		document.append("norNameG3", doc.norNameG3);*/
		document.append("docRegCode", doc.docRegCode);
		document.append("docSecurity", doc.docSecurity);
		document.append("docNumber", doc.docNumber);
		document.append("docSymbol", doc.docSymbol);
		document.append("docSignal", doc.docSignal);
		if(doc.docDate!=null)
			document.append("docDate", doc.docDate.getTime());
		if(doc.docRegDate!=null)
			document.append("docRegDate", doc.docRegDate.getTime());
		document.append("docType", doc.docType);
		document.append("docSigner", doc.docSigner);
		document.append("docCopies", doc.docCopies);
		document.append("docPages", doc.docPages);
		document.append("docOrgReceived", doc.docOrgReceived);
		document.append("docOrgCreated", doc.docOrgCreated);
		document.append("docSummary", doc.docSummary);
		document.append("docAttachments", doc.docAttachments.size());
		try {
			TaskFilter taskFilter=new TaskFilter();
			taskFilter.docId=doc.getId();
			int countTask=taskRepositoryCustom.countAll(taskFilter);
			document.append("countTask", countTask);
		} catch (Exception e) {
			document.append("countTask", 0);
		}
		
		document.append("docCreator", doc.docCreator);
		
//		try {
//			User user=userRepository.findById(new ObjectId(doc.creatorId.toString())).get();
//			document.append("creatorId", doc.getCreatorId());
//			document.append("creatorName", user.getFullName());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		return document;
	}
	
	protected Document convertDocDetail(Doc doc) {
		Document document=new Document();
		document.append("createdTime", doc.getCreatedTime());
		document.append("id", doc.getId());
		document.append("docCategory", doc.docCategory);
		document.append("docFrom", doc.docFrom);
		/*document.append("norNameBoss", doc.norNameBoss);
		document.append("norNameG3", doc.norNameG3);*/
		document.append("docRegCode", doc.docRegCode);
		document.append("docSecurity", doc.docSecurity);
		document.append("docNumber", doc.docNumber);
		document.append("docSymbol", doc.docSymbol);
		document.append("docSignal", doc.docSignal);
		if(doc.docDate!=null)
			document.append("docDate", doc.docDate.getTime());
		if(doc.docRegDate!=null)
			document.append("docRegDate", doc.docRegDate.getTime());
		document.append("docType", doc.docType);
		document.append("docSigner", doc.docSigner);
		document.append("docCopies", doc.docCopies);
		document.append("docPages", doc.docPages);
		document.append("docOrgReceived", doc.docOrgReceived);
		document.append("docOrgCreated", doc.docOrgCreated);
		document.append("docSummary", doc.docSummary);
		document.append("docAttachments", doc.docAttachments);
		try {
			TaskFilter taskFilter=new TaskFilter();
			taskFilter.docId=doc.getId();
			int countTask=taskRepositoryCustom.countAll(taskFilter);
			document.append("countTask", countTask);
		} catch (Exception e) {
			document.append("countTask", 0);
		}
		
		document.append("docCreator", doc.docCreator);
		
//		try {
//			User user=userRepository.findById(new ObjectId(doc.creatorId.toString())).get();
//			document.append("creatorId", doc.getCreatorId());
//			document.append("creatorName", user.getFullName());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		return document;
	}
}
