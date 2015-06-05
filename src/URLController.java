import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class URLController 
{
	URL currentUrl ;
	String startUrl;
	private int bufferSize = 4096;
	StringBuffer page;
	String contentType = null;
	//SynchManager synchMngr;
	 int httpResponseCode = 0;
	  Mylogger log;
	  String protocol=null;
	WebPageInfoProvider webPage = new WebPageInfoProvider();
	BufferedWriter Associationout;
	public URLController()
	{
		/*try{
	//	synchMngr = new SynchManager();
		Associationout = new BufferedWriter(new FileWriter("Association.txt"));}
		catch(Exception e){}*/
		log =Mylogger.getInstance();
		page = new StringBuffer(bufferSize);
		
	}
	public void retrieveContentInfo() throws Exception
	  {
	    try
	    {
			log.logger.info("URLController: Retrieve Contents");
		    HttpURLConnection httpConn = (HttpURLConnection)  currentUrl.openConnection(); 
		    httpConn.setDefaultUseCaches(false);
		    httpResponseCode = httpConn.getResponseCode();
	            contentType = httpConn.getContentType(); 
		    httpConn.disconnect();
		    protocol = currentUrl.getProtocol();
	    }
	    catch(Exception e)
	    {
	    	System.out.println("Error in Http Connection: "+e.getMessage());
	    }
	    
	    }
	
	    public URL getCurrentUrl()
	  {
	     return currentUrl;
	  }
	public String getProtocol()
	  {
	     return protocol;
	  }
	 public String getContentType()
 	 {
      		return contentType; 
 	 }
	// read starting URL from configFile
	  public String readStartUrl(String configFile, String cid) throws Exception
	  {
	      readUrlFromConfigFile(configFile, cid);
	      return startUrl; 
	  }
	  
	   // Read Configuration file crawler.conf 
	  private void readUrlFromConfigFile(String fileName, String cid) throws Exception
	  {
		  File currDir  = new File(".");
		  String path = currDir.getAbsolutePath();
			path =path.substring(0, path.length()-1);
			fileName = path+fileName;
		  Properties configFile = new Properties();
	     configFile.load(new FileInputStream(fileName));
	     
	     startUrl = configFile.getProperty(cid).trim();
	  }
	
	public WebPageInfoProvider downloadPage()  
	{
		StringBuffer pageBuffer;
		String crc;
		String filename;
		File currDir  = new File(".");
		String path = currDir.getAbsolutePath();
		path =path.substring(0, path.length()-1);
		File f = new File(path+"CrawledPages");
		  // if the directory does not exist, create it
		  if (!f.exists()) {
		    //System.out.println("creating directory: " + directoryName);
		    boolean result = f.mkdir();  
		    if(result) {    
		   //    System.out.println("DIR created");  
		     }
		  }
	
		try
		{
			  if(currentUrl!=null)
			  {
				  //  synchMngr.incCounter(counter);
			        //  System.out.println("Counter: "+ synchMngr.getCounter(counter) +" "+tid+ "\n");
				    
				  // System.out.println("[" + synchMngr.getCounter(counter) + "] "+ currentUrl.toString() + " [" + httpResponseCode +"]\n\n");

				  pageBuffer = readFromURL(currentUrl);
				  crc = Utility.getCRC(currentUrl.toString());
				  filename = f.getAbsolutePath()+"/"+crc+".out";
				  writeToFile(pageBuffer,filename);
				 // Associationout.flush();
				  //Associationout.close();
				  webPage.setPageContents(pageBuffer, currentUrl);
				  return webPage;
			  }
	            
		}
		catch(Exception e)
		{
			log.logger.info("URLController: Error while downloading "+e.getMessage());
			//System.out.println("Error ["+ this.toString() + ".downloadPage()] : " + e);
		}
		return null;
		
	}
	public StringBuffer readFromURL(URL currentUrl)
	{
		
		page.setLength(0);
		try
		{
		//	BufferedReader reader = new BufferedReader(new InputStreamReader(currentUrl.openStream()));
	          // Read page into buffer.
	          String line;
	          //page = new StringBuffer(bufferSize);
	          //while ((line = reader.readLine()) != null) 
	         // {
	           // page.append(line);
	         // }
	         // //String textOnly = Jsoup.parse(page.toString()).text();
	          Document doc = Jsoup.connect(currentUrl.toString()).get();
 	          Elements para = doc.select("p");
		  for(Element p : para)
			page.append(p.text());
	          
		}
		catch(Exception e)
		{
			log.logger.info("URLController: Error while readingFromURL "+e.getMessage());
			//	System.out.println("Error ["+ this.toString() + ".downloadPage()] : " + e);
		}
		return page;
	}
	 public void writeToFile(StringBuffer pData,String filename) throws IOException 
	 {  
	       try
	       {
		 	BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		 	
		 	// This data replaces association.txt
		 	out.write(currentUrl.toString());
		 	out.newLine();
		 	out.write(Jsoup.parse(pData.toString()).title());
		 	out.newLine();
		 	
		 	String textOnly = Jsoup.parse(pData.toString()).text();
	        out.write(textOnly);  
	        out.flush();  
	        out.close();
	       // StoreToAssociationFile();
	    //    Associationout.write(currentUrl.toString()+"\t"+filename+"\n");
	       }
	       
	       catch(Exception e)
	       {
	    	   
	    	  log.logger.info("URLController: Error while writing "+e.getMessage());
			// e.printStackTrace();
	       }
	       
	    }
	 
	 public boolean isValidURL(String url)
	  {
	    try{
	          currentUrl = Utility.verifyUrl(url);
	       }catch(Exception e)
	       {
	          return false; 
	       }
	     return true;
	  }
	
}

