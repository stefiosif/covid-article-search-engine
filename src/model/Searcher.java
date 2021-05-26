package model;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
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

  public List<Result> search(String searchQuery, NextQuery nextQuery) throws IOException, ParseException, InvalidTokenOffsetsException {

    QueryParser parser;

    switch(nextQuery.getFocus()){
      case "business":
        parser = new QueryParser(Constants.ARTICLE_CONTENTS, new StandardAnalyzer());
      case "general":
        parser = new QueryParser(Constants.ARTICLE_CONTENTS, new StandardAnalyzer());
      case "tech":
        parser = new QueryParser(Constants.ARTICLE_CONTENTS, new StandardAnalyzer());
      case "consumer":
        parser = new QueryParser(Constants.ARTICLE_FOCUS, new StandardAnalyzer());
      case "science":
        parser = new QueryParser(Constants.ARTICLE_CONTENTS, new StandardAnalyzer());
      case "finance":
        parser = new QueryParser(Constants.ARTICLE_CONTENTS, new StandardAnalyzer());
      default:
        parser = new QueryParser(Constants.ARTICLE_CONTENTS, new StandardAnalyzer());

    }
    Query query = parser.parse(searchQuery);

    TopDocs topDocs;
    if (nextQuery.getSortBy().equals("relevance"))
      topDocs = searcher.search(query, 48, Sort.RELEVANCE);
    else {
      topDocs = searcher.search(query, 48, new Sort(new SortField(Constants.ARTICLE_DATE, SortField.Type.STRING)));
    }

    SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
    Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));


    List<Result> results = new ArrayList<>();
    var i = 0;
    for (ScoreDoc sd : topDocs.scoreDocs) {
      int id = sd.doc;
      Document doc = searcher.doc(id);

      TokenStream tokenStream = TokenSources.getAnyTokenStream(
          searcher.getIndexReader(), id, Constants.ARTICLE_CONTENTS, new StandardAnalyzer());
      String text = doc.get(Constants.ARTICLE_CONTENTS);
      TextFragment[] frag = highlighter.getBestTextFragments(
          tokenStream, text, false, 5);
      String out = "";
      for (int j = 0; j < frag.length; j++) {
        if ((frag[j] != null) && (frag[j].getScore() > 0)) {
          out += frag[j].toString();
        }
      }
      results.add(new Result(
          doc.get(Constants.ARTICLE_TITLE),
          doc.get(Constants.ARTICLE_AUTHOR),
          doc.get(Constants.ARTICLE_DATE),
          doc.get(Constants.ARTICLE_FOCUS),
          doc.get(Constants.ARTICLE_CONTENTS)));

      results.get(i).createHighlight(out);
      i++;
    }

    return results;
  }

}
