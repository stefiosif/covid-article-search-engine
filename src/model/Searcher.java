package model;

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

      String m = findA(doc.get(Constants.ARTICLE_CONTENTS), searchQuery);
      results.get(i).createHighlight(m);
      i++;
    }

    return results;
  }

  public static String findA(String st, String pointer) {
    String lines[] = st.split(" ");
    int position=0;
    String  a = "", b = "", c = "";
    // vriskei tin thesi tis leksis
    for(int i=0; i<lines.length; i++)
    {
      if(lines[i].equals(pointer))
      {
        position = i;
        a += lines[i];
        break;
      }
    }
    // typonei to proto meros kai elegxei an to megethos ine < 10 gia na kanei analogo print
    for(int i=0; i<lines.length; i++)
    {
      if(position==i && i >= 10)
      {
        for(int j=i-10; j<i; j++)
        {
          b += lines[j] + " ";
        }
        break;
      }
      else if (position==i && i< 10)
      {
        for(int j=0; j<i; j++)
        {
          b += lines[j] + " ";
        }
        break;
      }
    }
    // typonei to deutero meros kai elegxei an to telos tou einai <10, an einai typonei to analogo print
      for(int i=0; i<lines.length; i++)
    {
      if(position == i)
      {
        for(int j=i+1; j<i+11; j++)
        {
          if(lines[j].equals(lines[lines.length - 1]))
          {
            c += lines[j] + " ";
            break;
          }
          else
          {
            c += lines[j] + " ";
          }
        }
        break;
      }
    }
    return b + " --> " + a + " <-- " + c;
  }

}
