package user;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;


@WebServlet("/userProfile")
public class UserProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
 
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		MultipartRequest multi = null;
		int fileMaxSize = 10 * 1024 * 1024;
		String savePath = request.getRealPath("/upload").replaceAll("\\\\", "/");
		try {
			multi = new MultipartRequest(request, savePath, fileMaxSize, "UTF-8", new DefaultFileRenamePolicy());
		} catch(Exception e) {
			e.printStackTrace();
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "파일 크기는 10MB를 넘을 수 없습니다.");
			response.sendRedirect("profileUpload.jsp");
			return;
		}
		String userID = multi.getParameter("userID");
		// session 사용자 일치 여부 확인
		HttpSession session = request.getSession();
		if(!userID.equals((String) session.getAttribute("userID"))) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "잘못된 접근입니다!");
			response.sendRedirect("index.jsp");
			return;
		}
		String fileName = "";
		File file = multi.getFile("userProfile");
		if(file != null) {
			// 파일 확장자 확인
			String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
			if(ext.equals("jpg") || ext.equals("png") || ext.equals("gif") || ext.equals("JPG") || ext.equals("PNG") || ext.equals("GIF")) {
				// 기존 파일 삭제
				String prev = new UserDAO().getUser(userID).getUserProfile();
				File prevFile = new File(savePath + "/" + prev);
				if(prevFile.exists()) {
					prevFile.delete();
				}
				
				// 업로드 파일명 세팅
				fileName = file.getName();
			} else {
				file.delete();
				session.setAttribute("messageType", "오류 메시지");
				session.setAttribute("messageContent", "이미지  파일만 업로드 가능합니다!");
				response.sendRedirect("index.jsp");
				return;
			}
		}
		new UserDAO().profile(userID, fileName);
		session.setAttribute("messageType", "성공 메시지");
		session.setAttribute("messageContent", "파일 업로드 성공!");
		response.sendRedirect("index.jsp");
		return;
	}

}
