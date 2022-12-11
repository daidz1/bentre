package ws.core.scheduled;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ws.core.enums.DocCategory;
import ws.core.model.Doc;
import ws.core.model.DocAttachment;
import ws.core.model.User;
import ws.core.model.filter.UserFilter;
import ws.core.module.ioffice.IOfficeService;
import ws.core.module.ioffice.PackageIOffice;
import ws.core.module.ioffice.TrangthaiIOffice;
import ws.core.module.ioffice.VanbanDKModel;
import ws.core.module.ioffice.VanbanModel;
import ws.core.repository.DocRepository;
import ws.core.repository.UserRepository;
import ws.core.repository.UserRepositoryCustom;
import ws.core.service.DocAttachmentService;


@Component
public class IOfficeSynTasks {
	protected Logger log = LogManager.getLogger(IOfficeSynTasks.class);

	@Autowired
	protected IOfficeService iOfficeService;
	
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected UserRepositoryCustom userRepositoryCustom;
	
	@Autowired
	protected DocRepository docRepository;
	
	@Autowired
	protected DocAttachmentService docAttachmentService;
	
	@Value("${ioffice.sync.start}")
	boolean iofficeSyncStart;
	
	@Scheduled(cron = "0 * * ? * *") /* mỗi phút */
    public void scheduleTaskWithFixedSitemaps() {
		if(iofficeSyncStart) {
			System.out.println("IOffice sync running .....");
			sync();
		}else {
			System.out.println("IOffice sync stoped, it disabled");
		}
    }
	
