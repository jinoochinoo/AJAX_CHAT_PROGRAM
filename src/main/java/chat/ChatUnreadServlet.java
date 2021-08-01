package chat;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/chatUnreadServlet")
public class ChatUnreadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		String userID = request.getParameter("userID");
		if(userID == null || userID.equals("")) {
			response.getWriter().write("0");
		} else {
			
			// session 사용자 일치 체크
			HttpSession session = request.getSession();
			if(!userID.equals((String) session.getAttribute("userID"))){
				session.setAttribute("messageType", "오류 메시지");
				session.setAttribute("messageContent", "접근할 수 없습니다!");
				response.sendRedirect("index.jsp");
				return;
			}
			
			userID = URLDecoder.decode(userID, "UTF-8");
			response.getWriter().write(new ChatDAO().getAllUnreadChat(userID) + "");
		}
	}
}
