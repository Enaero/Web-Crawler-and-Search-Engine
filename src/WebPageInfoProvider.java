import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class WebPageInfoProvider 
{
	URL pageUrl ;
	String pageContents ;
	SynchManager synchMngr;
	   
	   public WebPageInfoProvider(){
	     
	     synchMngr = new SynchManager();
	   }

	public void setPageContents (StringBuffer content, URL contentUrl)
	   {
	     pageContents = new String(content.toString());
	     pageUrl = contentUrl; 
	   }
	public int getLength()
	{
		return pageContents.length();
	}
	
	public ArrayList retrieveLinks(HashSet crawledList, String nextURL,boolean limithost)
	{
		ArrayList linkList = new ArrayList();
		String strLink;
		
		Document doc;
		try
		{
		doc = Jsoup.connect(nextURL).get(); // read url from config file.
		//TODO Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
		String title = doc.title();
		
		//currentUrl = new URL("http://www.ucr.edu/"); //
		
		//get all links
		Elements links = doc.body().select("a[href]");
		
		for(Element presentlink : links)
		{
			strLink = presentlink.attr("abs:href").toString();
			//add more normalization ref: http://en.wikipedia.org/wiki/URL_normalization
			//skip empty links
			if (strLink.contains(".pdf") || strLink.contains("@") 
					|| strLink.contains("adfad") || strLink.contains(":80")
					|| strLink.contains("fdafd") || strLink.contains(".jpg")) // remove for images
				continue;
			if(strLink.length() < 1)
			{
				continue;
			}
			//skip links that are just page anchors
			if(strLink.charAt(0)=='#')
			{
				continue;
			}
			//skip mailto links
			if(strLink.indexOf("mailto:")!=-1)
			{
				continue;
			}
			if(strLink.indexOf("javascript")!=-1)
			{
				continue;
			}
			 int index = strLink.indexOf('#');
	         if (index != -1) {
	        	 //String str = presentlink.attr("abs:href").toString().substring(0, index); 
	        	 //presentlink.attr("abs:href",presentlink.attr("abs:href").toString().substring(0, index));
	        	 //strLink = presentlink.attr("abs:href").toString().substring(0, index);
	        		strLink = strLink.substring(0,index);	 
	         }
	         if(strLink.endsWith("/"))
	        	 strLink = strLink.substring(0, strLink.length() - 1);
	         
			 
	         
	         URL verifiedLink = Utility.verifyUrl(strLink);
	         if (verifiedLink == null) {
	           continue;
	         }
	         /* TODO limit links to those
	         having the same host as the start URL*/
	         
	        /* if (limithost && !strLink.toLowerCase().equals(verifiedLink.getHost().toLowerCase()))
	         {
	           continue;
	         }*/
			if(synchMngr.crawledList_contain(crawledList, strLink))
					{
						continue;
					}
	      //   System.out.println("\nlink : " + strLink);
			//readFromURL(currentUrl);
			
			//System.out.println("\ntext : " + link.text());
			linkList.add(strLink);
			
		}
		
		
		}
		catch(Exception e)
		{
			
			e.printStackTrace();
			return null;
		}
		return linkList;
		
	}
}
