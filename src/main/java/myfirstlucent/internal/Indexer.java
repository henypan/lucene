package myfirstlucent.internal;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * <p>
 * This class is used to index the raw data so that we can make it searchable using lucene library.
 * </p>
 * <p>
 * Copyright &copy 2015 Pan
 * </p>
 * 
 * @author Henry Pan
 */
public class Indexer
{
    private IndexWriter writer;

    public Indexer(String indexDirectoryPath) throws IOException{
       //this directory will contain the indexes
       Directory indexDirectory = 
          FSDirectory.open(new File(indexDirectoryPath));

       //create the indexer
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, new StandardAnalyzer(
                Version.LUCENE_40));
        writer = new IndexWriter(indexDirectory, config);
    }

    public void close() throws CorruptIndexException, IOException{
       writer.close();
    }

    private Document getDocument(File file) throws IOException{
       Document document = new Document();

       //index file contents
       Field contentField = new Field(LuceneConstant.CONTENTS, 
          new FileReader(file));
       //index file name
       Field fileNameField = new Field(LuceneConstant.FILE_NAME,
          file.getName(),
          Field.Store.YES,Field.Index.NOT_ANALYZED);
       //index file path
       Field filePathField = new Field(LuceneConstant.FILE_PATH,
          file.getCanonicalPath(),
          Field.Store.YES,Field.Index.NOT_ANALYZED);

       document.add(contentField);
       document.add(fileNameField);
       document.add(filePathField);

       return document;
    }   

    private void indexFile(File file) throws IOException{
       System.out.println("Indexing "+file.getCanonicalPath());
       Document document = getDocument(file);
       writer.addDocument(document);
    }

    public int createIndex(String dataDirPath, FileFilter filter) 
       throws IOException{
       //get all files in the data directory
       File[] files = new File(dataDirPath).listFiles();

       for (File file : files) {
          if(!file.isDirectory()
             && !file.isHidden()
             && file.exists()
             && file.canRead()
             && filter.accept(file)
          ){
             indexFile(file);
          }
       }
       return writer.numDocs();
    }
}
