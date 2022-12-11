package ws.core.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ws.core.model.Organization;
import ws.core.model.OrganizationRole;
import ws.core.model.User;
import ws.core.model.filter.UserFilter;
import ws.core.repository.OrganizationRepository;
import ws.core.repository.OrganizationRoleRepository;
import ws.core.repository.UserRepository;
import ws.core.repository.UserRepositoryCustom;

@Service
public class SyncRoleService {
	/* userId 6051883e2e85d71a67ca8319 */
	/* org Root 20705 */
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRepositoryCustom userRepositoryCustom;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private OrganizationRoleRepository organizationRoleRepository;
	
	public void syncRole() throws Exception {
		User admin=userRepository.findById(new ObjectId("6051883e2e85d71a67ca8319")).get();
		
		List<Organization> organizations=organizationRepository.findAll();
		for (Organization organization : organizations) {
			/* Kiểm tra chỉ thêm cho các đơn vị đồng bộ qua */
			if(StringUtils.isEmpty(organization.getOrgIdMysql())==false && StringUtils.isEmpty(organization.getOrgTypeMysql())==false) {
				System.out.println("Create organization role for: ["+organization.getName()+"]");
				/* Quản trị đơn vị */
				OrganizationRole organizationRoleQTDV=new OrganizationRole();
				organizationRoleQTDV.creatorId=admin.getId();
				organizationRoleQTDV.creatorName=admin.getFullName();
				organizationRoleQTDV.name="Quản trị tổ chức";
				organizationRoleQTDV.description="Cho phép người quản trị có thể quản lý tổ chức, người dùng trong đơn vị";
				organizationRoleQTDV.organizationId=organization.getId();
				organizationRoleQTDV.userIds=new LinkedList<String>();
				organizationRoleQTDV.permissionKeys.addAll(Arrays.asList("quanly_nguoidung","quanly_donvi","quanly_vaitro"));
				
				organizationRoleQTDV=organizationRoleRepository.save(organizationRoleQTDV);
				System.out.println("+ "+organizationRoleQTDV.getName());
				
				/* Lãnh đạo đơn vị */
				OrganizationRole organizationRoleLDDV=new OrganizationRole();
				organizationRoleLDDV.creatorId=admin.getId();
				organizationRoleLDDV.creatorName=admin.getFullName();
				organizationRoleLDDV.name="Lãnh đạo (trưởng)";
				organizationRoleLDDV.description="Người lãnh đạo đơn vị, cơ quan, tổ chức, trường phòng, ....";
				organizationRoleLDDV.organizationId=organization.getId();
				organizationRoleLDDV.userIds=setRoleFor(organization, Arrays.asList("chutich", "lanhdaoubndtinh", "lanhdaodonvi","lanhdaophongban"));
				organizationRoleLDDV.permissionKeys.addAll(Arrays.asList("truongdonvi"));
				
				organizationRoleLDDV=organizationRoleRepository.save(organizationRoleLDDV);
				System.out.println("+ "+organizationRoleLDDV.getName());
				
				/* Phó Lãnh đạo đơn vị */
				OrganizationRole organizationRolePLDDV=new OrganizationRole();
				organizationRolePLDDV.creatorId=admin.getId();
				organizationRolePLDDV.creatorName=admin.getFullName();
				organizationRolePLDDV.name="Lãnh đạo (phó)";
				organizationRolePLDDV.description="Người lãnh đạo phó đơn vị, cơ quan, tổ chức, phó phòng, ....";
				organizationRolePLDDV.organizationId=organization.getId();
				organizationRolePLDDV.userIds=setRoleFor(organization, Arrays.asList("phochutich"));
				organizationRolePLDDV.permissionKeys.addAll(Arrays.asList("photruongdonvi"));
				
				organizationRolePLDDV=organizationRoleRepository.save(organizationRolePLDDV);
				System.out.println("+ "+organizationRolePLDDV.getName());
				
				/* Giao nhiệm vụ */
				OrganizationRole organizationRoleGNV=new OrganizationRole();
				organizationRoleGNV.creatorId=admin.getId();
				organizationRoleGNV.creatorName=admin.getFullName();
				organizationRoleGNV.name="Giao nhiệm vụ";
				organizationRoleGNV.description="Giao nhiệm vụ đến các tài khoản trong tổ chức hoặc cấp dưới (nếu có)";
				organizationRoleGNV.organizationId=organization.getId();
				organizationRoleGNV.userIds=setRoleFor(organization, Arrays.asList("chutich", "phochutich", "lanhdaoubndtinh", "lanhdaodonvi","lanhdaophongban", "biensoanchidao"));
				organizationRoleGNV.permissionKeys.addAll(Arrays.asList("giaonhiemvu"));
				
				organizationRoleGNV=organizationRoleRepository.save(organizationRoleGNV);
				System.out.println("+ "+organizationRoleGNV.getName());
				
				/* Phân nhiệm vụ đơn vị */
				OrganizationRole organizationRolePNVDV=new OrganizationRole();
				organizationRolePNVDV.creatorId=admin.getId();
				organizationRolePNVDV.creatorName=admin.getFullName();
				organizationRolePNVDV.name="Phân nhiệm vụ đơn vị";
				organizationRolePNVDV.description="Người có vai trò sẽ phân các nhiệm vụ được cấp trên giao cho đơn vị cho 1 tài khoản trong đơn vị để thực hiện";
				organizationRolePNVDV.organizationId=organization.getId();
				organizationRolePNVDV.userIds=setRoleFor(organization, Arrays.asList("lanhdaodonvi","capnhatchidao"));
				organizationRolePNVDV.permissionKeys.addAll(Arrays.asList("phanhiemvudonvi"));
				
				organizationRolePNVDV=organizationRoleRepository.save(organizationRolePNVDV);
				System.out.println("+ "+organizationRolePNVDV.getName());
				
				/* Quản lý văn bản IOffice */
				OrganizationRole organizationRoleQLVB=new OrganizationRole();
				organizationRoleQLVB.creatorId=admin.getId();
				organizationRoleQLVB.creatorName=admin.getFullName();
				organizationRoleQLVB.name="Quản lý văn bản (liên quan)";
				organizationRoleQLVB.description="Người dùng quản lý văn bản liên quan và thêm văn bản để giao nhiệm vụ";
				organizationRoleQLVB.organizationId=organization.getId();
				/* Nếu là đơn vị root và đơn vị văn phòng ubnd tỉnh và các đơn vị con của văn phòng ubnd tỉnh 62ff36572ba1c81e8396edcf ID của VPUBND Tỉnh*/
				if(organization.parentId.isEmpty() || organization.path.contains("62ff36572ba1c81e8396edcf")) {
					organizationRoleQLVB.userIds=setRoleFor(organization, Arrays.asList("chutich", "phochutich", "lanhdaoubndtinh", "biensoanchidao"));
				}else {
					organizationRoleQLVB.userIds=new LinkedList<String>();
				}
				organizationRoleQLVB.permissionKeys.addAll(Arrays.asList("themvanban","xemvanban"));
				
				organizationRoleQLVB=organizationRoleRepository.save(organizationRoleQLVB);
				System.out.println("+ "+organizationRoleQLVB.getName());
				
				/* Quản lý văn bản đơn vị */
				OrganizationRole organizationRoleQLVBDV=new OrganizationRole();
				organizationRoleQLVBDV.creatorId=admin.getId();
				organizationRoleQLVBDV.creatorName=admin.getFullName();
				organizationRoleQLVBDV.name="Xem tất cả văn bản của đơn vị";
				organizationRoleQLVBDV.description="Được phép xem tất cả văn bản của đơn vị";
				organizationRoleQLVBDV.organizationId=organization.getId();
				organizationRoleQLVBDV.userIds=setRoleFor(organization, Arrays.asList("chutich", "phochutich", "lanhdaoubndtinh", "theodoichidaokhac"));
				organizationRoleQLVBDV.permissionKeys.addAll(Arrays.asList("xemvanbandonvi"));
				
				organizationRoleQLVBDV=organizationRoleRepository.save(organizationRoleQLVBDV);
				System.out.println("+ "+organizationRoleQLVBDV.getName());
				
				/* Xem nhiệm vụ đơn vị */
				OrganizationRole organizationRoleXNVDV=new OrganizationRole();
				organizationRoleXNVDV.creatorId=admin.getId();
				organizationRoleXNVDV.creatorName=admin.getFullName();
				organizationRoleXNVDV.name="Xem tất cả nhiệm vụ của đơn vị";
				organizationRoleXNVDV.description="Được phép xem tất cả nhiệm vụ của đơn vị (cá nhân, đơn vị): đã giao, được giao, hỗ trợ";
				organizationRoleXNVDV.organizationId=organization.getId();
				organizationRoleXNVDV.userIds=new LinkedList<String>();
				organizationRoleXNVDV.permissionKeys.addAll(Arrays.asList("xemnhiemvudonvi"));
				
				organizationRoleXNVDV=organizationRoleRepository.save(organizationRoleXNVDV);
				System.out.println("+ "+organizationRoleXNVDV.getName());
				
				/* Không nhận việc */
				OrganizationRole organizationRoleKNV=new OrganizationRole();
				organizationRoleKNV.creatorId=admin.getId();
				organizationRoleKNV.creatorName=admin.getFullName();
				organizationRoleKNV.name="Không nhận việc";
				organizationRoleKNV.description="Tài khoản sẽ không hiển thị trong danh sách chọn cá bộ xử lý/theo dõi nhiệm vụ";
				organizationRoleKNV.organizationId=organization.getId();
				organizationRoleKNV.userIds=new LinkedList<String>();
				organizationRoleKNV.permissionKeys.addAll(Arrays.asList("khongnhanviec"));
				
				organizationRoleKNV=organizationRoleRepository.save(organizationRoleKNV);
				System.out.println("+ "+organizationRoleKNV.getName());
				
				System.out.println();
			}
		}
	}
	
