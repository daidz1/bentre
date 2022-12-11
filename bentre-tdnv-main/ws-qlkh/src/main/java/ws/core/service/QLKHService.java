package ws.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ws.core.model.KhachHang;

@Service
public class QLKHService {

	@Value("${data.store.file.qlkh-json}")
	protected String qlkhJSON;
	
	public KhachHang get(String makh) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<KhachHang>> typeReference = new TypeReference<List<KhachHang>>(){};
		
		InputStream inputStream = new FileInputStream(new File(qlkhJSON));
		List<KhachHang> users = mapper.readValue(inputStream,typeReference);
		for (KhachHang khachHang : users) {
			if(khachHang.makhachhang.equals(makh)){
				return khachHang;
			}
		}
		
		throw new IOException("Không tồn tại trong hệ thống");
	}
}
