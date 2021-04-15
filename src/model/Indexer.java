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
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Indexer {

  private IndexWriter indexWriter;

  public Indexer(String indexDirectoryPath) throws IOException {

    Directory indexDirectory =
        FSDirectory.open(new File(indexDirectoryPath).toPath());
    StandardAnalyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
    indexWriter = new IndexWriter(indexDirectory, indexWriterConfig);

    createIndex(LuceneConstants.ARTICLES_PATH);

    indexWriter.close();
  }

  private void createIndex(String dataDirPath) throws IOException {

    File[] files = new File(dataDirPath).listFiles();

    for (File file : files)
      indexWriter.addDocument(createDocument(file));
  }

  private Document createDocument(File file) throws IOException {

    Document document = new Document();
    BufferedReader br = new BufferedReader(new FileReader(file));
    ArrayList<TextField> fields = new ArrayList<>();

    fields.add(new TextField(LuceneConstants.ARTICLE_AUTHOR,
        br.readLine(), Field.Store.YES));
    fields.add(new TextField(LuceneConstants.ARTICLE_DATE,
        br.readLine(), Field.Store.YES));
    fields.add(new TextField(LuceneConstants.ARTICLE_FOCUS,
        br.readLine(), Field.Store.YES));
    fields.add(new TextField(LuceneConstants.ARTICLE_TITLE,
        br.readLine(), Field.Store.YES));
    fields.add(new TextField(LuceneConstants.ARTICLE_CONTENTS,
        br.lines().collect(Collectors.joining()), Field.Store.NO));

    fields.add(new TextField(LuceneConstants.FILE_NAME,
        file.getName(), Field.Store.YES));
    fields.add(new TextField(LuceneConstants.FILE_PATH,
        file.getCanonicalPath(), Field.Store.YES));

    for (TextField field : fields)
      document.add(field);

    return document;
  }

  public IndexWriter getIndexWriter() {
    return indexWriter;
  }
}
