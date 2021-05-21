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
import java.util.List;
import java.util.Scanner;

public class Searcher {

  private IndexSearcher searcher;
  private Directory index;

  public Searcher(String indexDir) throws IOException {

    index = FSDirectory.open(new File(indexDir).toPath());
    IndexReader reader = DirectoryReader.open(index);
    searcher = new IndexSearcher(reader);
  }

  public List<Result> search(String searchQuery) throws IOException, ParseException {

    QueryParser parser =
        new QueryParser(Constants.ARTICLE_CONTENTS, new StandardAnalyzer());
    Query query = parser.parse(searchQuery);
    TopDocs hits = searcher.search(query, 10);

    List<Result> results = new ArrayList<>();
    for (ScoreDoc sd : hits.scoreDocs) {
      Document doc = searcher.doc(sd.doc);

      results.add(new Result(String.format(doc.get(Constants.ARTICLE_TITLE)), doc.get(Constants.ARTICLE_FOCUS)));
    }
    
    return results;
  }

//  private String makeDescription(String content, String query) {
//    int position = content.indexOf(query);
//    String subString = content.substring(content.lastIndexOf(query));
//
//    Scanner sc = new Scanner(subString);
//    int wordCount = 0;
//    String description = "";
//    while (wordCount < 20 && sc.hasNext()){
//      String word = sc.next();
//      description = description.concat(" " + word);
//      wordCount++;
//    }
//
//    return description;
//  }
}
