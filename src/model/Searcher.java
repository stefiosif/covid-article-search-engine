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

public class Searcher {

  private IndexSearcher searcher;
  private Directory index;

  public Searcher(String indexDir) throws IOException {

    index = FSDirectory.open(new File(indexDir).toPath());
    searcher = new IndexSearcher(DirectoryReader.open(index));
  }

  public List<Result> search(String searchQuery) throws IOException, ParseException {

    QueryParser parser =
        new QueryParser(Constants.ARTICLE_CONTENTS, new StandardAnalyzer());
    Query query = parser.parse(searchQuery);
    TopDocs hits = searcher.search(query, 10);

    List<Result> results = new ArrayList<>();
    for (ScoreDoc sd : hits.scoreDocs) {
      Document doc = searcher.doc(sd.doc);

      results.add(new Result(
          doc.get(Constants.ARTICLE_TITLE),
          doc.get(Constants.ARTICLE_AUTHOR),
          doc.get(Constants.ARTICLE_DATE),
          doc.get(Constants.ARTICLE_FOCUS),
          doc.get(Constants.ARTICLE_CONTENTS)));
    }
    
    return results;
  }

}
