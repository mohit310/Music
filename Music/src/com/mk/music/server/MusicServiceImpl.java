package com.mk.music.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocCollector;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.mk.music.client.MusicBean;
import com.mk.music.client.MusicService;

public class MusicServiceImpl extends RemoteServiceServlet implements
		MusicService {

	private static final String PATH = ResourceUtil.getValue("music.root.dir");
	private static final String INDEX_DIR = ResourceUtil
			.getValue("music.index.dir");
	private static final File DEFAULT_ROOT = new File(PATH);
	private static final Map<String, MusicBean> DIR_MAP = new HashMap<String, MusicBean>();

	public MusicServiceImpl() {
		DIR_MAP.put(PATH, getAllFilesAndDir(PATH));
	}

	public static void refresh() {
		if (DIR_MAP != null) {
			DIR_MAP.clear();
		}
	}

	@Override
	public MusicBean[] getMusicList(String directory) {
		if (directory == null) {
			directory = PATH;
		}
		MusicBean musicBean = DIR_MAP.get(directory);
		if (musicBean == null) {
			musicBean = getAllFilesAndDir(directory);
			synchronized (musicBean) {
				DIR_MAP.put(directory, musicBean);
			}
		}
		List<MusicBean> children = musicBean.getChildren();
		MusicBean[] musicArr = new MusicBean[children.size()];
		children.toArray(musicArr);
		return musicArr;
	}

	@Override
	public MusicBean[] getParentMusicList(String directory) {
		if (directory == null
				|| DEFAULT_ROOT.getAbsolutePath().equalsIgnoreCase(
						(new File(directory)).getAbsolutePath())) {
			directory = PATH;
		} else {
			File f = new File(directory);
			directory = f.getParent();
		}
		MusicBean musicBean = DIR_MAP.get(directory);
		if (musicBean == null) {
			musicBean = getAllFilesAndDir(directory);
			synchronized (musicBean) {
				DIR_MAP.put(directory, musicBean);
			}
		}
		List<MusicBean> children = musicBean.getChildren();
		MusicBean[] musicArr = new MusicBean[children.size()];
		children.toArray(musicArr);
		return musicArr;
	}

	@Override
	public String getParentDirectory(String directory) {
		if (directory == null
				|| DEFAULT_ROOT.getAbsolutePath().equalsIgnoreCase(
						(new File(directory)).getAbsolutePath())) {
			directory = PATH;
		} else {
			File f = new File(directory);
			directory = f.getParent();
		}
		return directory;
	}

	private MusicBean getAllFilesAndDir(String directory) {
		MusicBean root = new MusicBean(directory, null, true);
		DirectoryFilter dirFilter = new DirectoryFilter();
		File f = new File(directory);
		if (f.isDirectory()) {
			File[] dirandfiles = f.listFiles();
			for (int i = 0; i < dirandfiles.length; ++i) {
				File dirorfile = dirandfiles[i];
				if ((dirorfile.isDirectory()) && (!(dirorfile.isHidden()))) {
					MusicBean bean = new MusicBean(dirorfile.getPath(), null,
							true);
					bean.setParentDirectory(dirorfile.getParent());
					bean.setName(dirorfile.getName());
					bean.setModifiedDate(new Date(dirorfile.lastModified()));
					root.addChild(bean);
				} else if (dirFilter.acceptExtension(dirorfile)) {
					MusicBean bean = new MusicBean(dirorfile.getParent(),
							dirorfile.getName(), false);
					bean.setName(dirorfile.getName());
					bean.setModifiedDate(new Date(dirorfile.lastModified()));
					root.addChild(bean);
				}
			}
		}
		List<MusicBean> children = root.getChildren();
		Collections.sort(children, new Comparator<MusicBean>() {
			@Override
			public int compare(MusicBean f1, MusicBean f2) {
				return f2.getModifiedDate().compareTo(f1.getModifiedDate());
			}
		});
		root.setChildren(children);
		return root;
	}

	private MusicBean getAllFilesAndDirWithStartLetter(String startLetter) {
		MusicBean root = DIR_MAP.get(startLetter.toUpperCase());
		if(root==null){
			root = new MusicBean(PATH, null, true);
			File f = new File(PATH);
			if (f.isDirectory()) {
				File[] dirandfiles = f.listFiles();
				for (int i = 0; i < dirandfiles.length; ++i) {
					File dirorfile = dirandfiles[i];
					if(startLetter.equalsIgnoreCase("0-9")){
						System.out.println("in 0-9");
						for(int j=0;j<9;j++){
							if (dirorfile.isDirectory() && dirorfile.getName().toLowerCase().startsWith(""+j,0) && (!(dirorfile.isHidden()))) {
								MusicBean bean = new MusicBean(dirorfile.getPath(), null, true);
								bean.setParentDirectory(dirorfile.getParent());
								bean.setName(dirorfile.getName());
								bean.setModifiedDate(new Date(dirorfile.lastModified()));
								root.addChild(bean);	
							}
						}
					}else{
						if (dirorfile.isDirectory() && dirorfile.getName().toLowerCase().startsWith(startLetter.toLowerCase(), 0) && (!(dirorfile.isHidden()))) {
							MusicBean bean = new MusicBean(dirorfile.getPath(), null, true);
							bean.setParentDirectory(dirorfile.getParent());
							bean.setName(dirorfile.getName());
							bean.setModifiedDate(new Date(dirorfile.lastModified()));
							root.addChild(bean);
						}
					}
				}
			}
			List<MusicBean> children = root.getChildren();
			Collections.sort(children, new Comparator<MusicBean>() {
				@Override
				public int compare(MusicBean f1, MusicBean f2) {
					return f2.getModifiedDate().compareTo(f1.getModifiedDate());
				}
			});
			root.setChildren(children);
			synchronized (root) {
				DIR_MAP.put(startLetter.toUpperCase(), root);
			}
		}
		return root;
	}

	@Override
	public MusicBean[] search(String searchQuery) {
		List<MusicBean> finalList = new ArrayList<MusicBean>();
		MusicBean[] searchList = new MusicBean[0];
		try {
			Set<String> directory = new HashSet<String>();
			IndexReader reader = IndexReader.open(new File(INDEX_DIR));
			Searcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new StandardAnalyzer();
			MultiFieldQueryParser parser = new MultiFieldQueryParser(
					new String[] { "name" }, analyzer);
			Query query = parser.parse(searchQuery + "*");
			TopDocCollector collector = new TopDocCollector(10);
			searcher.search(query, collector);
			ScoreDoc[] docs = collector.topDocs().scoreDocs;
			for (int i = 0; i < docs.length; i++) {
				Document doc = searcher.doc(docs[i].doc);
				directory.add(doc.get("path"));
			}
			for (Iterator<String> iterator = directory.iterator(); iterator
					.hasNext();) {
				String directoryPath = (String) iterator.next();
				MusicBean[] beans = getMusicList(directoryPath);
				finalList.addAll(Arrays.asList(beans));
			}
			searchList = new MusicBean[finalList.size()];
			finalList.toArray(searchList);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return searchList;
	}

	@Override
	public boolean isPlaylistAvailable() {
		HttpServletRequest perThreadReq = getThreadLocalRequest();
		HttpSession session = perThreadReq.getSession();
		if (session != null) {
			List<File> playlist = (List<File>) session
					.getAttribute("playlistfiles");
			if (playlist != null && playlist.size() > 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void addToPlaylist(String file) {
		HttpServletRequest perThreadReq = getThreadLocalRequest();
		HttpSession session = perThreadReq.getSession();
		if (session != null) {
			List<File> playlist = (List<File>) session
					.getAttribute("playlistfiles");
			if (playlist == null) {
				playlist = new ArrayList<File>();
				session.setAttribute("playlistfiles", playlist);
			}
			playlist.add(new File(file));
		}
	}

	@Override
	public void removeFromPlaylist(String file) {
		HttpServletRequest perThreadReq = getThreadLocalRequest();
		HttpSession session = perThreadReq.getSession();
		if (session != null) {
			List<File> playlist = (List<File>) session
					.getAttribute("playlistfiles");
			if (playlist != null) {
				playlist.remove(new File(file));
			}
		}
	}

	@Override
	public MusicBean[] listDirectories(String startLetter) {
		MusicBean musicBean = getAllFilesAndDirWithStartLetter(startLetter);
		List<MusicBean> children = musicBean.getChildren();
		MusicBean[] musicArr = new MusicBean[children.size()];
		children.toArray(musicArr);
		return musicArr;
	}

	@Override
	public String[] getImages() {
		String albumArtURL = getServletContext()
				.getRealPath("/images/albumart");
		File f = new File(albumArtURL);
		File[] imageFiles = f.listFiles();
		if (imageFiles != null) {
			List<String> imageList = new ArrayList<String>();
			for (int i = 0; i < imageFiles.length; i++) {
				File imageFile = imageFiles[i];
				if (imageFile.isFile() && !imageFile.isHidden()) {
					String imageURL = getServletContext().getContextPath()
							+ "/images/albumart/" + imageFiles[i].getName();
					imageList.add(imageURL);
				}
			}
			String[] images = new String[imageList.size()];
			imageList.toArray(images);
			return images;
		}
		return new String[0];
	}

}
