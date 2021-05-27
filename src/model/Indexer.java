package model;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
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
  private SpellChecker spellChecker;

  public SpellChecker getSpellChecker() {
    return spellChecker;
  }

  public Indexer(String indexDir, String dataDir) {

    this.indexDir = indexDir;
    this.dataDir = dataDir;
    this.analyzer = new StandardAnalyzer();
  }

  public void index() throws IOException {

    Directory index = FSDirectory.open(new File(indexDir).toPath());
    IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
    IndexWriter iw = new IndexWriter(index, iwc);

    // Delete previous launch's temp index
    iw.deleteAll();

    File[] files = new File(dataDir).listFiles();
    assert files != null;
    for (File file : files)
      iw.addDocument(createDoc(file));

    // Create Spellchecker
    Directory spellCheckerDirectory
        = FSDirectory.open(new File(Constants.SPELLCHECKER_PATH).toPath());
    spellChecker = new SpellChecker(spellCheckerDirectory);
    IndexReader reader = DirectoryReader.open(index);
    Dictionary dictionary = new LuceneDictionary(reader, Constants.ARTICLE_CONTENTS);
    spellChecker.indexDictionary(dictionary, new IndexWriterConfig(new StandardAnalyzer()), true);

    iw.close();
    index.close();
  }

  private Document createDoc(File file) throws IOException {

    Document doc = new Document();
    BufferedReader br = new BufferedReader(new FileReader(file));

    // Fields for the article's info
    doc.add(new TextField(Constants.ARTICLE_AUTHOR,
        br.readLine(), Field.Store.YES));

    String date = br.readLine();
    long sortByDate = Long.parseLong(date.replace("-",""));
    doc.add(new SortedNumericDocValuesField(Constants.SORTBY_DATE, sortByDate));
    doc.add(new TextField(Constants.ARTICLE_DATE,
            date, Field.Store.YES));
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
