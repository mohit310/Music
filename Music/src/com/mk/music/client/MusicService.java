package com.mk.music.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("musicService")
public interface MusicService extends RemoteService{
	public MusicBean[] getMusicList(String directory);
	public MusicBean[] getParentMusicList(String directory);
	public String getParentDirectory(String directory);
	public boolean isPlaylistAvailable();
	public void addToPlaylist(String file);
	public void removeFromPlaylist(String file);
	public MusicBean[] search(String searchQuery);
	public MusicBean[] listDirectories(String startLetter);
	public String[] getImages();
}
