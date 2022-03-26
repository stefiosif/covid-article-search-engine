package com.model;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;

public class QueryBuilder {

  public static Query createQuery(String query, String options) throws ParseException {

    Query finalQuery;
    QueryParser qp = new QueryParser(Constants.ARTICLE_CONTENTS, new StandardAnalyzer());

    if (query.contains("?") || query.contains("*"))
      finalQuery = new WildcardQuery(new Term(Constants.ARTICLE_CONTENTS, query));
    else
      finalQuery = qp.parse(query);

    if (!options.equals("all")) {
      Query focusQuery = new TermQuery(new Term(Constants.ARTICLE_FOCUS, options));

      BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
      booleanQuery.add(finalQuery, BooleanClause.Occur.MUST);
      booleanQuery.add(focusQuery, BooleanClause.Occur.MUST);

      return booleanQuery.build();
    }

    return finalQuery;
  }

}
