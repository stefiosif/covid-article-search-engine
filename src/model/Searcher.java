package model;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
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

public class Searcher {

  private IndexSearcher indexSearcher;
  private QueryParser queryParser;
  private Query query;

  public Searcher(String indexDirectoryPath) throws IOException {

    indexSearcher = new IndexSearcher(indexSearcher.getIndexReader());
    queryParser = new QueryParser(LuceneConstants.ARTICLE_CONTENTS,
        new StandardAnalyzer());

  }

  public TopDocs search(String searchQuery)
      throws IOException, ParseException {
    query = queryParser.parse(searchQuery);
    return indexSearcher.search(query, 10);
  }

  public Document getDocument(ScoreDoc scoreDoc)
      throws CorruptIndexException, IOException {
    return indexSearcher.doc(scoreDoc.doc);
  }

}