	private LinkedList<String> setRoleFor(Organization organization, List<String> roleOlds) {
		UserFilter userFilter=new UserFilter();
		userFilter.organizationIds.add(organization.getId());
		
		LinkedList<String> userIds=new LinkedList<String>();
		List<User> users=userRepositoryCustom.findAll(userFilter, 0, 0);
		
		/* Lặp qua từng user trong tổ chức đang có */
		for (User user : users) {
			/* Nếu là user Mysql */
			if(StringUtils.isEmpty(user.getUserIdMysql())==false) {
				/* Lấy danh sách role của user */
				List<String> rolesOfUser=getListRoleForUser(Long.parseLong(user.getUserIdMysql()));
				/* Kiểm tra role của user có ~ roleOld không */
				for (String roleName : rolesOfUser) {
					/* Nếu có thì lưu lại userId và tiếp tục user mới */
					if(roleOlds.contains(roleName)) {
						if(userIds.contains(user.getId())==false) {
							userIds.add(user.getId());
						}
						break;
					}
				}
			}
		}
		return userIds;
	}
	
	
	private LinkedList<String> getListRoleForUser(long id){
		LinkedList<String> result=new LinkedList<String>();
		try {
			Connection conn = DatabaseUtil.getConnect();
            String sql="SELECT DISTINCT(`name`) FROM `role_` A INNER JOIN `users_roles` B ON A.`roleId`=B.`roleId` WHERE B.`userId`=?";
            PreparedStatement  stmt = conn.prepareStatement(sql);
    		stmt.setLong(1, id);
    		ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
            	result.add(rs.getString("name"));
            }
            conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
