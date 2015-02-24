package myfirstlucent.internal.test;

import java.io.IOException;

import myfirstlucent.internal.Indexer;
import myfirstlucent.internal.LuceneConstant;
import myfirstlucent.internal.Searcher;
import myfirstlucent.internal.TextFileFilter;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class LuceneTester
{
    String indexDir = "C:\\Users\\C09471.WHQ_NT_DOMAIN\\Lucene\\Index";
    String dataDir = "C:\\Users\\C09471.WHQ_NT_DOMAIN\\Lucene\\Data";
    Indexer indexer;
    Searcher searcher;

    public static void main(String[] args) {
       LuceneTester tester;
       try {
          tester = new LuceneTester();
          tester.createIndex();
          tester.search("Marry");
       } catch (IOException e) {
          e.printStackTrace();
       } catch (ParseException e) {
          e.printStackTrace();
       }
    }

    private void createIndex() throws IOException{
       indexer = new Indexer(indexDir);
       int numIndexed;
       long startTime = System.currentTimeMillis();  
       numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
       long endTime = System.currentTimeMillis();
       indexer.close();
       System.out.println(numIndexed+" File indexed, time taken: "
          +(endTime-startTime)+" ms");       
    }

    private void search(String searchQuery) throws IOException, ParseException{
       searcher = new Searcher(indexDir);
       long startTime = System.currentTimeMillis();
       TopDocs hits = searcher.search(searchQuery);
       long endTime = System.currentTimeMillis();
    
       System.out.println(hits.totalHits +
          " documents found. Time :" + (endTime - startTime));
       for(ScoreDoc scoreDoc : hits.scoreDocs) {
          Document doc = searcher.getDocument(scoreDoc);
             System.out.println("File: "
             + doc.get(LuceneConstant.FILE_PATH));
       }
       searcher.close();
    }
}