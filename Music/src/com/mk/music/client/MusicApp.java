package com.mk.music.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MusicApp implements EntryPoint {

	private VerticalPanel mainPanel = new VerticalPanel();
	//private HorizontalPanel directoryPanel = new HorizontalPanel();
	private VerticalPanel newAlbumVPanel = new VerticalPanel();
	private HorizontalPanel atozPanel = new HorizontalPanel();
	private HorizontalPanel newAlbumPanel = new HorizontalPanel();
	private FlexTable directoryflexTable;
	private FlexTable musicflexTable;
	private MusicServiceAsync musicSvc;
	private MusicBean[] musicArr;
	//private PushButton navigation;
	//private Label directoryLabel;
	private Image headerImage;
	private PushButton playlistButton;
	//private PushButton radioButton;
	private PushButton searchButton;
	private TextBox searchBox;
	private MusicConstants constants;
	Image waitImage;
	PopupPanel waitPanel;
	String[] aToz = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","0-9"};

	/**
	 * Entry point method.
	 */
	public void onModuleLoad() {
		ScheduledCommand componentsScheduledCommand = new ScheduledCommand() {
			@Override
			public void execute() {
				setupComponents();
			}
		};
		ScheduledCommand dataScheduledCommand = new ScheduledCommand() {
			@Override
			public void execute() {
				showWait();
				//getListing(constants.topDirectory());
				displayDirectories("A");
			}
		};
		Scheduler.get().scheduleDeferred(componentsScheduledCommand);
		Scheduler.get().scheduleDeferred(dataScheduledCommand);
	}

	private void setupComponents() {
		constants = (MusicConstants) GWT.create(MusicConstants.class);
		if (musicSvc == null) {
			musicSvc = GWT.create(MusicService.class);
		}
		mainPanel.setStyleName("mainpanel");
		addHeader();
		addBreak();
		addNavigation();
		addBreak();
		addSearch();
		addBreak();
		addNewAlbums();
		addBreak();
		addAtoZ();
		addBreak();
		addPlaylistButton();
		addBreak();
		createAndAddTables();
		directoryflexTable.setVisible(false);
		musicflexTable.setVisible(false);
		RootPanel.get("content_area").add(mainPanel);
	}
	
	private void addNewAlbums() {
		mainPanel.add(newAlbumVPanel);
		newAlbumVPanel.add(newAlbumPanel);
		addImagesFromServer();
	}

	private void addAtoZ() {
		for (int i = 0; i < aToz.length; i++) {
			Button b = new Button(aToz[i]);
			b.setSize("40px", "30px");
			b.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Button b = (Button)event.getSource();
					String startLetter = b.getText();
					directoryflexTable.setVisible(false);
					musicflexTable.setVisible(false);
					showWait();
					displayDirectories(startLetter);
					
				}
			});
			atozPanel.add(b);
		}
		mainPanel.add(atozPanel);
		
	}
	
	private void displayDirectories(final String startLetter){
		// Set up the callback object.
		AsyncCallback<MusicBean[]> callback = new AsyncCallback<MusicBean[]>() {
			public void onFailure(Throwable caught) {
				Window.alert("Error communicating with server.");
			}

			public void onSuccess(MusicBean[] result) {
				if(result==null || result.length == 0){
					directoryflexTable.setVisible(false);
					musicflexTable.setVisible(false);
					hideWait();
					Window.alert("No music found with letter " + startLetter);
				}else{
					musicArr = result;
					addLinks();
				}
			}
		};
		musicSvc.listDirectories(startLetter, callback);
	}
	
	private void addImagesFromServer(){
		// Set up the callback object.
		AsyncCallback<String[]> callback = new AsyncCallback<String[]>() {
			public void onFailure(Throwable caught) {
				Window.alert("Error communicating with server.");
			}

			public void onSuccess(String[] result) {
				if(result!=null && result.length != 0){
					for(int i=0;i<result.length;i++){
						String imageURL = result[i];
						String name = imageURL.substring(imageURL.lastIndexOf("/")+1, imageURL.lastIndexOf("."));
						Image image = new Image(URL.encode(imageURL));
						image.setTitle(name);
						image.setSize("140px", "150px");
						image.addMouseOverHandler(new MouseOverHandler() {
							@Override
							public void onMouseOver(MouseOverEvent event) {
								Image image = (Image) event.getSource();
								image.setStyleName("handcursor");
							}
						});
						image.addMouseOutHandler(new MouseOutHandler() {
							@Override
							public void onMouseOut(MouseOutEvent event) {
								Image image = (Image) event.getSource();
								image.setStyleName("defaultcursor");
							}
						});
						image.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								Image image = (Image)event.getSource();
								String directory = image.getTitle();
								getListing(constants.topDirectory() + "/" + directory);
							}
						});
						newAlbumPanel.add(image);
					}
				}
			}
		};
		musicSvc.getImages(callback);
	}

	private void addSearch() {
		HorizontalPanel hpanel = new HorizontalPanel();
		searchBox = new TextBox();
		searchBox.setStyleName("textbox");
		searchBox.setMaxLength(50);
		searchBox.setWidth("250px");
		searchBox.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER){
					String searchQuery = searchBox.getText();
					if(searchQuery==null || searchQuery.trim().length()==0){
						Window.alert("Please enter search string");
					}else{
						directoryflexTable.setVisible(false);
						musicflexTable.setVisible(false);
						showWait();
						search(searchQuery);
					}
				}
			}
		});		
		hpanel.add(searchBox);
		searchButton = new PushButton("Search");
		//searchButton.setSize("40px", "16px");
		searchButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				String searchQuery = searchBox.getText();
				if(searchQuery==null || searchQuery.trim().length()==0){
					Window.alert("Please enter search string");
				}else{
					directoryflexTable.setVisible(false);
					musicflexTable.setVisible(false);
					showWait();
					search(searchQuery);
				}
			}
		});
		hpanel.add(searchButton);
		mainPanel.add(hpanel);
	}

	private void addPlaylistButton() {
		HorizontalPanel hPanel = new HorizontalPanel();
		playlistButton = new PushButton("Create PlayList");
		//playlistButton.setSize("140px", "25px");
		playlistButton.addStyleDependentName("margin");
		playlistButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				PushButton playListButton = (PushButton) event.getSource();
				playListButton.setEnabled(false);
				getPlaylist();
				removeSelections();
				playListButton.setEnabled(true);
			}
		});
