package model;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

public class QueryBuilder {

  public static Query createQuery(String query, String options) {

    Query finalQuery;
    if (!options.equals("all")) {
      Query query1 = new TermQuery(new Term(Constants.ARTICLE_CONTENTS, query));
      Query query2 = new TermQuery(new Term(Constants.ARTICLE_FOCUS, options));

      BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
      booleanQuery.add(query1, BooleanClause.Occur.MUST);
      booleanQuery.add(query2, BooleanClause.Occur.MUST);

      return booleanQuery.build();
    }

    finalQuery = new TermQuery(new Term(Constants.ARTICLE_CONTENTS, query));

    return finalQuery;
  }

}
