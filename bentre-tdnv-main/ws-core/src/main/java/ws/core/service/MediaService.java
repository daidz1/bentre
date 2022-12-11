package ws.core.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ws.core.model.Media;
import ws.core.model.Media.Category;
import ws.core.model.request.ReqUploadFile;
import ws.core.util.DateTimeUtil;
import ws.core.util.TextUtil;


@Service
public class MediaService {
	@Autowired
	protected FileLocationService fileLocationService;
	
	public String getLink(Media media) {
		return fileLocationService.getPathAttachments()+"/attachment/"+media.getPath();
	}
	
	public Media storeMedia(HttpServletRequest request, ReqUploadFile reqUploadFile) throws IOException{
		Media media=new Media();
		media.setCategory(Category.File);
		
		File uploadRootDir = new File(fileLocationService.getPathAttachments());
		/* Tạo thư mục gốc upload nếu nó không tồn tại. */
        if (!uploadRootDir.exists()) {
            uploadRootDir.mkdirs();
        }
        
        MultipartFile file = reqUploadFile.getFile();
        if(file==null || file.isEmpty()) {
        	throw new IOException("file empty");
        }
        
        /* Phân theo folder năm/tháng/ngày */
        String date=DateTimeUtil.getDateFolder().format(media.createdTime);
        
        /* Tên file gốc tại Client. */
        media.setName(file.getOriginalFilename());
        media.setSize(file.getSize());
        media.setType(file.getContentType());
        media.setPath(date + "/" + media.get_id().toString()+ "_" + convertToAlias(file.getOriginalFilename()));
		
        File folderFile = new File(uploadRootDir.getAbsolutePath() + File.separator + date);
		if (!folderFile.exists()) {
			folderFile.mkdirs();
        }
		
        /* Tạo file tại Server. */
		File storeFileAtServer = new File(uploadRootDir.getAbsolutePath() + File.separator + media.getPath());
		
		/* Ghi xuống file tại Server */
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(storeFileAtServer));
		stream.write(file.getBytes());
		stream.close();
		
		return media;
    }
	
	public String convertToAlias(String fileName) {
		return TextUtil.removeAccent(fileName).replaceAll(" ", "_").toLowerCase();
	}
}