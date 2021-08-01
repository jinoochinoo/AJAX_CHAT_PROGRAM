package user;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/userFind")
public class UserFindServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		String userID = request.getParameter("userID");
		
		// 입력 안 된 값 확인
		if(userID == null || userID.equals("")) {
			response.getWriter().write("-1");
		} else if(new UserDAO().registerCheck(userID) == 0) { // 회원가입 체크
			try {
				response.getWriter().write(find(userID));
			} catch (Exception e) {
				e.printStackTrace();
				response.getWriter().write("-1");				
			}
		} else {
			response.getWriter().write("-1");
		}
	}
	
	public String find(String userID) throws Exception {
		StringBuffer result = new StringBuffer("");
		result.append("{\"userProfile\":\"" + new UserDAO().getProfile(userID) + "\"}");		
		return result.toString();
	}
}

