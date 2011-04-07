package com.mk.music.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

public class IndexerServlet extends HttpServlet {

	private static final String INDEX_DIR = ResourceUtil.getValue("music.index.dir");
	private static final String PATH = ResourceUtil.getValue("music.root.dir");

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		File indexDir = new File(INDEX_DIR);
		if (!indexDir.exists()) {
			indexDir.mkdir();
		}else{
			indexDir.delete();
		}
		IndexWriter writer = new IndexWriter(indexDir, new StandardAnalyzer(), true, IndexWriter.MaxFieldLength.LIMITED);
		indexDocs(writer, new File(PATH));
		System.out.println("Optimizing...");
		writer.optimize();
		writer.close();
		System.out.println("Done...");
		response.setContentType("text/html");
		PrintWriter oWriter = response.getWriter();
		oWriter.write("<html><body><h3>Done indexing....</h3></body></html>");
	}

	static void indexDocs(IndexWriter writer, File file) throws IOException {
		// do not try to index files that cannot be read
		if (file.canRead()) {
			if (file.isDirectory()) {
				System.out.println("adding " + file);
				writer.addDocument(getDocument(file));
				String[] files = file.list();
				// an IO error could occur
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, new File(file, files[i]));
					}
				}
			} else {
				System.out.println("adding " + file);
				try {
					writer.addDocument(getDocument(file));
				}
				// at least on windows, some temporary files raise this exception with an "access denied" message
				// checking if the file can be read doesn't help
				catch (FileNotFoundException fnfe) {
					;
				}
			}
		}
	}

	public static Document getDocument(File f) throws java.io.FileNotFoundException {

		// make a new, empty document
		Document doc = new Document();

		
		// Add the path of the file as a field named "path". Use a field that is
		// indexed (i.e. searchable), but don't tokenize the field into words.
		if(f.isDirectory()){
			doc.add(new Field("path",f.getAbsolutePath(),Field.Store.YES,Field.Index.NOT_ANALYZED));
		}else{
			doc.add(new Field("path", f.getParent(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		}
		// Add the last modified date of the file a field named "modified". Use
		// a field that is indexed (i.e. searchable), but don't tokenize the field
		// into words.
		doc.add(new Field("modified", DateTools.timeToString(f.lastModified(), DateTools.Resolution.MINUTE), Field.Store.YES,
				Field.Index.NOT_ANALYZED));

		// Add the name of the file as a field named "name". Use a field that is
		// indexed (i.e. searchable), and tokenize the field into words.
		doc.add(new Field("name", f.getName(), Field.Store.YES, Field.Index.ANALYZED));

		// return the document
		return doc;
	}

}
