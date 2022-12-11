package ws.core.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ws.core.model.TaskAttachment;
import ws.core.model.request.ReqTaskAttachFile;
import ws.core.util.DateTimeUtil;
import ws.core.util.TextUtil;


@Service
public class TaskAttachmentService {
	
	@Autowired
	private FileLocationService fileLocationService;
	
	
	public TaskAttachment storeMedia(String fileName, String fileType, byte[] fileBase64) throws IOException {
		TaskAttachment media=new TaskAttachment();
		 
        File uploadRootDir = new File(fileLocationService.getPathAttachments());
		/* Tạo thư mục gốc upload nếu nó không tồn tại. */
        if (!uploadRootDir.exists()) {
            uploadRootDir.mkdirs();
        }
        
        /* Phân theo folder năm/tháng/ngày */
        String date=DateTimeUtil.getDateFolder().format(media.createdTime);
        
		/* Tên file gốc tại Client. */
        media.fileName=fileName;
        media.fileType=fileType;
        media.filePath=date+ "/"+ media.getId()+"_"+convertToAlias(fileName);
        
        File folderFile = new File(uploadRootDir.getAbsolutePath() + File.separator + date);
		if (!folderFile.exists()) {
			folderFile.mkdirs();
        }
		
        /* Tạo file tại Server. */
		File storeFileAtServer = new File(uploadRootDir.getAbsolutePath() + File.separator + media.filePath);
		
		/* Ghi xuống file tại Server */
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(storeFileAtServer));
		stream.write(fileBase64);
		stream.close();
		
		return media;
    }
	
	public TaskAttachment storeMedia(ReqTaskAttachFile reqAttachFile) throws IOException {
		TaskAttachment taskAttachment=new TaskAttachment();
		 
        File uploadRootDir = new File(fileLocationService.getPathAttachments());
		/* Tạo thư mục gốc upload nếu nó không tồn tại. */
        if (!uploadRootDir.exists()) {
            uploadRootDir.mkdirs();
        }
        
        MultipartFile file = reqAttachFile.getFile();
        if(file==null || file.isEmpty()) {
        	throw new IOException("file đính kèm rỗng");
        }
        
        /* Phân theo folder năm/tháng/ngày */
        String date=DateTimeUtil.getDateFolder().format(taskAttachment.createdTime);
        
		/* Tên file gốc tại Client. */
        taskAttachment.fileName=file.getOriginalFilename();
        taskAttachment.fileType=file.getContentType();
        taskAttachment.filePath=date+ "/"+ taskAttachment.getId()+"_"+ convertToAlias(file.getOriginalFilename());
        
        File folderFile = new File(uploadRootDir.getAbsolutePath() + File.separator + date);
		if (!folderFile.exists()) {
			folderFile.mkdirs();
        }
		
        /* Tạo file tại Server. */
		File storeFileAtServer = new File(uploadRootDir.getAbsolutePath() + File.separator + taskAttachment.filePath);
		
		/* Ghi xuống file tại Server */
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(storeFileAtServer));
		stream.write(file.getBytes());
		stream.close();
		
		return taskAttachment;
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
	
	
	public void delete(TaskAttachment attachment) {
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					File uploadRootDir = new File(fileLocationService.getPathAttachments());
					File storeFileAtServer = new File(uploadRootDir.getAbsolutePath() + File.separator + attachment.filePath);
					if(storeFileAtServer.exists()) {
						try {
				            Files.delete(Paths.get(storeFileAtServer.getAbsolutePath()));
				            System.out.println("Đã xóa task attachment ["+Paths.get(storeFileAtServer.getAbsolutePath())+"]");
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