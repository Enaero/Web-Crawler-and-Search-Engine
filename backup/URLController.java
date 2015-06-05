import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.jsoup.Jsoup;


public class URLController 
{
	URL currentUrl ;
	private int bufferSize = 4096;
	private final String folder_name = "pages";
	
	String filename;
	WebPageInfoProvider webPage = new WebPageInfoProvider();
	BufferedWriter Associationout;
	public URLController() throws IOException
	{
		try{
		Associationout = new BufferedWriter(new FileWriter("Association.txt"));}
		catch(Exception e){}
		
	}
	public URL getCurrentUrl()
	  {
	     return currentUrl;
	  }
	
	public WebPageInfoProvider downloadPage()  //TODO also to save the page as file
	{
		StringBuffer pageBuffer;
		String crc;
		File currDir  = new File(".");
		
		String path = currDir.getAbsolutePath();
		//Ferris was here
		//path =path.substring(0, path.length()-1);
		path = path.substring(0, path.length()-1) + folder_name + "/";
		File folder_path = new File(path);
		if (!folder_path.exists())
		{
			folder_path.mkdir();
		}
		
		try
		{
			  if(currentUrl!=null)
			  {
				  pageBuffer = readFromURL(currentUrl);
				  crc = Utility.getCRC(currentUrl.toString());
				  filename = path+crc;
				  System.out.println(filename);
				  writeToFile(pageBuffer);
				  Associationout.flush();
				  //Associationout.close();
				  webPage.setPageContents(pageBuffer, currentUrl);
				  return webPage;
			  }
	            
		}
		catch(Exception e)
		{
			System.out.println("Error ["+ this.toString() + ".downloadPage()] : " + e);
		}
		return null;
		
	}
	public StringBuffer readFromURL(URL currentUrl)
	{
		
		StringBuffer page=new StringBuffer(bufferSize);;
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(currentUrl.openStream()));
	          // Read page into buffer.
	          String line;
	          //page = new StringBuffer(bufferSize);
	          while ((line = reader.readLine()) != null) 
	          {
	            page.append(line);
	          }
	          //String textOnly = Jsoup.parse(page.toString()).text();
	          
	          
		}
		catch(Exception e)
		{
			System.out.println("Error ["+ this.toString() + ".downloadPage()] : " + e);
		}
		return page;
	}
	 public void writeToFile(StringBuffer pData) throws IOException 
	 {  
	       try
	       {
		 	BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		 	String textOnly = Jsoup.parse(pData.toString()).text();
		 	
		 	//Ferris was here
		 	String title = Jsoup.parse(pData.toString()).title();
		 	out.write(currentUrl.toString());
		 	out.newLine();
		 	out.write(title);
		 	out.newLine();
		 	
	        out.write(textOnly);  
	        out.flush();  
	        out.close();
	       // StoreToAssociationFile();
	        //Ferris was here
	        Associationout.write(currentUrl.toString()+"\t"+filename);
	        Associationout.newLine(); //platform independent newline, it was breaking on mine
	       }
	       
	       catch(Exception e)
	       {
	    	   
	    	   e.printStackTrace();
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

