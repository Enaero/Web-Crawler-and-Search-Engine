import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//trial class for parsing not to be used same code as webinfoprovider
public class WebPageProcessor 
{
	URL currentUrl ;

	private int bufferSize = 4096;
	
	//String absHref;
	//String relHref;
	
	/*public static void main(String args[])
	{
		WebPageProcessor parse = new WebPageProcessor();
		parse.parseURL();
		
	}*/
	public void parseURL()
	{
		Document doc;
		try
		{
		doc = Jsoup.connect("http://www.ucr.edu/").get(); // read url from config file.
		String title = doc.title();
		
		currentUrl = new URL("http://www.ucr.edu/"); //TODO change this it should be referred using an object
		
		//get all links
		Elements links = doc.select("a[href]");
		
		for(Element presentlink : links)
		{
			//add more normalization ref: http://en.wikipedia.org/wiki/URL_normalization
			//skip empty links
			if(presentlink.attr("abs:href").toString().length() < 1)
			{
				continue;
			}
			//skip links that are just page anchors
			if(presentlink.attr("abs:href").toString().charAt(0)=='#')
			{
				continue;
			}
			//skip mailto links
			if(presentlink.attr("abs:href").toString().indexOf("mailto:")!=-1)
			{
				continue;
			}
			if(presentlink.attr("abs:href").toString().indexOf("javascript")!=-1)
			{
				continue;
			}
			 int index = presentlink.attr("abs:href").toString().indexOf('#');
	         if (index != -1) {
	        	 //String str = presentlink.attr("abs:href").toString().substring(0, index); 
	        	 presentlink.attr("abs:href",presentlink.attr("abs:href").toString().substring(0, index));
	        			 
	        			
	         }
			System.out.println("\nlink : " + presentlink.attr("abs:href"));
			//readFromURL(currentUrl);
			
			//System.out.println("\ntext : " + link.text());
		}
		
		
		}
		catch(Exception e)
		{
			
			e.printStackTrace();
		}
		
	}
	public void readFromURL(URL currentUrl)
	{
		String crc,filename;
		File currDir  = new File(".");
		String path = currDir.getAbsolutePath();
		path =path.substring(0, path.length()-1);
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(currentUrl.openStream()));
	          // Read page into buffer.
	          String line;
	          StringBuffer pageBuffer = new StringBuffer(bufferSize);
	          while ((line = reader.readLine()) != null) 
	          {
	            pageBuffer.append(line);
	          }
	          crc = Utility.getCRC(currentUrl.toString());
	          filename = path+crc;
	            writeToFile(filename, pageBuffer);
		}
		catch(Exception e)
		{
			System.out.println("Error ["+ this.toString() + ".downloadPage()] : " + e);
		}
	}
	 public static void writeToFile(String pFilename, StringBuffer pData) throws IOException 
	 {  
	       try
	       {
		 	BufferedWriter out = new BufferedWriter(new FileWriter(pFilename));  
	        out.write(pData.toString());  
	        out.flush();  
	        out.close();
	       }
	       catch(Exception e)
	       {
	    	   
	    	   e.printStackTrace();
	       }
	       
	    } 
}
