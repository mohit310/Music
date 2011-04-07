package com.mk.music.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MusicServiceAsync {
	void getMusicList(String directory, AsyncCallback<MusicBean[]> callback);
	void getParentMusicList(String directory, AsyncCallback<MusicBean[]> callback);
	void getParentDirectory(String directory, AsyncCallback<String> callback);
	void isPlaylistAvailable(AsyncCallback<Boolean> callback);
	void addToPlaylist(String file,AsyncCallback<Void> callback);
	void removeFromPlaylist(String file,AsyncCallback<Void> callback);
	void search(String searchString, AsyncCallback<MusicBean[]> callback);
	void listDirectories(String startLetter, AsyncCallback<MusicBean[]> callback);
	void getImages(AsyncCallback<String[]> callback);
}
