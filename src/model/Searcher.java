package model;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.uhighlight.UnifiedHighlighter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import javax.swing.text.Highlighter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    TopDocs topDocs = searcher.search(query, 50, Sort.RELEVANCE);

    UnifiedHighlighter highlighter = new UnifiedHighlighter(searcher, new StandardAnalyzer());
    String[] m = highlighter.highlight(Constants.ARTICLE_TITLE, query, topDocs);

    List<Result> results = new ArrayList<>();
    var i = 0;
    for (ScoreDoc sd : topDocs.scoreDocs) {
      Document doc = searcher.doc(sd.doc);

      results.add(new Result(
          doc.get(Constants.ARTICLE_TITLE),
          doc.get(Constants.ARTICLE_AUTHOR),
          doc.get(Constants.ARTICLE_DATE),
          doc.get(Constants.ARTICLE_FOCUS),
          doc.get(Constants.ARTICLE_CONTENTS)));

      results.get(i).createHighlight(m[i++]);
    }

    return results;
  }

}
