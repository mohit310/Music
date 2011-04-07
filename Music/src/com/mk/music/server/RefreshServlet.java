package com.mk.music.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RefreshServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MusicServiceImpl.refresh();
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter(); 
		writer.println("<html>");
		writer.println("<head>");
		writer.println("<title>Refreshed</title>");
		writer.println("</head>");
		writer.println("<body>");
		writer.println("<h3>Data Refreshed</h3>");
		writer.println("</body>");
		writer.println("</html>");
		response.flushBuffer();
	}

}
