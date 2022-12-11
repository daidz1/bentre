package ws.core.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ws.core.model.DocAttachment;
import ws.core.model.request.ReqDocAttachFile;
import ws.core.util.TextUtil;


@Service
public class DocAttachmentService {
	@Autowired
	private FileLocationService fileLocationService;
	
	public DocAttachment storeMedia(String fileName, String fileType, byte[] fileBase64) {
		try {
			DocAttachment docAttachment=new DocAttachment();
	 
	        File uploadRootDir = new File(fileLocationService.getPathAttachments());
			/* Tạo thư mục gốc upload nếu nó không tồn tại. */
	        if (!uploadRootDir.exists()) {
	            uploadRootDir.mkdirs();
	        }
	        
			/* Tên file gốc tại Client. */
	        docAttachment.fileName=fileName;
	        docAttachment.fileType=fileType;
	        docAttachment.filePath=docAttachment.getId()+"_"+fileName;
	        
	        /* Tạo file tại Server. */
    		File storeFileAtServer = new File(uploadRootDir.getAbsolutePath() + File.separator + docAttachment.filePath);
			
    		/* Ghi xuống file tại Server */
    		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(storeFileAtServer));
    		stream.write(fileBase64);
    		stream.close();
    		
    		return docAttachment;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
	
	public DocAttachment storeMedia(HttpServletRequest request, ReqDocAttachFile reqUploadFile) {
		try {
			DocAttachment docAttachment=new DocAttachment();
	 
	        File uploadRootDir = new File(fileLocationService.getPathAttachments());
			/* Tạo thư mục gốc upload nếu nó không tồn tại. */
	        if (!uploadRootDir.exists()) {
	            uploadRootDir.mkdirs();
	        }
	        
	        MultipartFile file = reqUploadFile.getFile();
	        if(file==null || file.isEmpty()) {
	        	throw new IOException("file đính kèm rỗng");
	        }
	        
			/* Tên file gốc tại Client. */
	        docAttachment.fileName=file.getOriginalFilename();
	        docAttachment.fileType=file.getContentType();
	        docAttachment.filePath=docAttachment.getId()+"_"+convertToAlias(file.getOriginalFilename());
	        
	        /* Tạo file tại Server. */
    		File storeFileAtServer = new File(uploadRootDir.getAbsolutePath() + File.separator + docAttachment.filePath);
			
    		/* Ghi xuống file tại Server */
    		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(storeFileAtServer));
    		stream.write(file.getBytes());
    		stream.close();
    		
    		docAttachment.createdTime=new Date();
    		docAttachment.updatedTime=new Date();
    		
    		return docAttachment;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
	
	public byte[] getFilePath(String path) {
		try {
			File uploadRootDir = new File(fileLocationService.getPathAttachments());
	        
	        /* Tạo file tại Server. */
    		File storeFileAtServer = new File(uploadRootDir.getAbsolutePath() + File.separator + path);
    		if(storeFileAtServer.exists()==false) {
    			return null;
    		}
    		
    		byte[] inFileBytes = Files.readAllBytes(Paths.get(storeFileAtServer.getPath())); 
    		//byte[] encoded = Base64.getMimeEncoder().encode(inFileBytes);
    		
    		return inFileBytes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public void delete(DocAttachment attachment) {
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					File uploadRootDir = new File(fileLocationService.getPathAttachments());
					File storeFileAtServer = new File(uploadRootDir.getAbsolutePath() + File.separator + attachment.filePath);
					if(storeFileAtServer.exists()) {
						try {
				            Files.delete(Paths.get(storeFileAtServer.getAbsolutePath()));
				            System.out.println("Đã xóa doc attachment ["+Paths.get(storeFileAtServer.getAbsolutePath())+"]");
				        } catch (IOException e) {
				            e.printStackTrace();
				        }
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.run();
	}
	
	public String convertToAlias(String fileName) {
		return TextUtil.removeAccent(fileName).replaceAll(" ", "_").toLowerCase();
	}
}