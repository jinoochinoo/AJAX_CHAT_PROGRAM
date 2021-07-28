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
	
		ArrayList<ChatDTO> chatList = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "SELECT * "
				+ "FROM chat "
				+ "WHERE ((fromID = ? AND toID =?) or (fromID = ? AND toID = ?)) "
				+ "AND chatID > (SELECT MAX(chatID) - ? "
				+ "FROM chat WHERE (fromID = ? AND toID = ?) OR (fromID = ? AND toID = ?))"
				+ "ORDER BY chatTime";
		
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, fromID);
			pstmt.setString(2, toID);
			pstmt.setString(3, toID);
			pstmt.setString(4, fromID);
			pstmt.setInt(5, number);
			pstmt.setString(6, fromID);
			pstmt.setString(7, toID);
			pstmt.setString(8, toID);
			pstmt.setString(9, fromID);
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

public ArrayList<ChatDTO> getBox(String userID){
	
	ArrayList<ChatDTO> chatList = null;
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String SQL = "SELECT * "
			+ "FROM chat "
			+ "WHERE chatID IN ("
			+ "SELECT MAX(chatID) "
			+ "FROM chat "
			+ "WHERE toID = ? AND fromID = ? "
			+ "GROUP BY fromID, toID)";
	
	try {
		conn = dataSource.getConnection();
		pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, userID);
		pstmt.setString(2, userID);
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
		for(int i=0; i<chatList.size(); i++) {
			ChatDTO chatX = chatList.get(i);
			for(int j=0; j<chatList.size(); j++) {
				ChatDTO chatY = chatList.get(j);
				if(chatX.getFromID().equals(chatY.getToID()) && chatX.getToID().equals(chatY.getFromID())) {
					if(chatX.getChatID() < chatY.getChatID()) {
						chatList.remove(chatX);
						i--;
						break;
					} else {
						chatList.remove(chatY);
						j--;
					}
				}
			}
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
	String SQL = "insert into chat values (seq_chatID.nextval, ?, ?, ?, sysdate, 0)";
	
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

public int readChat(String fromID, String toID) {
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String SQL = "UPDATE chat "
			+ "SET chatRead = 1 "
			+ "WHERE (fromID = ? AND toID =?)";
	try {
		conn = dataSource.getConnection();
		pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, toID);
		pstmt.setString(2, fromID);
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
	 return -1; // DB오류
	}

public int getAllUnreadChat(String userID) {
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String SQL = "SELECT COUNT(chatID) "
			+ "FROM chat "
			+ "WHERE toID = ? AND chatRead = 0";
	try {
		conn = dataSource.getConnection();
		pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, userID);
		rs = pstmt.executeQuery();
		if(rs.next()) {
			return rs.getInt("COUNT(chatID)");
		}
		return 0;
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
	 return -1; // DB오류
	}
}
