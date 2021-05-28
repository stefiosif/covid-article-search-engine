package model;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Searcher {

  private final IndexSearcher searcher;

  public Searcher(String indexDir) throws IOException {

    Directory index = FSDirectory.open(new File(indexDir).toPath());
    searcher = new IndexSearcher(DirectoryReader.open(index));
  }

  public List<Result> search(String searchQuery, AdvancedSearch options) throws IOException, ParseException, InvalidTokenOffsetsException {

    Query query = QueryBuilder.createQuery(searchQuery, options.getFocus());

    TopDocs topDocs;
    if (options.getSortBy().equals("relevance"))
      topDocs = searcher.search(query, 48, Sort.RELEVANCE);
    else
      topDocs = searcher.search(query, 48, new Sort(new SortedNumericSortField(Constants.SORTBY_DATE, SortField.Type.LONG, true)));

    SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
    Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));

    List<Result> results = new ArrayList<>();
    for (ScoreDoc sd : topDocs.scoreDocs) {

      Document doc = searcher.doc(sd.doc);
      String highlight = createHighlight(highlighter, doc.get(Constants.ARTICLE_CONTENTS));

      results.add(new Result(
          doc.get(Constants.ARTICLE_TITLE),
          doc.get(Constants.ARTICLE_AUTHOR),
          doc.get(Constants.ARTICLE_DATE),
          doc.get(Constants.ARTICLE_FOCUS),
          doc.get(Constants.ARTICLE_CONTENTS),
          highlight));
    }

    return results;
  }

  private String createHighlight(Highlighter highlighter, String text)
      throws IOException, InvalidTokenOffsetsException {

    String[] highlight = highlighter.getBestFragments(
        new StandardAnalyzer(), Constants.ARTICLE_CONTENTS, text, 1);

    // Create output string
    StringBuilder out = new StringBuilder();
    for (String s : highlight) {
      if ((s != null))
        out.append(s);
    }

    return out.toString();
  }

}
