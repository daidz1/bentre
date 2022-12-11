package ws.core.mysql;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseUtil {
	private static String DB_URL = "jdbc:mysql://192.168.1.202:3306/bentre_liferay_portal";
	private static String USER_NAME = "root";
	private static String PASSWORD = "P@ssw0rd!@#";

	public static Connection getConnection(String dbURL, String userName, String password) {
		Connection conn = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(dbURL, userName, password);
			System.out.println("connect successfully!");
		} catch (Exception ex) {
			System.out.println("connect failure!");
			ex.printStackTrace();
		}
		return conn;
	}

	public static Connection getConnect() {
		try {
			return getConnection(DB_URL, USER_NAME, PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ResultSet getOrganizations(long orgId) {
		ResultSet rs=null;
		try {
			Connection conn = getConnect();
            String sql="select * from organization_ where organizationId = ?";
            PreparedStatement  stmt = conn.prepareStatement(sql);
			stmt.setLong(1, orgId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("+ organizationId: "+rs.getInt("organizationId"));
                System.out.println("+ name: "+rs.getString("name"));
                System.out.println();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public static void getListOrganizations(String orgIds) {
		try {
			Connection conn = getConnect();
			Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from organization_");
            while (rs.next()) {
                System.out.println("+ organizationId: "+rs.getInt("organizationId"));
                System.out.println("+ name: "+rs.getString("name"));
                System.out.println();
            }
            conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ResultSet getListSubOrganizations(long parentId) {
		ResultSet rs=null;
		try {
			Connection conn = getConnect();
			String sql="select * from organization_ where parentOrganizationId = ?";
			PreparedStatement  stmt = conn.prepareStatement(sql);
			stmt.setLong(1, parentId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("+ organizationId: "+rs.getInt("organizationId"));
                System.out.println("+ name: "+rs.getString("name"));
                System.out.println();
            }
            conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public static void getListUsers(String orgIds) {
		try {
			Connection conn = getConnect();
			Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from user_");
            while (rs.next()) {
                System.out.println("+ userId: "+rs.getInt("userId"));
                System.out.println("+ fullName: "+rs.getString("firstname"));
                System.out.println();
            }
            conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
