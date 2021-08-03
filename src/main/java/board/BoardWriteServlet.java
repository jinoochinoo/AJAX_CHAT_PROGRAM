package board;

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


@WebServlet("/boardWrite")
public class BoardWriteServlet extends HttpServlet {
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
		
		String boardTitle = multi.getParameter("boardTitle");
		String boardContent = multi.getParameter("boardContent");
		if(boardTitle == null || boardTitle.equals("") || 
				boardContent == null || boardTitle.equals("")) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "제목, 내용을 모두 채워주세요!");
			response.sendRedirect("boardWrite.jsp");
			return;
		}
		String boardFile = "";
		String boardRealFile = "";
		File file = multi.getFile("boardFile");
		if(file != null) {
			boardFile = multi.getOriginalFileName("boardFile");
			boardRealFile = file.getName();
		}
		BoardDAO boardDAO = new BoardDAO();
		int result = boardDAO.write(userID, boardTitle, boardContent, boardFile, boardRealFile);
		if(result == 1) {
			session.setAttribute("messageType", "성공 메시지");
			session.setAttribute("messageContent", "게시물 작성 성공!");
			response.sendRedirect("boardView.jsp");
		} else {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "게시물 작성 실패!");
			response.sendRedirect("boardView.jsp");
		}
		return;
	}

}
