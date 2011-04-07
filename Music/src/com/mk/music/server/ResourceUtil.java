package com.mk.music.server;

import java.util.ResourceBundle;

public class ResourceUtil {
	
	private static final ResourceBundle bundle = ResourceBundle.getBundle("com/mk/music/client/MusicConstants");
	
	public static String getValue(String key){
		return bundle.getString(key);
	}

}
