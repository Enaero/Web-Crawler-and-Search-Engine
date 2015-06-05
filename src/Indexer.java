import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.tartarus.snowball.*;
import org.tartarus.snowball.ext.PorterStemmer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Indexer {
	static private final Version version = Version.LUCENE_46;
	static private final Analyzer analyzer = new StandardAnalyzer(version);
	static private final IndexWriterConfig config = new IndexWriterConfig(version, analyzer);
	
	
	String path;
	File index_file;
	Directory index = null;
	HashSet<String> stop_words;
	
	private boolean isStopWord(String word)
	{
		return stop_words.contains(word);
	}
	
	private String cleanWord(String word) throws Exception
	{
		word = word.toLowerCase();
		word = word.replaceAll("[\\p{P}]","");
		word = word.replaceAll("[~`\\-\\*/\\+]", " ");
		
		PorterStemmer p = new PorterStemmer();
		p.setCurrent(word);
		p.stem();
		word = p.getCurrent();
		
		return word;
	}
	
	public Indexer(String doc_path, String index_path, String stop_words_path) throws Exception {
		path = doc_path;
		index_file = new File(index_path);
		if (!index_file.exists())
			index_file.mkdir();
		index = new MMapDirectory(index_file);
		
		stop_words = new HashSet<String>();
		BufferedReader reader = new BufferedReader(new FileReader(stop_words_path));
		for (String line = reader.readLine(); line != null; line = reader.readLine())
		{
			stop_words.add(line);
		}
	}
	
	
	private String getSingleContext (ArrayList<String> doc, String word) throws Exception
	{
		word = cleanWord(word);
		int i = -1;
		
		for (int j = 0; j < doc.size(); ++j)
		{
			if (cleanWord(doc.get(j)).equals(word))
			{
				i = j;
				break;
			}
		}
		
		if (i == -1)
			return "";
		
		String context = "";
		for (int j = 3; j > 0; --j)
		{
			if (i - j >= 0)
				context = context + " " + doc.get(i-j);
		}
		for (int j = 0; j <= 3 && i+j < doc.size(); ++j)
		{
			context = context + " " + doc.get(i+j);
		}		
		
		return context;
	}
	
	private ArrayList<String> getContext(ArrayList<String> doc, String query) throws Exception
	{
		String[] keywords = query.split("\\s");
		ArrayList<String> context = new ArrayList<String>();
		HashSet<String> usedKeyWords = new HashSet<String>();
		
		for (int i = 0; i < keywords.length; ++i)
		{
			if (usedKeyWords.contains(keywords[i]))
				continue;
			
			String c = getSingleContext(doc, keywords[i]);
			String[] words = c.split("\\s");
			for (String word : words)
			{
				usedKeyWords.add(word);
			}
			
			context.add(c);
		}
		
		return context;
	}
	
	public void index() throws Exception{
		// Set up variables for loading pages
		File dir = new File(path);
		if (!dir.isDirectory())
			throw new Exception("invalid path given to Indexer class");
		File[] files = dir.listFiles();
		BufferedReader reader = null;
		
		// Set up variables for indexing
		IndexWriter writer = new IndexWriter(index, config);
		
		// Index each file
		for (File file : files)
		{
			if (file.isDirectory())
				continue;
			reader = new BufferedReader(new FileReader(path+"/"+file.getName()));	
			
			Document doc = new Document();
			
			//Format of documents has url first then title second, then the data.
			String url = reader.readLine();
			String title = reader.readLine();
			
			TextField tf = new TextField("url", url, Field.Store.YES);
			tf.setBoost(2.0f);
			doc.add(tf);
			tf = new TextField("title", title, Field.Store.YES);
			tf.setBoost(1.5f);
			doc.add(tf);
			doc.add(new StringField("filename", file.getName(), Field.Store.YES));
			
			for (String line = reader.readLine(); line != null; line = reader.readLine())
			{	
				String[] words = line.split("\\s");
				for (int i = 0; i < words.length; ++i)
				{
					String word = words[i];
					System.out.println(word);
					
					word = cleanWord(word);
					if (word.length() > 0)
					{
						if(!isStopWord(word))
						{
							doc.add(new TextField("keyword", word, Field.Store.YES));
							System.out.println(word);
						}
						doc.add(new StringField("text", words[i], Field.Store.YES));
					}
				}
				
			}
			writer.addDocument(doc);	
			reader.close();
		}
		writer.close();
	}
	
	public ArrayList<Result> search(String query, int hitsPerPage) throws Exception {
		Query q = new QueryParser(version, "keyword", analyzer).parse(query);
		
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		
		ArrayList<Result> results = new ArrayList<Result>();
		
	    for(int i=0;i<hits.length;++i) {
	      int docId = hits[i].doc;
	      Document d = searcher.doc(docId);
	      
	      ArrayList<String> text = new ArrayList<String>();
	      String[] raw_text = d.getValues("text");
	      for (int j = 0; j < raw_text.length; ++j)
	    	  text.add(raw_text[j]);
	      
	      ArrayList<String> context = getContext(text, query);
	      String title = d.get("title");
	      String url = d.get("url");
	      
	      Result r = new Result(url, context, title, d.get("filename"));
	      results.add(r);
	    }		
		return results;
	}
	
}
