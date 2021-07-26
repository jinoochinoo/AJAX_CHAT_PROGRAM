package chat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class ChatDAO {
	
	DataSource dataSource;

	public ChatDAO() {
		try {
			InitialContext initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			dataSource = (DataSource) envContext.lookup("jdbc/oracle");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<ChatDTO> getChatListByID(String fromID, String toID, String chatID){
		
		System.out.println("ChatDAO - getChatListByID - chatID : " + chatID);
		
		ArrayList<ChatDTO> chatList = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "select * "
				+ "from chat "
				+ ""
				+ "where ((fromID = ? and toID =?) or (fromID = ? and toID = ?)) "
				+ "and chatID > ? "
				+ "order by chatTime";
		
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, fromID);
			pstmt.setString(2, toID);
			pstmt.setString(3, toID);
			pstmt.setString(4, fromID);
			pstmt.setInt(5, Integer.parseInt(chatID));
			rs = pstmt.executeQuery();
			
			chatList = new ArrayList<ChatDTO>();
			while(rs.next()) {
				ChatDTO chat = new ChatDTO();
				chat.setChatID(rs.getInt("chatID"));
				chat.setFromID(rs.getString("fromID"));
				chat.setToID(rs.getString("toID"));
				chat.setChatContent(rs.getString("chatContent"));
				
				// 시간 format 제대로 되는지 나중에 확인
				int chatTime = Integer.parseInt(rs.getString("chatTime").substring(11, 13));
				
				String timeType = "오전";
				if(chatTime > 12) {
					timeType = "오후";
					chatTime -= 12;
				}
				chat.setChatTime(rs.getString("chatTime").substring(0, 11) +
						" " + timeType + " " + chatTime + ":" +
						rs.getString("chatTime").substring(14, 16) + "");				
				
				chatList.add(chat);
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
		return chatList;
	}
	
public ArrayList<ChatDTO> getChatListByRecent(String fromID, String toID, int number){

	System.out.println("ChatDAO - getChatListByRecent - number : " + number);
	
		ArrayList<ChatDTO> chatList = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "select * "
				+ "from chat "
				+ ""
				+ "where ((fromID = ? and toID =?) or (fromID = ? and toID = ?)) "
				+ "and chatID > (select max(chatID) - ? from chat) "
				+ "order by chatTime";
		
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, fromID);
			pstmt.setString(2, toID);
			pstmt.setString(3, toID);
			pstmt.setString(4, fromID);
			pstmt.setInt(5, number);
			rs = pstmt.executeQuery();
			
			chatList = new ArrayList<ChatDTO>();
			while(rs.next()) {
				ChatDTO chat = new ChatDTO();
				chat.setChatID(rs.getInt("chatID"));
				chat.setFromID(rs.getString("fromID"));
				chat.setToID(rs.getString("toID"));
				chat.setChatContent(rs.getString("chatContent"));
				
				// 시간 format 제대로 되는지 나중에 확인
				int chatTime = Integer.parseInt(rs.getString("chatTime").substring(11, 13));
				String timeType = "오전";
				if(chatTime > 12) {
					timeType = "오후";
					chatTime -= 12;
				}
				chat.setChatTime(rs.getString("chatTime").substring(0, 11) +
						" " + timeType + " " + chatTime + ":" +
						rs.getString("chatTime").substring(14, 16) + "");				
				
				chatList.add(chat);
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
		return chatList;
	}

public int submit(String fromID, String toID, String chatContent){
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String SQL = "insert into chat values (seq_chatID.nextval, ?, ?, ?, sysdate)";
	
	try {
		conn = dataSource.getConnection();
		pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, fromID);
		pstmt.setString(2, toID);
		pstmt.setString(3, chatContent);
		return pstmt.executeUpdate();
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
	return -1; // DB 오류
	}
}
