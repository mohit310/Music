package com.mk.music.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class PlaylistServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//PrintWriter writer = null;
		HttpSession session = request.getSession();
		try {
			String file = request.getParameter("file");
			String directory = request.getParameter("directory");
			String isSession = request.getParameter("isSession");
			isSession = (isSession!=null)?isSession.trim():"false";
			List<File> files = new ArrayList<File>();
			if (Boolean.valueOf(isSession).booleanValue()) {
				List<File> playListDirAndFiles = (List<File>) session.getAttribute("playlistfiles");
				for (File fileOrDir : playListDirAndFiles) {
					if (fileOrDir.isDirectory()) {
						files.addAll(getAllFilesInDirAndSubdir(fileOrDir.getPath()));
					} else {
						files.add(fileOrDir);
					}
				}
				session.removeAttribute("playlistfiles");
			} else if (file != null && file.trim().length() > 0) {
				files.add(new File(directory + System.getProperty("file.separator") + file));
			} else {
				files = getAllFilesInDirAndSubdir(directory);
			}
			Collections.sort(files, new Comparator<File>() {
				@Override
				public int compare(File f1, File f2) {
					return Long.valueOf(f1.lastModified()).compareTo(Long.valueOf(f2.lastModified()));
				}
			});
			//writer.write(getPlayListData(request, response, files));
			request.setAttribute("playlist", files);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/playlist.jsp");
			dispatcher.forward(request, response);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	private List<File> getAllFilesInDirAndSubdir(String directory) {
		List<File> mp3Files = new ArrayList<File>();
		DirectoryFilter dirFilter = new DirectoryFilter();
		File f = new File(directory);
		if (f.isDirectory()) {
			File[] dirandfiles = f.listFiles();
			for (int i = 0; i < dirandfiles.length; i++) {
				File dirorfile = dirandfiles[i];
				if ((dirorfile.isDirectory()) && (!(dirorfile.isHidden())))
					mp3Files.addAll(getAllFilesInDirAndSubdir(dirorfile.getPath()));
				else if (dirFilter.acceptExtension(dirorfile)) {
					mp3Files.add(dirorfile);
				}
			}
		}
		return mp3Files;
	}

//	private String getPlayListData(HttpServletRequest request, HttpServletResponse response, List<File> audioFiles) {
//		StringBuffer playlist = new StringBuffer("#EXTM3U");
//		playlist.append("\n");
//		int i = 1;
//		for (File audioFile : audioFiles) {
//			playlist.append(getPlayListData(request, response, i, audioFile));
//			++i;
//		}
//		return playlist.toString();
//	}

//	private String getPlayListData(HttpServletRequest request, HttpServletResponse response, int num, File audioFile) {
//		StringBuffer buffer = new StringBuffer();
//		String fileName = audioFile.getName();
//		fileName = fileName.replaceAll("\\[", "");
//		fileName = fileName.replaceAll("\\]", "");
//		buffer.append("#EXTINF:" + num + "," + fileName  + "\n");
//		String file = "";
//		try {
//			file = audioFile.getPath();
//			file = URLEncoder.encode(file, "UTF-8");
//			file = file.replaceAll("\\+", "%20");
//		} catch (UnsupportedEncodingException e) {
//			file = StringEscapeUtils.escapeHtml(file);
//		}
//		String url = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/playService?file=" + file
//				+ "\n";
//		buffer.append(url);
//		return buffer.toString();
//	}

	// private String getTitle(File audioFile) {
	// String title = "";
	// try {
	// String file = audioFile.getName();
	// if (file.lastIndexOf(".") != -1) {
	// String extn = file.substring(file.lastIndexOf(".") + 1, file.length());
	// if (extn.equalsIgnoreCase("mp3")) {
	// AudioFile f = AudioFileIO.read(audioFile);
	// Tag tag = f.getTag();
	// title = tag.getFirstTitle();
	// }
	// }
	// } catch (CannotReadException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (TagException e) {
	// e.printStackTrace();
	// } catch (ReadOnlyFileException e) {
	// e.printStackTrace();
	// } catch (InvalidAudioFrameException e) {
	// e.printStackTrace();
	// }
	// title = ((title == null) || (title.trim().length() == 0)) ? audioFile.getName() : title;
	// return title;
	// }

}