//		radioButton = new PushButton("LIVE RADIO");
//		radioButton.setSize("120px", "25px");
//		radioButton.addStyleDependentName("margin");
//		radioButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				Window.open("http://www.soneemo.com/radio", "_self","");
//			}
//		});
		hPanel.add(playlistButton);
		hPanel.setCellHorizontalAlignment(playlistButton, HorizontalPanel.ALIGN_LEFT);
//		hPanel.add(radioButton);
//		hPanel.setCellHorizontalAlignment(radioButton, HorizontalPanel.ALIGN_LEFT);
		mainPanel.add(hPanel);
	}
	
	private void removeSelections(){
		for(int i=1;i<directoryflexTable.getRowCount();i++){
			CheckBox c = (CheckBox)directoryflexTable.getWidget(i, 0);
			c.setValue(Boolean.FALSE);
		}
		for(int i=1;i<musicflexTable.getRowCount();i++){
			CheckBox c = (CheckBox)musicflexTable.getWidget(i, 0);
			c.setValue(Boolean.FALSE);
		}
	}

	private void addBreak() {
		HTML html = new HTML("<br>");
		mainPanel.add(html);
	}

	private void addHeader() {
		mainPanel.add(getHeaderImage());
	}

	private void createAndAddTables() {
		createDirTable();
		createMusicTable();
	}

	private void addNavigation() {
//		mainPanel.add(directoryPanel);
//		navigation = new PushButton(getHomeImage(), getHomeImage());
//		navigation.setStyleName("navbutton");
//		directoryLabel = new Label(constants.topDirectory());
//		directoryPanel.add(navigation);
//		directoryPanel.add(directoryLabel);
//		navigation.addMouseOverHandler(new MouseOverHandler() {
//			@Override
//			public void onMouseOver(MouseOverEvent event) {
//				PushButton navigation = (PushButton) event.getSource();
//				navigation.setStyleName("handcursor");
//			}
//		});
//		navigation.addMouseOutHandler(new MouseOutHandler() {
//			@Override
//			public void onMouseOut(MouseOutEvent event) {
//				PushButton navigation = (PushButton) event.getSource();
//				navigation.setStyleName("defaultcursor");
//			}
//		});
//		navigation.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				PushButton a = (PushButton) event.getSource();
//				showWait();
//				a.setEnabled(false);
//				hideTables();
//				getParentListing(directoryLabel.getText());
//				a.setEnabled(true);
//			}
//		});
//		
	}

	private void hideTables() {
		directoryflexTable.setVisible(false);
		musicflexTable.setVisible(false);
	}

	private void createDirTable() {
		directoryflexTable = new FlexTable();
		directoryflexTable.setStyleName("musictable");
		directoryflexTable.setText(0, 0, "Select");
		directoryflexTable.setText(0, 1, "Directory");
		//directoryflexTable.setText(0, 2, "Modified Date");
		directoryflexTable.setText(0, 2, "Stream");
		directoryflexTable.setText(0, 3, "DirPath");
		directoryflexTable.getCellFormatter().setVisible(0, 3, false);
		directoryflexTable.getRowFormatter().setStyleName(0, "tableheader");
		mainPanel.add(directoryflexTable);

	}

	private void createMusicTable() {
		musicflexTable = new FlexTable();
		musicflexTable.setStyleName("musictable");
		musicflexTable.setTitle("Music Files");
		musicflexTable.setText(0, 0, "Select");
		musicflexTable.setText(0, 1, "File");
		//musicflexTable.setText(0, 2, "Modified Date");
		musicflexTable.setText(0, 2, "Stream");
		musicflexTable.setText(0, 3, "FilePath");
		musicflexTable.getCellFormatter().setVisible(0, 3, false);
		musicflexTable.getRowFormatter().setStyleName(0, "tableheader");
		mainPanel.add(musicflexTable);
	}

	private void getListing(String directory) {
		// Set up the callback object.
		AsyncCallback<MusicBean[]> callback = new AsyncCallback<MusicBean[]>() {
			public void onFailure(Throwable caught) {
				Window.alert("Error communicating with server.");
			}

			public void onSuccess(MusicBean[] result) {
				musicArr = result;
				addLinks();
			}
		};
		musicSvc.getMusicList(directory, callback);
	}
	
	private void search(String searchString) {
		// Set up the callback object.
		AsyncCallback<MusicBean[]> callback = new AsyncCallback<MusicBean[]>() {
			public void onFailure(Throwable caught) {
				Window.alert("Error communicating with server.");
			}

			public void onSuccess(MusicBean[] result) {
				musicArr = result;
				addLinks();
			}
		};
		musicSvc.search(searchString, callback);
	}

	private void getPlaylist() {
		// Set up the callback object.
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			public void onFailure(Throwable caught) {
				Window.alert("Error communicating with server.");
			}

			public void onSuccess(Boolean isAvailable) {
				if (isAvailable.booleanValue()) {
					getSessionPlayList();
				} else {
					Window.alert("Please select songs");
				}
			}
		};
		musicSvc.isPlaylistAvailable(callback);
	}

	private void getSessionPlayList() {
		String baseURL = GWT.getModuleBaseURL();
		String url = baseURL + "playlistService?isSession=true";
		Window.open(url, "", "menubar=no,location=no,resizable=no,scrollbars=no,status=no,toolbar=false,width=500px,height=75px");
	}

	private void getPlayList(String directory, String fileName) {
		String baseURL = GWT.getModuleBaseURL();
		String url = baseURL + "playlistService?directory=" + directory;
		if (fileName != null && fileName.trim().length() > 0) {
			url += "&file=" + fileName;
		}
		Window.open(url, "", "menubar=no,location=no,resizable=no,scrollbars=no,status=no,toolbar=false,width=500px,height=75px");
	}
	
	private void addToPlaylist(String file){
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {
			public void onFailure(Throwable caught) {
				Window.alert("Error communicating with server.");
			}

			public void onSuccess(Void v) {
			}
		};
		musicSvc.addToPlaylist(file,callback);
	}
	
	private void removeFromPlaylist(String file){
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {
			public void onFailure(Throwable caught) {
				Window.alert("Error communicating with server.");
			}

			public void onSuccess(Void v) {
			}
		};
		musicSvc.removeFromPlaylist(file,callback);
	}

