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

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ws.core.model.Organization;
import ws.core.model.OrganizationRole;
import ws.core.model.User;
import ws.core.model.UserOrganization;
import ws.core.model.embeded.UserOrganizationExpand;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.filter.OrganizationRoleFilter;
import ws.core.model.filter.UserFilter;
import ws.core.repository.OrganizationRepository;
import ws.core.repository.OrganizationRepositoryCustom;
import ws.core.repository.OrganizationRoleRepository;
import ws.core.repository.OrganizationRoleRepositoryCustom;
import ws.core.repository.UserRepository;
import ws.core.repository.UserRepositoryCustom;

@Service
public class SyncUserService {
	/* userId 6051883e2e85d71a67ca8319 */
	/* org Root 20705 */
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRepositoryCustom userRepositoryCustom;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private OrganizationRepositoryCustom organizationRepositoryCustom;
	
	@Autowired
	private OrganizationRoleRepository organizationRoleRepository;
	
	@Autowired
	private OrganizationRoleRepositoryCustom organizationRoleRepositoryCustom;
	
	
	public void syncUser() throws Exception {
		User admin=userRepository.findById(new ObjectId("6051883e2e85d71a67ca8319")).get();
		
		/* Lay don vi ROOT */
		Connection conn = DatabaseUtil.getConnect();
        String sql="select * from user_ where 1";
        PreparedStatement  stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			long id=rs.getLong("userId");
			Date createdTime=rs.getDate("createDate");
			String fullName=rs.getString("firstName");
			String jobTitle=rs.getString("jobTitle");
			String username=rs.getString("screenName");
			String email=rs.getString("emailAddress");
			int status=rs.getInt("status");
			String userIdMongoDB=rs.getString("userIdMongoDB");
			
			storeUser(id, userIdMongoDB, createdTime, fullName, jobTitle, username, email, status, admin);
		}
		conn.close();
		
