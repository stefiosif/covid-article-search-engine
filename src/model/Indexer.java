package model;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
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

    createIndex("articles");

    indexWriter.close();
  }

  private void createIndex(String dataDirPath) throws IOException {

    File[] files = new File(dataDirPath).listFiles();

    for (File file : files)
      indexWriter.addDocument(getDocument(file));
  }

  private Document getDocument(File file) throws IOException {

    Document document = new Document();
    BufferedReader br = new BufferedReader(new FileReader(file));
    ArrayList<TextField> textFields = new ArrayList<>();

    textFields.add(new TextField(LuceneConstants.ARTICLE_AUTHOR,
        br.readLine(), TextField.Store.YES));
    textFields.add(new TextField(LuceneConstants.ARTICLE_DATE,
        br.readLine(), TextField.Store.YES));
    textFields.add(new TextField(LuceneConstants.ARTICLE_FOCUS,
        br.readLine(), TextField.Store.YES));
    textFields.add(new TextField(LuceneConstants.ARTICLE_TITLE,
        br.readLine(), TextField.Store.YES));
    textFields.add(new TextField(LuceneConstants.ARTICLE_CONTENTS,
        br.lines().collect(Collectors.joining()), TextField.Store.NO));

    textFields.add(new TextField(LuceneConstants.FILE_NAME,
        file.getName(), TextField.Store.YES));
    textFields.add(new TextField(LuceneConstants.FILE_PATH,
        file.getCanonicalPath(), TextField.Store.YES));

    for (TextField field : textFields)
      document.add(field);

    return document;
  }

}
