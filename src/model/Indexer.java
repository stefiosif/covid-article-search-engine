package model;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.TextField;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

public class Indexer {

  private IndexWriter writer;

  public Indexer(String indexDir, String dataDir) throws IOException {

    Directory dir = FSDirectory.open(new File(indexDir).toPath());
    StandardAnalyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig writerConfig = new IndexWriterConfig(analyzer);
    writer = new IndexWriter(dir, writerConfig);

    writer.deleteAll();
    index(dataDir);

    writer.close();
  }

  private void index(String dataDirPath) throws IOException {

    File[] files = new File(dataDirPath).listFiles();

    for (File file : files)
      writer.addDocument(getDocument(file));
  }

  private Document getDocument(File file) throws IOException {

    Document doc = new Document();
    BufferedReader br = new BufferedReader(new FileReader(file));

    // Fields for the article's info
    doc.add(new TextField(LuceneConstants.ARTICLE_AUTHOR,
        br.readLine(), Field.Store.YES));
    doc.add(new TextField(LuceneConstants.ARTICLE_DATE,
        br.readLine(), Field.Store.YES));
    doc.add(new TextField(LuceneConstants.ARTICLE_FOCUS,
        br.readLine(), Field.Store.YES));
    doc.add(new TextField(LuceneConstants.ARTICLE_TITLE,
        br.readLine(), Field.Store.YES));
    doc.add(new TextField(LuceneConstants.ARTICLE_CONTENTS,
        br.lines().collect(Collectors.joining()), Field.Store.YES));

    // Fields for the article's file info
    doc.add(new TextField(LuceneConstants.FILE_NAME,
        file.getName(), Field.Store.YES));
    doc.add(new TextField(LuceneConstants.FILE_PATH,
        file.getCanonicalPath(), Field.Store.YES));

    return doc;
  }

  public IndexWriter getWriter() {
    return writer;
  }
}