//	private void getParentListing(String directory) {
//		// Set up the callback object.
//		AsyncCallback<MusicBean[]> callback = new AsyncCallback<MusicBean[]>() {
//			public void onFailure(Throwable caught) {
//				Window.alert("Error communicating with server.");
//			}
//
//			public void onSuccess(MusicBean[] result) {
//				musicArr = result;
//				addLinks();
//			}
//		};
//		// Set up the callback object.
//		AsyncCallback<String> parentcallback = new AsyncCallback<String>() {
//			public void onFailure(Throwable caught) {
//				Window.alert("Error communicating with server.");
//			}
//
//			public void onSuccess(String result) {
//				String serverFolder = result.replaceAll("\\\\", "\\/");
//				if (serverFolder.equalsIgnoreCase(constants.topDirectory())) {
//					//navigation.getDownFace().setImage(getHomeImage());
//					//navigation.getUpFace().setImage(getHomeImage());
//				} else {
//					//navigation.getDownFace().setImage(getUpImage());
//					//navigation.getUpFace().setImage(getUpImage());
//				}
//				//directoryLabel.setText(serverFolder);
//			}
//		};
//		musicSvc.getParentDirectory(directory, parentcallback);
//		musicSvc.getParentMusicList(directory, callback);
//	}

	private void addLinks() {
		setDir();
		setMusic();
		hideWait();
	}

	private void setDir() {
		while(directoryflexTable.getRowCount() > 1){
			directoryflexTable.removeRow(directoryflexTable.getRowCount()-1);
		}
		int row = directoryflexTable.getRowCount();
		for (int i = 0; i < musicArr.length; i++) {
			MusicBean bean = musicArr[i];
			if (bean.isDirectory()) {
				CheckBox cBox = new CheckBox();
				cBox.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						Cell cellForEvent = directoryflexTable.getCellForEvent(event);
						int row = cellForEvent.getRowIndex();
						CheckBox selectBox = (CheckBox) event.getSource();
						if(selectBox.getValue().booleanValue()){
							addToPlaylist(directoryflexTable.getText(row, 3));
						}else{
							removeFromPlaylist(directoryflexTable.getText(row, 3));
						}
					}
				});
				directoryflexTable.setWidget(row, 0, cBox);
				Anchor dirAnchor = new Anchor(bean.getName());
				dirAnchor.addMouseOverHandler(new MouseOverHandler() {
					@Override
					public void onMouseOver(MouseOverEvent event) {
						Anchor dirAnchor = (Anchor) event.getSource();
						dirAnchor.setStyleName("handcursor");
					}
				});
				dirAnchor.addMouseOutHandler(new MouseOutHandler() {
					@Override
					public void onMouseOut(MouseOutEvent event) {
						Anchor dirAnchor = (Anchor) event.getSource();
						dirAnchor.setStyleName("defaultcursor");
					}
				});

				dirAnchor.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						Cell cellForEvent = directoryflexTable.getCellForEvent(event);
						showWait();
						hideTables();
						int row = cellForEvent.getRowIndex();
						String dir = directoryflexTable.getText(row, 3);
						//directoryLabel.setText(dir);
						//navigation.getDownFace().setImage(getUpImage());
						//navigation.getUpFace().setImage(getUpImage());
						getListing(dir);
					}
				});
				directoryflexTable.setWidget(row, 1, dirAnchor);
				//directoryflexTable.setText(row, 2, bean.getModifiedDate().toString());
				PushButton dirButton = new PushButton(getStreamDirImage(), getStreamDirImage());
				dirButton.setStyleName("streambutton");
				dirButton.addMouseOverHandler(new MouseOverHandler() {
					@Override
					public void onMouseOver(MouseOverEvent event) {
						PushButton dirButton = (PushButton) event.getSource();
						dirButton.setStyleName("handcursor");
					}
				});
				dirButton.addMouseOutHandler(new MouseOutHandler() {
					@Override
					public void onMouseOut(MouseOutEvent event) {
						PushButton dirButton = (PushButton) event.getSource();
						dirButton.setStyleName("defaultcursor");
					}
				});
				dirButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						Cell cellForEvent = directoryflexTable.getCellForEvent(event);
						PushButton a = (PushButton) event.getSource();
						a.setEnabled(false);
						int row = cellForEvent.getRowIndex();
						getPlayList(directoryflexTable.getText(row, 3), null);
						a.setEnabled(true);
					}
				});
				directoryflexTable.setWidget(row, 2, dirButton);
				directoryflexTable.setText(row, 3, bean.getDirectory());
				directoryflexTable.getCellFormatter().setVisible(row, 3, false);
				directoryflexTable.getRowFormatter().setStyleName(row, "tablecell");

			}
			row = directoryflexTable.getRowCount();
		}
		if (directoryflexTable.getRowCount() == 1) {
			directoryflexTable.setVisible(false);
		} else {
			directoryflexTable.setVisible(true);
		}
	}

	private void setMusic() {
		while(musicflexTable.getRowCount() > 1){
			musicflexTable.removeRow(musicflexTable.getRowCount()-1);
		}
		int row = musicflexTable.getRowCount();
		for (int i = 0; i < musicArr.length; i++) {
			MusicBean bean = musicArr[i];
			if (!bean.isDirectory()) {
				CheckBox cBox = new CheckBox();
				cBox.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						Cell cellForEvent = musicflexTable.getCellForEvent(event);
						int row = cellForEvent.getRowIndex();
						CheckBox selectBox = (CheckBox) event.getSource();
						if(selectBox.getValue().booleanValue()){
							addToPlaylist(musicflexTable.getText(row, 3) + "/" + musicflexTable.getText(row, 1));
						}else{
							removeFromPlaylist(musicflexTable.getText(row, 3) + "/" + musicflexTable.getText(row, 1));
						}
					}
				});
				musicflexTable.setWidget(row, 0, cBox);
				musicflexTable.setText(row, 1, bean.getName());
				//musicflexTable.setText(row, 2, bean.getModifiedDate().toString());
				PushButton fileButton = new PushButton(getStreamFileImage(), getStreamFileImage());
				fileButton.setStyleName("streambutton");
				fileButton.addMouseOverHandler(new MouseOverHandler() {
					@Override
					public void onMouseOver(MouseOverEvent event) {
						PushButton fileButton = (PushButton) event.getSource();
						fileButton.setStyleName("handcursor");
					}
				});
				fileButton.addMouseOutHandler(new MouseOutHandler() {
					@Override
					public void onMouseOut(MouseOutEvent event) {
						PushButton fileButton = (PushButton) event.getSource();
						fileButton.setStyleName("defaultcursor");
					}
				});
				fileButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						Cell cellForEvent = musicflexTable.getCellForEvent(event);
						PushButton a = (PushButton) event.getSource();
						a.setEnabled(false);
						int row = cellForEvent.getRowIndex();
						getPlayList(musicflexTable.getText(row, 3), musicflexTable.getText(row, 1));
						a.setEnabled(true);
					}
				});
				musicflexTable.setWidget(row, 2, fileButton);
				musicflexTable.setText(row, 3, bean.getDirectory());
				musicflexTable.getCellFormatter().setVisible(row, 3, false);
				musicflexTable.getRowFormatter().setStyleName(row, "tablecell");
			}
			row = musicflexTable.getRowCount();
		}
		if (musicflexTable.getRowCount() == 1) {
			musicflexTable.setVisible(false);
		} else {
			musicflexTable.setVisible(true);
		}

	}

	private Image getHomeImage() {
		Image homeImage = new Image("images/house_orange.png");
		homeImage.setStyleName("home");
		return homeImage;
	}

	private Image getStreamDirImage() {
		Image dirStreamImage = new Image("images/directory.png");
		dirStreamImage.setStyleName("streamimage");
		return dirStreamImage;

	}

	private Image getStreamFileImage() {
		Image fileStreamImage = new Image("images/sound.png");
		fileStreamImage.setStyleName("streamimage");
		return fileStreamImage;
	}

	private Image getUpImage() {
		Image upArrowImage = new Image("images/up_arrow_orange.png");
		upArrowImage.setStyleName("uparrow");
		return upArrowImage;
	}

	private Image getWaitImage() {
		if (waitImage == null) {
			waitImage = new Image("images/wait.gif");
			waitImage.setStyleName("waitimage");
		}
		return waitImage;
	}
	
	private Image getHeaderImage() {
		if (headerImage == null) {
			headerImage = new Image("images/mkmusicstreamer.gif");
			headerImage.setStyleName("headerimage");
		}
		return headerImage;
	}

	private void showWait() {
		ScheduledCommand waitScheduledCommand = new ScheduledCommand() {
			@Override
			public void execute() {
				waitPanel = new PopupPanel();
				waitPanel.setStylePrimaryName("noborder");
				HorizontalPanel horizPanel = new HorizontalPanel();
				horizPanel.add(getWaitImage());
				horizPanel.add(new Label("Loading..."));
				waitPanel.setSize("100px", "100px");
				waitPanel.setPopupPosition((Window.getClientWidth()/2)-100, (Window.getClientHeight()/2));
				waitPanel.add(horizPanel);
				playlistButton.setVisible(false);
				//radioButton.setVisible(false);
				waitPanel.show();
			}
		};
		Scheduler.get().scheduleDeferred(waitScheduledCommand);
	}

	private void hideWait() {
		ScheduledCommand waitScheduledCommand = new ScheduledCommand() {
			@Override
			public void execute() {
				waitPanel.hide();
				playlistButton.setVisible(true);
				//radioButton.setVisible(true);
			}
		};
		Scheduler.get().scheduleDeferred(waitScheduledCommand);
	}
}