	private synchronized void sync() {
		boolean auth=false;
		try {
			auth=iOfficeService.getauthentication();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("IOffice thread auth: "+auth);
		log.debug("IOffice thread auth: "+auth);
		
		if(auth){
			try {
				boolean isChange=false;
				UserFilter userFilter=new UserFilter();
				userFilter.existsAccountDomino="true";
				
				List<User> users=userRepositoryCustom.findAll(userFilter, 0, 0);
				for (User user : users) {
					if(StringUtils.isEmpty(user.getAccountDomino())) {
						continue;
					}
					
					int sogoitin=0;
					TrangthaiIOffice trangthaiIOffice=null;
					
					/*Refrest lại từ đầu*/
					/*trangthaiIOffice=TrangthaiIOffice.DAGUIVANHANTHANHCONG;
					sogoitin=IOficeLocalServiceUtil.GetCountVBCDDH(user.getUserIOffice(), trangthaiIOffice.getGET(), "", "");
					System.out.println(user.getFullName()+", có số VB CDDH ĐÃ NHẬN: "+sogoitin);
					LOG.info(user.getFullName()+", có số VB CDDH ĐÃ NHẬN: "+sogoitin);  
					
					for (int i = 0; i < sogoitin; i++) {
						GoiTinIOffice goiTinCDDH=IOficeLocalServiceUtil.GetVBCDDHNew(user.getUserIOffice(), trangthaiIOffice.getGET(), "", "");
						IOficeLocalServiceUtil.UpdateTTVBCDDH(goiTinCDDH.getIdGoiTin(), user.getUserIOffice(), trangthaiIOffice.getUPDATE());
						System.out.println(user.getFullName()+", đã update lại trạng thai LỖI KHI NHÂN cho "+goiTinCDDH.getIdGoiTin());
						LOG.info(user.getFullName()+", đã update lại trạng thai LỖI KHI NHÂN cho "+goiTinCDDH.getIdGoiTin());
					}*/
					
					/*Lấy lại văn bản bị lấy lỗi lần trước*/
					try {
						trangthaiIOffice=TrangthaiIOffice.LOIKHINHAN;
						sogoitin=iOfficeService.GetCountVBCDDH(user.getAccountDomino(), trangthaiIOffice.getGET(), "", "");
						
						System.out.println("- "+user.getFullName()+", có số VB CDDH LỖI KHI NHÂN: "+sogoitin);
						log.debug("- "+user.getFullName()+", có số VB CDDH LỖI KHI NHÂN: "+sogoitin);
						
						for (int i = 0; i < sogoitin; i++) {
							PackageIOffice goiTinCDDH=null;
							try {
								goiTinCDDH=iOfficeService.GetVBCDDHNew(user.getAccountDomino(), trangthaiIOffice.getGET(), "", "");
								
								/* Lưu thông tin văn bản */
								Doc doc=convertToDoc(goiTinCDDH.getModelVanBanPhatHanh(), user);
								doc=docRepository.save(doc);
								
								/* Lưu đính kèm văn bản */
								if(goiTinCDDH.isAttachment()){
									List<DocAttachment> listDocAttachments=new ArrayList<DocAttachment>();
									for(VanbanDKModel VBDKModel:goiTinCDDH.getModelVanBanDinhKem()){
										try {
											DocAttachment docAttachment = convertToDocAttachment(doc, user, VBDKModel);
											listDocAttachments.add(docAttachment);
											
											int idGoiTin=Integer.parseInt(goiTinCDDH.getIdGoiTin());
											boolean status=iOfficeService.UpdateTTVBCDDH(idGoiTin, user.getAccountDomino(), trangthaiIOffice.getUPDATE());
											
											System.out.println("+ Sync Attachment status: "+status+", attachname: "+VBDKModel.getTenDinhKem());
											log.debug("+ Sync Attachment status: "+status+", attachname: "+VBDKModel.getTenDinhKem());
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
									
									if(listDocAttachments.size()>0) {
										doc.docAttachments.addAll(listDocAttachments);
										docRepository.save(doc);
									}
								}
								
								System.out.println("IdGoitin: "+goiTinCDDH.getIdGoiTin()+", IdIOffice: "+goiTinCDDH.getModelVanBanPhatHanh().getIdIOffice()+", IdVanban: "+doc.getId()+", Trichyeu: "+doc.getDocSummary());
								log.debug("IdGoitin: "+goiTinCDDH.getIdGoiTin()+", IdIOffice: "+goiTinCDDH.getModelVanBanPhatHanh()+", IdVanban: "+doc.getId()+", Trichyeu: "+doc.getDocSummary());
							}catch(Exception e) {
								System.out.println("Lỗi ở gói tin - và đã bỏ qua");
								e.printStackTrace();
							}
							isChange=true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					
					/*Lấy văn bản mới*/
					try {
						trangthaiIOffice=TrangthaiIOffice.CHUAGUI;
						sogoitin=iOfficeService.GetCountVBCDDH(user.getAccountDomino(), trangthaiIOffice.getGET(), "", "");
						
						System.out.println("- "+user.getFullName()+", có số VB CDDH CHƯA GỬI: "+sogoitin);
						log.debug("- "+user.getFullName()+", có số VB CDDH CHƯA GỬI: "+sogoitin);
						
						for (int i = 0; i < sogoitin; i++) {
							PackageIOffice goiTinCDDH=null;
							try {
								goiTinCDDH=iOfficeService.GetVBCDDHNew(user.getAccountDomino(), trangthaiIOffice.getGET(), "", "");
								
								/* Lưu thông tin văn bản */
								Doc doc=convertToDoc(goiTinCDDH.getModelVanBanPhatHanh(), user);
								doc=docRepository.save(doc);
								
								/* Lưu đính kèm văn bản */
								if(goiTinCDDH.isAttachment()){
									List<DocAttachment> listDocAttachments=new ArrayList<DocAttachment>();
									for(VanbanDKModel VBDKModel:goiTinCDDH.getModelVanBanDinhKem()){
										try {
											DocAttachment docAttachment = convertToDocAttachment(doc, user, VBDKModel);
											listDocAttachments.add(docAttachment);
										} catch (Exception e) {
											e.printStackTrace();
											int idGoiTin=Integer.parseInt(goiTinCDDH.getIdGoiTin());
											boolean status=iOfficeService.UpdateTTVBCDDH(idGoiTin, user.getAccountDomino(), trangthaiIOffice.getUPDATE());
											
											System.out.println("+ Sync Attachment status: "+status+", attachname: "+VBDKModel.getTenDinhKem());
											log.debug("+ Sync Attachment status: "+status+", attachname: "+VBDKModel.getTenDinhKem());
										}
									}
									
									if(listDocAttachments.size()>0) {
										doc.docAttachments.addAll(listDocAttachments);
										docRepository.save(doc);
									}
								}
								
								System.out.println("IdGoitin: "+goiTinCDDH.getIdGoiTin()+", IdIOffice: "+goiTinCDDH.getModelVanBanPhatHanh().getIdIOffice()+", IdVanban: "+doc.getId()+", Trichyeu: "+doc.getDocSummary());
								log.debug("IdGoitin: "+goiTinCDDH.getIdGoiTin()+", IdIOffice: "+goiTinCDDH.getModelVanBanPhatHanh()+", IdVanban: "+doc.getId()+", Trichyeu: "+doc.getDocSummary());
							}catch(Exception e) {
								System.out.println("Lỗi ở gói tin - và đã bỏ qua");
								e.printStackTrace();
							}
							
							/* Có thay đổi */
							isChange=true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				/* Nếu thay đổi thì do something */
				if(isChange) {
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/* Chuyển thông tin văn bản từ ioffice vào model hệ thống đang sử dụng */
	private Doc convertToDoc(VanbanModel vanbanModel, User user) throws Exception{
		try {
			Doc doc=new Doc();
			
			/* Ngày thêm vào hệ thống */
			doc.createdTime=new Date();
			
			/* Ghi data cho task */
			//doc.creatorId=user.getId();
			
			/* Mặc định là công văn đi */
			doc.docCategory=DocCategory.CVDI.getKey();
			
			/* Người soạn thảo văn bản */
			doc.docFrom=user.getAccountDomino();
			
			/* Người chủ trì văn bản */
			//doc.norNameBoss=
			
			/* Người hỗ trợ, phối hợp */
			//doc.norNameG3=
			
			//doc.docRegCode=docCreate.docRegCode;
			
			/* Độ mật */
			doc.docSecurity=vanbanModel.getDomat().getName();
			
			/* Số hiệu */
			doc.docNumber=vanbanModel.getSoHieu();
			
			/* Ký hiệu */
			doc.docSymbol=vanbanModel.getKyHieu();
			
			/* Số ký hiệu */
			doc.docSignal=vanbanModel.getSoHieu()+"-"+vanbanModel.getKyHieu();
			
			if(vanbanModel.getNgaythem()!=null) {
				doc.docDate=vanbanModel.getNgaythem();
			}
			
			if(vanbanModel.getNgayBanhanh()!=null) {
				doc.docRegDate=vanbanModel.getNgayBanhanh();
			}
			
			/* Loại văn bản */
			doc.docType=vanbanModel.getTenLoaivanban();
			
			/* Người ký văn bản */
			doc.docSigner=vanbanModel.getTenNguoiky();
			
			doc.docSignerPosition=vanbanModel.getTenChucVuNguoiky();
			
			/* Số lượng văn bản phát hành */
			doc.docCopies=vanbanModel.getSoLuongBanPhatHanh();
			
			/* Số trang của văn bản */
			doc.docPages=vanbanModel.getSoTrangCuaVanBan();
			
			/* Đơn vị nhận văn bản */
			doc.docOrgReceived=vanbanModel.getNoiNhan();
			
			/* Đơn vị phát hành văn bản */
			doc.docOrgCreated=vanbanModel.getTenDonvibanhanh();
			
			/* Trích yếu văn bản */
			doc.docSummary=vanbanModel.getTrichYeu();
			
			/* Đính kèm văn bản xử lý sau khi lưu thông tin văn bản thành công */
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	/* Chuyển thông tin đính kèm từ ioffice sáng model hệ thống đang sử dụng */
	private DocAttachment convertToDocAttachment(Doc doc, User user, VanbanDKModel vBDKModel) {
		DocAttachment docAttachment = docAttachmentService.storeMedia(vBDKModel.getTenDinhKem(), vBDKModel.getLoaiDinhKem(), vBDKModel.getNoiDungDinhKem());
		if(docAttachment!=null) {
			docAttachment.setIdIOffice(vBDKModel.getIdDinhKem()+"");
			return docAttachment;
		}
		return null;
	}
}