		/* Chuyển chuyên viên lên tổ chức UBND Tỉnh và gán lãnh đạo */
		moveUserAndSetLeader();
	}
	
	private void moveUserAndSetLeader() {
		/* Tìm tổ chức Văn phòng UBND tỉnh */
		Optional<Organization> _organization=organizationRepository.findByName("Văn phòng UBND tỉnh");
		if(_organization.isPresent()) {
			/* Tìm các tổ chức, phòng ban của Văn phòng UBNd tỉnh */
			Organization organization=_organization.get();
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.parentId=organization.getId();
			
			LinkedList<String> orgIds=new LinkedList<String>();
			List<Organization> organizationList=organizationRepositoryCustom.findAll(organizationFilter, 0, 0);
			for (Organization org : organizationList) {
				orgIds.add(org.getId());
				System.out.println("- Phòng ban: "+org.getName());
			}
			
			/* Nếu có các phòng ban thì */
			if(orgIds.size()>0) {
				/* Tìm tổ chức UBND Tỉnh Bến Tre */
				Optional<Organization> _orgUBNDTinhBenTre=organizationRepository.findByName("UBND Tỉnh Bến Tre");
				if(_orgUBNDTinhBenTre.isPresent()) {
					Organization orgUBNDTinhBenTre=_orgUBNDTinhBenTre.get();
					
					/* Tìm các tài khoản lãnh đạo */
					UserFilter userFilterLeader=new UserFilter();
					userFilterLeader.organizationIds=Arrays.asList(orgUBNDTinhBenTre.getId());
					
					List<UserOrganization> leaders=new ArrayList<UserOrganization>();
					List<User> userLeaders=userRepositoryCustom.findAll(userFilterLeader, 0, 0);
					for (User user : userLeaders) {
						if(user.getJobTitle().equalsIgnoreCase("Chủ tịch") || user.getJobTitle().equalsIgnoreCase("Phó Chủ tịch")) {
							UserOrganization leader=new UserOrganization();
							leader.userId=user.getId();
							leader.fullName=user.getFullName();
							leader.organizationId=orgUBNDTinhBenTre.getId();
							leader.organizationName=orgUBNDTinhBenTre.getName();
							
							leaders.add(leader);
							
							System.out.println("-> Các lãnh đạo: "+user.getFullName());
						}
					}
					
					
					/*--------------------------------------*/
					/* Tìm các user trong các phòng ban */
					UserFilter userFilter=new UserFilter();
					userFilter.organizationIds=orgIds;
					
					OrganizationRoleFilter organizationRoleFilter=new OrganizationRoleFilter();
					organizationRoleFilter.organizationIds=Arrays.asList(orgUBNDTinhBenTre.getId());
					organizationRoleFilter.keySearch="Giao nhiệm vụ";
					
					try {
						OrganizationRole organizationRole=organizationRoleRepositoryCustom.findOne(organizationRoleFilter).get();
						
						/* Duyệt qua các user */
						List<User> users=userRepositoryCustom.findAll(userFilter, 0, 0);
						for (User user : users) {
							boolean exists=false;
							for(UserOrganizationExpand item:user.getOrganizations()) {
								if(item.getOrganizationId().equals(orgUBNDTinhBenTre.getId())) {
									exists=true;
								}
							}
							
							if(exists==false) {
								UserOrganizationExpand userOrganizationExpand=new UserOrganizationExpand();
								userOrganizationExpand.setOrganizationId(orgUBNDTinhBenTre.getId());
								userOrganizationExpand.setOrganizationName(orgUBNDTinhBenTre.getName());
								user.organizations.add(userOrganizationExpand);
							}
							
							user.leaders.addAll(leaders);
							userRepository.save(user);
							
							if(organizationRole.userIds.contains(user.getId())==false) {
								organizationRole.userIds.add(user.getId());
							}
							System.out.println("Đã thêm user ["+user.getFullName()+"] vào đơn vị "+orgUBNDTinhBenTre.getName()+" và gán lãnh đạo");
						}
						
						organizationRoleRepository.save(organizationRole);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private void storeUser(long id, String userIdMongoDB,  Date createdTime, String fullName, String jobTitle, String username, String email, int status, User admin) throws Exception {
		User user = new User();
		/* Kiem tra da luu hay chua */
		if(StringUtils.isEmpty(userIdMongoDB)==false) {
			Optional<User> _user=userRepository.findById(new ObjectId(userIdMongoDB));
			if(_user.isPresent()) {
				user=_user.get();
			}
		}
		
		user.createdTime=createdTime;
		user.updatedTime=new Date();
		user.fullName=fullName;
		user.creatorId=user.getId();
		user.creatorName=user.getFullName();
		user.username=username;
		user.email=email;
		user.jobTitle=jobTitle;
		user.active=true;
		user.userIdMysql=String.valueOf(id);
		user.statusMysql=String.valueOf(status);
		user.accountDomino=getAccountIOffice(id);
		user.password=passwordEncoder.encode("abc123");
		
		
		
		/* Danh sách các đơn vị tk thuộc */
		LinkedList<String> organizationIds=getOrgIdsMongoDB(id);
		for (String organizationId : organizationIds) {
			/* To chuc */
			boolean exists=false;
			for(UserOrganizationExpand item:user.getOrganizations()) {
				if(item.getOrganizationId().equals(organizationId)) {
					exists=true;
				}
			}
			
			if(exists==false) {
				try {
					Organization organization=organizationRepository.findById(new ObjectId(organizationId)).get();
					
					UserOrganizationExpand userOrganizationExpand=new UserOrganizationExpand();
					userOrganizationExpand.setOrganizationId(organization.getId());
					userOrganizationExpand.setOrganizationName(organization.getName());
					user.organizations.add(userOrganizationExpand);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		user=userRepository.save(user);
		
		/* Cap nhat userIdMongoDB vao Mysql */
		updateUserIdMongoDB(id, user.getId());
		
		System.out.println("Sync thanh cong user: "+ fullName);
	}
	
	private String getAccountIOffice(long userId) throws SQLException{
		String userIdMongoDB=null;
		Connection conn = DatabaseUtil.getConnect();
        String sql="SELECT DISTINCT C.`data_`, D.`userId` FROM `expandovalue` C INNER JOIN (SELECT A.`rowId_`, A.`tableId`, A.`classPK`, B.`userId`, B.`userIdMongoDB` FROM `expandorow` A INNER JOIN (SELECT `userId`,`userIdMongoDB` FROM `user_`) B ON A.classPK=B.userId) D ON C.`rowId_`= D.`rowId_` WHERE `classNameId`= 20005 AND `columnId` = 21902 AND D.`userId`=?";
        PreparedStatement  stmt = conn.prepareStatement(sql);
		stmt.setLong(1, userId);
        ResultSet rs = stmt.executeQuery();
        while(rs.next()) {
        	userIdMongoDB=rs.getString("data_");
        }
        conn.close();
        
        return userIdMongoDB;
	}
	
	private void updateUserIdMongoDB(long id, String userIdMongoDB) throws SQLException {
		Connection conn = DatabaseUtil.getConnect();
        String sql="UPDATE `user_` SET `userIdMongoDB`=? WHERE `userId`=?";
        PreparedStatement  stmt = conn.prepareStatement(sql);
        stmt.setString(1, userIdMongoDB);
		stmt.setLong(2, id);
        stmt.execute();
        conn.close();
	}
	
	public String getUserIdMongoDB(long id) throws SQLException {
		String userIdMongoDB=null;
		Connection conn = DatabaseUtil.getConnect();
        String sql="SELECT `userIdMongoDB` FROM `user_` WHERE `userId`=?";
        PreparedStatement  stmt = conn.prepareStatement(sql);
		stmt.setLong(1, id);
        ResultSet rs = stmt.executeQuery();
        while(rs.next()) {
        	userIdMongoDB=rs.getString("userIdMongoDB");
        }
        conn.close();
        
        return userIdMongoDB;
	}
	
	private LinkedList<String> getOrgIdsMongoDB(long id){
		LinkedList<String> result=new LinkedList<String>();
		try {
			Connection conn = DatabaseUtil.getConnect();
            String sql="SELECT DISTINCT(`orgIdMongoDB`) FROM `organization_` WHERE `organizationId` IN(SELECT `organizationId` FROM `users_orgs` WHERE `userId`=?)";
            PreparedStatement  stmt = conn.prepareStatement(sql);
    		stmt.setLong(1, id);
    		ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
            	result.add(rs.getString("orgIdMongoDB"));
            }
            conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
