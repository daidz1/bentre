package ws.core.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import ws.core.model.Organization;
import ws.core.model.User;
import ws.core.model.UserOrganization;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.filter.UserFilter;
import ws.core.repository.OrganizationRepository;
import ws.core.repository.OrganizationRepositoryCustom;
import ws.core.repository.UserRepository;
import ws.core.repository.UserRepositoryCustom;
import ws.core.service.OrganizationService;

@Service
public class SyncOrganizationService {
	/* userId 6051883e2e85d71a67ca8319 */
	/* org Root 20705 */
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private OrganizationRepositoryCustom organizationRepositoryCustom;
	
	@Autowired
	private OrganizationService organizationService;
	
	public void syncOrganization() throws Exception {
		User user=userRepository.findById(new ObjectId("6051883e2e85d71a67ca8319")).get();
		
		/* Đồng bộ đơn vị ROOT 20705 */
		Connection conn = DatabaseUtil.getConnect();
        String sql="select * from organization_ where organizationId=?";
        PreparedStatement  stmt = conn.prepareStatement(sql);
		stmt.setLong(1, 20705);
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			long id=rs.getLong("organizationId");
			long parentId=rs.getLong("parentOrganizationId");
			String name=rs.getString("name");
			Date createdTime=rs.getDate("createDate");
			Date updatedTime=rs.getDate("modifiedDate");
			String type=rs.getString("type_");
			String orgIdMongoDB=rs.getString("orgIdMongoDB");
			
			storeOrganization(id, parentId, orgIdMongoDB, name, createdTime, updatedTime, type, user);
		}
		conn.close();
		
		/* Đồng bộ các đơn vị con của 20705 */
		syncSubOrganization(20705, user);
		
		/* Cập nhật lại các phòng ban cho Văn phòng UBND Tỉnh Bến Tre */
		updateOrganizationVanPhongUBNDTinh(user);
		
		/* Cập nhật lại path cho các organizaztion */
		updatePathForAll();
	}
	
	private void updatePathForAll() throws Exception {
		List<Organization> organizationAll=organizationRepository.findAll();
		for (Organization organization : organizationAll) {
			organization.path=organizationService.getPath(organization);
			organizationRepository.save(organization);
		}
	}
	
	private void updateOrganizationVanPhongUBNDTinh(User user) throws Exception{
		try {
			System.out.println("Cập nhật đơn vị cho Văn phòng UBND tỉnh");
			
			/* Tìm đơn vị Root UBND Tỉnh Bến Tre */
			String keyName="Văn phòng UBND tỉnh";
			Organization orgParent = organizationRepository.findByName(keyName).get();
			System.out.println("Parent: "+orgParent.getName());
			
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.parentId=orgParent.getParentId();
			
			List<Organization> orgTypeRooms=organizationRepositoryCustom.findAll(organizationFilter, 0, 0);
			for (Organization orgChild : orgTypeRooms) {
				if(orgChild.orgTypeMysql.equalsIgnoreCase("regular-room")) {
					orgChild.setParentId(orgParent.getId());
					organizationRepository.save(orgChild);
					System.out.println("--> Cập nhật parentId thành công cho đơn vị ["+ orgChild.getName()+"]");
				}
			}
			
			System.out.println("Cập nhật thành công đơn vị ["+ keyName+"]");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void syncSubOrganization(long parentOrganizationId, User user) throws Exception {
		/* Lay danh sach suborg */
		Connection conn = DatabaseUtil.getConnect();
		String sql="select * from organization_ where parentOrganizationId=?";
		PreparedStatement  stmt = conn.prepareStatement(sql);
		stmt.setLong(1, parentOrganizationId);
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			/* Luu vao csdl */
			long id=rs.getLong("organizationId");
			long parentId=rs.getLong("parentOrganizationId");
			String name=rs.getString("name");
			Date createdTime=rs.getDate("createDate");
			Date updatedTime=rs.getDate("modifiedDate");
			String type=rs.getString("type_");
			String orgIdMongoDB=rs.getString("orgIdMongoDB");
			
			storeOrganization(id, parentId, orgIdMongoDB, name, createdTime, updatedTime, type, user);
			
			/* De quy cho cac suborg */
			syncSubOrganization(id, user);
		}
		conn.close();
	}
	
	private void storeOrganization(long id, long parentId, String orgIdMongoDB, String name, Date createdTime, Date updatedTime, String type, User user) throws Exception {
		Organization organization = new Organization();
		/* Kiem tra da luu hay chua */
		if(StringUtils.isEmpty(orgIdMongoDB)==false) {
			Optional<Organization> _organization=organizationRepository.findById(new ObjectId(orgIdMongoDB));
			if(_organization.isPresent()) {
				organization=_organization.get();
			}
		}
		
		organization.createdTime=createdTime;
		organization.updatedTime=new Date();
		organization.name=name;
		organization.creatorId=user.getId();
		organization.creatorName=user.getFullName();
		String parentOrgIdMongoDB=getOrgIdMongoDB(parentId);
		if(StringUtils.isEmpty(parentOrgIdMongoDB)==false) {
			organization.parentId=parentOrgIdMongoDB;
		}
		organization.path=organizationService.getPath(organization);
		organization.active=true;
		organization.orgIdMysql=String.valueOf(id);
		organization.orgTypeMysql=type;
		
		organization=organizationRepository.save(organization);
		
		/* Cap nhat orgIdMongoDB vao Mysql */
		insertOrgIdMongoDB(id, organization.getId());
		
		System.out.println("Sync thanh cong org ["+ name+"]");
	}
	
	public void insertOrgIdMongoDB(long id, String orgIdMongoDB) throws SQLException {
		Connection conn = DatabaseUtil.getConnect();
        String sql="UPDATE `organization_` SET `orgIdMongoDB`=? WHERE `organizationId`=?";
        PreparedStatement  stmt = conn.prepareStatement(sql);
        stmt.setString(1, orgIdMongoDB);
		stmt.setLong(2, id);
        stmt.execute();
        conn.close();
	}
	
	public String getOrgIdMongoDB(long id) throws SQLException {
		String orgIdMongoDB=null;
		Connection conn = DatabaseUtil.getConnect();
        String sql="SELECT `orgIdMongoDB` FROM `organization_` WHERE `organizationId`=?";
        PreparedStatement  stmt = conn.prepareStatement(sql);
		stmt.setLong(1, id);
        ResultSet rs = stmt.executeQuery();
        while(rs.next()) {
        	orgIdMongoDB=rs.getString("orgIdMongoDB");
        }
        conn.close();
        
        return orgIdMongoDB;
	}
}
