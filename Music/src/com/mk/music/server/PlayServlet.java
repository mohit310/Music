package com.mk.music.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PlayServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		OutputStream stream = null;
		BufferedInputStream inputStream = null;
		try {
			String file = request.getParameter("file");
			if(file!=null){
				stream = response.getOutputStream();
				File mp3File = new File(file);
				response.setContentType("audio/mpeg");
				response.setContentLength(Long.valueOf(mp3File.length()).intValue());
				inputStream = new BufferedInputStream(new FileInputStream(mp3File));
				byte[] filebuffer = new byte[8192];
				while (inputStream.read(filebuffer) != -1)
					stream.write(filebuffer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stream != null) {
					stream.flush();
					stream.close();
				}
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
