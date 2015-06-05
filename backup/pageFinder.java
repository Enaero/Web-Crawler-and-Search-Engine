

import java.io.*;
import java.net.URL;
import java.util.*;
public class pageFinder
{
	String seedUrl;
	// list of to be crawled links
	HashSet alreadyCrawledList = new HashSet();
	LinkedHashSet listToCrawl = new LinkedHashSet();
	
	// Cache of robot disallow lists.
	  private HashMap disallowListCache = new HashMap();
	boolean crawling;

	WebPageInfoProvider pagecontents;
	URLController currenturl;
	 //constructor

	public pageFinder() throws Exception
	{
		boolean crawling = false;
		pagecontents = new WebPageInfoProvider();
		currenturl = new URLController();
		
		
	}
	
	// Check if robot is allowed to access the given URL.
	   private boolean isRobotAllowed(URL urlToCheck) 
	   {
		   if (urlToCheck == null)
			   return false;
	       String host = urlToCheck.getHost().toLowerCase();
	       // Retrieve host's disallow list from cache.
	       ArrayList disallowList = (ArrayList) disallowListCache.get(host);
	       
	       // If list is not in the cache, download and cache it.
	       if (disallowList == null) 
	        {
	         disallowList = new ArrayList();
	         try {
	            URL robotsFileUrl =
	            new URL("http://" + host + "/robots.txt");
	            
	            // Open connection to robot file URL for reading.
	            BufferedReader reader =
	            new BufferedReader(new InputStreamReader(
	            robotsFileUrl.openStream()));
	            
	            // Read robot file, creating list of disallowed paths.
	            String line;
	            while ((line = reader.readLine()) != null) 
	            {
	              if (line.toLowerCase().indexOf("Disallow:") == 0) {
	                String disallowPath =  line.toLowerCase().substring("Disallow:".length());
	                // Check disallow path for comments and remove if present.
	                int commentIndex = disallowPath.indexOf("#");
	                if (commentIndex != - 1) {
	                  disallowPath =
	                  disallowPath.substring(0, commentIndex);
	                }
	                // Remove leading or trailing spaces from disallow path.
	                disallowPath = disallowPath.trim();
	                // Add disallow path to list.
	                disallowList.add(disallowPath);
	               }
	            }
	            // Add new disallow list to cache.
	            disallowListCache.put(host, disallowList);
	     
	           } catch (Exception e) 
	           {
	             /* Assume robot is allowed since an exception
	             is thrown if the robot file doesn't exist. */
	             return true;
	           }
	         }
	        /* Loop through disallow list to see if
	        crawling is allowed for the given URL. */
	        String file = urlToCheck.getFile();
	        for (int i = 0; i < disallowList.size(); i++) 
	        {
	           String disallow = (String) disallowList.get(i);
	           if (file.startsWith(disallow)) {
	             return false;
	           }
	        }
	         return true;
	   }
	
	//actual crawling function
	private void crawl()
	{
		String url;
		seedUrl = "http://www.ucr.edu/"; // add this url to config file
		listToCrawl.add(seedUrl);
		crawling = true;
		ArrayList links = new ArrayList();
		while(crawling == true && listToCrawl.size()>0)
		{
			url=(String)listToCrawl.iterator().next();
			 URL verifiedUrl = null;
			listToCrawl.remove(url);
			if (currenturl.isValidURL(url))
	         {  
	           verifiedUrl = currenturl.getCurrentUrl();
	         }
	          else
	         {  
	            continue;
	         } 
			// Skip URL if robots are not allowed to access it.
            if (!isRobotAllowed(verifiedUrl)) {
              continue;
            }
			
			// download page using webpage and urlcon class
			pagecontents = currenturl.downloadPage(); 
			
			alreadyCrawledList.add(url);
			
			//TODO store page in database or decide some storing strategy
			if(pagecontents!=null && pagecontents.getLength()>0)
			{
				// Retrieve valid links from the page using WebPageInfo class.
				//store it in list of Array
				if(verifiedUrl!=null)
				  links = pagecontents.retrieveLinks(alreadyCrawledList,verifiedUrl.toString());
	             
	                // Add links to the To Crawl list.
	                listToCrawl.addAll(links); 
			}
			
		}
	
	}
	

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		try
		{	
			Indexer indexer = new Indexer("./pages/", "./index/", "./stop_words.txt");
			indexer.index();
			
			ArrayList<Result> results = indexer.search("Computer Science", 10);
			for (Result r : results)
			{
				
				System.out.println(r.title);
				
				for (String c : r.contexts)
				{
					System.out.println(c);
				}
				System.out.println(r.filename);
				System.out.println(r.url);
			}
			//pageFinder mycrawler = new pageFinder();
			//mycrawler.crawl();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}

	}
}