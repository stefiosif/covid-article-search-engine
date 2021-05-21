package model;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

public class Indexer {

  private final String indexDir, dataDir;
  private final Analyzer analyzer;

  public Indexer(String indexDir, String dataDir) throws IOException {

    this.indexDir = indexDir;
    this.dataDir = dataDir;
    this.analyzer = new StandardAnalyzer();
  }

  public void index() throws IOException {

    Directory index = FSDirectory.open(new File(indexDir).toPath());
    IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
    IndexWriter iw = new IndexWriter(index, iwc);

    // Retrieve files from the given local path
    File[] files = new File(dataDir).listFiles();

    for (File file : files)
      iw.addDocument(createDoc(file));

    iw.close();
    index.close();
  }

  private Document createDoc(File file) throws IOException {

    Document doc = new Document();
    BufferedReader br = new BufferedReader(new FileReader(file));

    // Fields for the article's info
    doc.add(new TextField(Constants.ARTICLE_AUTHOR,
        br.readLine(), Field.Store.YES));
    doc.add(new TextField(Constants.ARTICLE_DATE,
        br.readLine(), Field.Store.YES));
    doc.add(new TextField(Constants.ARTICLE_FOCUS,
        br.readLine(), Field.Store.YES));
    doc.add(new TextField(Constants.ARTICLE_TITLE,
        br.readLine(), Field.Store.YES));
    doc.add(new TextField(Constants.ARTICLE_CONTENTS,
        br.lines().collect(Collectors.joining()), Field.Store.YES));

    // Fields for the article's file info
    doc.add(new TextField(Constants.FILE_NAME,
        file.getName(), Field.Store.YES));
    doc.add(new TextField(Constants.FILE_PATH,
        file.getCanonicalPath(), Field.Store.YES));

    return doc;
  }
}
