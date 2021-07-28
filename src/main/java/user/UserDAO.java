package user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class UserDAO {
	
	DataSource dataSource;

	public UserDAO() {
		try {
			InitialContext initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			dataSource = (DataSource) envContext.lookup("jdbc/oracle");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public int login(String userID, String userPassword) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "select * from userInfo where userID = ?";
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				if(rs.getString("userPassword").equals(userPassword)) {
					return 1; // 로그인 성공
				} else {
					return 2; // 비밀번호 틀림
				}
			} else {
				return 0; // 해당 사용자 없음
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		// 중간에 return 안 됐으면 DB 오류
		return -1;
	}
	
	public int registerCheck(String userID) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "select * from userInfo where userID = ?";
		System.out.println("registerCheck - userID : " + userID);
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			if(rs.next() || userID.equals("")) {
				return 0; // 이미 등록된 회원
			} else {
				return 1; // 가입 가능한 회원
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		// 중간에 return 안 됐으면 DB 오류
		return -1;
	}
	
	public int register(String userID, String userPassword, String userName, int userAge, String userGender, String userEmail, String userProfile) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String SQL = "insert into userInfo values (?, ?, ?, ?, ?, ?, ?)";
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			pstmt.setString(2, userPassword);
			pstmt.setString(3, userName);
			pstmt.setInt(4, userAge);
			pstmt.setString(5, userGender);
			pstmt.setString(6, userEmail);
			pstmt.setString(7, userProfile);
			return  pstmt.executeUpdate();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		// 중간에 return 안 됐으면 DB 오류
		return -1;
	}	
}
