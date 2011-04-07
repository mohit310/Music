package com.mk.music.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MusicBean implements Serializable {

	private String directory;
	private String fileName;
	private Date modifiedDate;
	private boolean isDirectory;
	private String parentDirectory;
	private String name;
	private List<MusicBean> children = new ArrayList<MusicBean>();

	public MusicBean(){
		
	}
	
	public MusicBean(String directory, String fileName, boolean isDirectory) {
		this.directory = directory;
		this.fileName = fileName;
		this.isDirectory = isDirectory;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

	public String getParentDirectory() {
		return parentDirectory;
	}

	public void setParentDirectory(String parentDirectory) {
		this.parentDirectory = parentDirectory;
	}

	public List<MusicBean> getChildren() {
		return children;
	}

	public void setChildren(List<MusicBean> children) {
		this.children = children;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addChild(MusicBean mbean) {
		children.add(mbean);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((directory == null) ? 0 : directory.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MusicBean other = (MusicBean) obj;
		if (directory == null) {
			if (other.directory != null)
				return false;
		} else if (!directory.equals(other.directory))
			return false;
		if (!isDirectory) {
			if (fileName == null) {
				if (other.fileName != null)
					return false;
			} else if (!fileName.equals(other.fileName))
				return false;
		}
		return true;
	}

}
