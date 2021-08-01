package user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/userUpdate")
public class UserUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		String userID = request.getParameter("userID");
		String userPassword1 = request.getParameter("userPassword1");
		String userPassword2 = request.getParameter("userPassword2");
		String userName = request.getParameter("userName");
		String userGender = request.getParameter("userGender");
		String userEmail = request.getParameter("userEmail");
		
		// 입력 안 된 값 확인
		if(userID == null || userID.equals("") || userPassword1 == null || userPassword1.equals("") || 
				userPassword2 == null || userPassword2.equals("") || userName == null || userName.equals("") || 
						userGender == null || userGender.equals("") || userEmail == null || userEmail.equals("") || 
								request.getParameter("userAge") == null || request.getParameter("userAge").equals("")) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "모든 내용을 입력하세요!");
			response.sendRedirect("update.jsp");
			return;
		}
		
		// session 사용자 일치 여부 확인
		HttpSession session = request.getSession();
		if(!userID.equals((String) session.getAttribute("userID"))) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "잘못된 접근입니다!");
			response.sendRedirect("index.jsp");
			return;
		}
		
		// 입력 완료된 후 type 변환
		int userAge = Integer.parseInt(request.getParameter("userAge"));
		
		if(!userPassword1.equals(userPassword2)) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "비밀번호가 서로 다릅니다!");
			response.sendRedirect("update.jsp");
			return;
		}
 		int result = new UserDAO().update(userID, userPassword1, userName, userAge, userGender, userEmail);
		if(result == 1) {
			request.getSession().setAttribute("userID", userID);
			request.getSession().setAttribute("messageType", "성공 메시지");
			request.getSession().setAttribute("messageContent", "회원정보 수정에 성공했습니다!");
			response.sendRedirect("index.jsp");
			return;
		} else {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "DB 오류 발생!");
			response.sendRedirect("update.jsp");
			return;
		}
	}

}
