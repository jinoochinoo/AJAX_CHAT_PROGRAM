package chat;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import user.UserDAO;

/**
 * Servlet implementation class ChatBoxServlet
 */
@WebServlet("/chatBox")
public class ChatBoxServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		String userID = request.getParameter("userID");
		if(userID == null || userID.equals("")) {
			response.getWriter().write("");
		} else {
			try {
				
				// session 사용자 일치 체크
				HttpSession session = request.getSession();
				if(!userID.equals((String) session.getAttribute("userID"))){
					session.setAttribute("messageType", "오류 메시지");
					session.setAttribute("messageContent", "접근할 수 없습니다!");
					response.sendRedirect("index.jsp");
					return;
				}
				
				userID = URLDecoder.decode(userID, "UTF-8");
				response.getWriter().write(getBox(userID));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}		
	}

	public String getBox(String userID) {
		StringBuffer result = new StringBuffer("");
		result.append("{\"result\":[");
		ChatDAO chatDAO = new ChatDAO();
		ArrayList<ChatDTO> chatList = chatDAO.getBox(userID);
		Collections.reverse(chatList);
		
		if(chatList.size() == 0) return "";
		for(int i=0; i<chatList.size(); i++) {
			String unread = "";
			if(userID.equals(chatList.get(i).getToID())) {
				unread = chatDAO.getUnreadChat(chatList.get(i).getFromID(), userID) + "";
					if(unread.equals("0")) unread = " ";
			}
			String userProfile = "";
			if(userID.equals(chatList.get(i).getToID())) {
				userProfile = new UserDAO().getProfile(chatList.get(i).getFromID());
			} else {
				userProfile = new UserDAO().getProfile(chatList.get(i).getToID());
			}
			
			result.append("[{\"value\": \"" + chatList.get(i).getFromID() + "\"},");
			result.append("{\"value\": \"" + chatList.get(i).getToID() + "\"},");
			result.append("{\"value\": \"" + chatList.get(i).getChatContent() + "\"},");
			result.append("{\"value\": \"" + chatList.get(i).getChatTime() + "\"},");
			result.append("{\"value\": \"" + userProfile + "\"},");
			result.append("{\"value\": \"" + unread + "\"}]");
			if(i != chatList.size() -1) {
				result.append(",");
			}
		}
		result.append("], \"last\":\"" + chatList.get(chatList.size()-1).getChatID() + "\"}");
		return result.toString();
	}
}
