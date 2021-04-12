package model;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.document.TextField;
import java.io.*;
import java.util.stream.Collectors;

public class Indexer {

    private IndexWriter writer;

    public Indexer(String indexDirectoryPath) throws IOException {
        Directory memoryIndex = new MMapDirectory(new File("here").toPath());
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(memoryIndex, indexWriterConfig);

        createIndex(indexDirectoryPath);
    }

    public void createIndex(String dataDirPath) throws IOException {
        //get all files in the data directory
        File[] files = new File(dataDirPath).listFiles();

        for (File file : files) {
            if(!file.isDirectory()
                    && !file.isHidden()
                    && file.exists()
                    && file.canRead()
            ){
                indexFile(file);
            }
        }
    }

    private void indexFile(File file) throws IOException {

        Document document = getDocument(file);
        writer.addDocument(document);
    }

    private Document getDocument(File file) throws IOException {

        Document document = new Document();
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        TextField authorField = new TextField(LuceneConstants.ARTICLE_AUTHOR,
                br.readLine(), TextField.Store.YES);
        TextField dateField = new TextField(LuceneConstants.ARTICLE_DATE,
                br.readLine(), TextField.Store.YES);
        TextField focusField = new TextField(LuceneConstants.ARTICLE_FOCUS,
                br.readLine(), TextField.Store.YES);
        TextField titleField = new TextField(LuceneConstants.ARTICLE_TITLE,
                br.readLine(), TextField.Store.YES);
        TextField contentsField = new TextField(LuceneConstants.ARTICLE_CONTENTS,
                br.lines().collect(Collectors.joining()), TextField.Store.NO);

        TextField fileNameField = new TextField(LuceneConstants.FILE_NAME,
                file.getName(),TextField.Store.YES);
        TextField filePathField = new TextField(LuceneConstants.FILE_PATH,
                file.getCanonicalPath(),TextField.Store.YES);

        document.add(authorField);
        document.add(dateField);
        document.add(focusField);
        document.add(titleField);
        document.add(contentsField);
        document.add(fileNameField);
        document.add(filePathField);

        return document;
    }
}
