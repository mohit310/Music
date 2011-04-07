package com.mk.music.server;

import java.io.File;
import java.io.FileFilter;

public class DirectoryFilter implements FileFilter {

	public boolean accept(File file) {
		return ((((!(file.isDirectory())) || (file.isHidden()))) && (!(acceptExtension(file))));
	}

	public boolean acceptExtension(File file) {
		if (file.isFile()) {
			String fileName = file.getName();
			int fileIndex = fileName.lastIndexOf(".");
			if(fileIndex!=-1){
				String extn = fileName.substring(fileIndex + 1, fileName.length());
				extn = (extn != null) ? extn.toLowerCase() : "";
				return "mp3".equals(extn);
			}
		}
		return false;
	}

}