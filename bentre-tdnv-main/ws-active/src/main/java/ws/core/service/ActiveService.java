package ws.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ws.core.model.Active;

@Service
public class ActiveService {

	@Value("${data.store.file.active-json}")
	protected String activeJSON;
	
	public Active getActive() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<Active> typeReference = new TypeReference<Active>(){};
		
		InputStream inputStream = new FileInputStream(new File(activeJSON));
		Active active = mapper.readValue(inputStream,typeReference);
		return active;
	}
	
	
	public byte[] getFileBytesCert(String path) {
		try {
    		File storeFileAtServer = new File(path);
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
}
