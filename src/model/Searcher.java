package model;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Searcher {

  private IndexSearcher searcher;

  public Searcher(String indexDir) throws IOException {

    Directory dir = FSDirectory.open(new File(indexDir).toPath());
    IndexReader reader = DirectoryReader.open(dir);
    searcher = new IndexSearcher(reader);
  }

  public ArrayList<ArrayList<String>> search(String searchQuery) throws IOException, ParseException {

    QueryParser parser =
        new QueryParser(LuceneConstants.ARTICLE_CONTENTS, new StandardAnalyzer());
    Query query = parser.parse(searchQuery);
    TopDocs hits = searcher.search(query, 20);

    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> descriptions = new ArrayList<>();
    for (ScoreDoc sd : hits.scoreDocs) {
      Document doc = searcher.doc(sd.doc);

      titles.add(String.format(doc.get(LuceneConstants.ARTICLE_TITLE)));
      descriptions.add(makeDescription(doc.get(LuceneConstants.ARTICLE_CONTENTS), searchQuery));
    }

    ArrayList<ArrayList<String>> data = new ArrayList<>();
    data.add(titles);
    data.add(descriptions);
    
    return data;
  }

  private String makeDescription(String content, String query) {
    int position = content.indexOf(query);
    String subString = content.substring(content.lastIndexOf(query));

    Scanner sc = new Scanner(subString);
    int wordCount = 0;
    String description = "";
    while (wordCount < 20 && sc.hasNext()){
      String word = sc.next();
      description = description.concat(" " + word);
      wordCount++;
    }

    return description;
  }
}
