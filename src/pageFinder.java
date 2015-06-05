

import java.io.*;
import java.net.URL;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
public class pageFinder extends Thread //implements Runnable
{
	String seedUrl;
	  private String startUrl;
	  private int maxUrls = -1;
	  private boolean limitHost = false;
	  private boolean crawling=false;  
	  private String crawlerId;
      private  int counter;
      static BufferedWriter Associationout;
	  
	  
	  
	  // list of to be crawled links
	 // public static Integer[] counter= new Integer[1];
	  public static HashSet alreadyCrawledList = new HashSet();
	  public static LinkedHashSet listToCrawl = new LinkedHashSet();
	
	// Cache of robot disallow lists.
	  private HashMap disallowListCache = new HashMap();
	

	WebPageInfoProvider pagecontents;
	URLController currenturl;
	SynchManager synchMngr;
        Mylogger logWrapper;
	Thread tcrawler;
	File currDir;
	String path;
	//hold configuration file name    
    String configFile = null;
  
	 //constructor
	public void init()
	{
		boolean crawling = false;
		pagecontents = new WebPageInfoProvider();
		currenturl = new URLController();
		counter =0;
		configFile = "crawler.conf";
		
	//	counter[0] = 0;
        crawlerId = "http://www.cs.cur.edu";
	synchMngr = new SynchManager();
//	        log = new Mylogger();
	logWrapper = Mylogger.getInstance();
        readConfigFile(configFile);
        
        
        startUrl = currenturl.readStartUrl(configFile, crawlerId);
        currDir  = new File(".");
		path = currDir.getAbsolutePath();
		path =path.substring(0, path.length()-1);
        tcrawler = new Thread(this);
        tcrawler.start();
//		synchMngr.logActivity(log,"PageFinder: Construction Initialized");
	        
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			 throws ServletException, IOException
	{
			// TODO Auto-generated method stub
			try
			{
				Indexer indexer = new Indexer("../webapps/search/WEB-INF/pages", "../webapps/search/WEB-INF/index", "../webapps/search/WEB-INF/stop_words.txt");
				ArrayList<Result> results = indexer.search(request.getQuery(), 10);
				
				for (Result r : results)
				{
					System.out.println(r.title);
					for (String c : r.contexts)
						System.out.println(c);
					System.out.println(r.url);
					//System.out.println(r.filename);
					System.out.println();
				}				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			//	logWrapper.logger.info(" PageFinder: Exiting error: "+e.getMessage());
				
			}
	}	
	
	public void destroy()
	{
		
	}
	
	 // Read Configuration file crawler.conf 
	  private void readConfigFile(String fileName) throws Exception
	  {
		  File currDir  = new File(".");
		  String path = currDir.getAbsolutePath();
			path =path.substring(0, path.length()-1);
			fileName = path+fileName;
	     Properties configFile = new Properties();
	     configFile.load(new FileInputStream(fileName));
	     logWrapper.logger.info("Thread: "+crawlerId+" PageFinder: Configuration File Read");
	     String strTemp;
	     
	     strTemp = configFile.getProperty("limithost").trim();
	     if (strTemp.toLowerCase().equals("true"))
	         limitHost = true;
	     else
	         limitHost = false; //default value for limitHost
	     
	     strTemp = configFile.getProperty("maxurl").trim();
	     if(Integer.parseInt(strTemp) > 0)
	          maxUrls = Integer.parseInt(strTemp);
	     else
	          maxUrls = -1;
	    
	  }
	
	// Check if robot is allowed to access the given URL.
	   private boolean isRobotAllowed(URL urlToCheck) 
	   {
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
	public void run()
	{
		crawling = true;
		String url;
		
		synchMngr.toCrawlList_add(listToCrawl, startUrl);
		ArrayList links = new ArrayList();
		while(crawling && synchMngr.toCrawlList_size(listToCrawl)>0)
		{
			//System.out.println("Entering Iteration\n");
//			synchMngr.logActivity(log,"PageFinder:Entering Iteration");
		logWrapper.logger.info("Thread: "+crawlerId+" PageFinder: Crawling started");
			/* Check to see if the max URL count has
	         been reached, if it was specified.*/
			 if (maxUrls != -1) {
		//            if ((counter[0].intValue() >= maxUrls)||(synchMngr.toCrawlList_size(listToCrawl)==0)) {
		          if ((counter >= maxUrls)||(synchMngr.toCrawlList_size(listToCrawl)==0)) {
//		              synchMngr.logActivity(log,"PageFinder: Crawling Done: max urls reached or end of crawl list");
		logWrapper.logger.info("Thread: "+crawlerId+" PageFinder: Crawling done maxurls reached or all urls crawled");            
			break;
		            }
		         }
	         
			url=(String) synchMngr.toCrawlList_get(listToCrawl);
			 URL verifiedUrl = null;
			//listToCrawl.remove(url);
			if (currenturl.isValidURL(url))
	         {  
	           verifiedUrl = currenturl.getCurrentUrl();
	         }
	          else
	         {  
	            continue;
	         } 
			/*if(synchMngr.crawledList_contain(alreadyCrawledList, url) )
			{
				System.out.println("Already done "+url);
				continue;
			}*/
			// Skip URL if robots are not allowed to access it.
            if (!isRobotAllowed(verifiedUrl)) {
              continue;
            }
			
			// download page using webpage and urlcon class
			pagecontents = currenturl.downloadPage();
			//synchMngr.incCounter(counter);
			counter++;
			//System.out.println("counter: "+synchMngr.getCounter(counter)+" "+crawlerId+"\n");
			synchMngr.writeToAssociation(Associationout, url, path+Utility.getCRC(url));
logWrapper.logger.info("Thread: "+crawlerId+" PageFinder: Counter "+counter+" Max Urls "+maxUrls);
  //                      synchMngr.logActivity(log,"PageFinder: Counter: "+counter+" "+crawlerId+" "+maxUrls);
			//System.out.println("counter: "+counter+" "+crawlerId+" "+maxUrls+"\n");
			//alreadyCrawledList.add(url);
			 synchMngr.crawledList_add(alreadyCrawledList,url);
			
		
			if(pagecontents!=null && pagecontents.getLength()>0)
			{
				
				// Retrieve valid links from the page using WebPageInfo class.
				//store it in list of Array
				if(verifiedUrl!=null)
					{
						
						links = pagecontents.retrieveLinks(alreadyCrawledList,url,limitHost);
						if(links==null)
								continue;
							//System.out.println("After "+ url);
					
	             
	                // Add links to the To Crawl list.
	               // listToCrawl.addAll(links);
						synchMngr.toCrawlList_addAll(listToCrawl, links);
					}
			}
//			synchMngr.logActivity(log,"PageFinder:Exiting Iteration");
logWrapper.logger.info("Thread: "+crawlerId+" PageFinder: Exiting Iteration, links collected from "+url);
			//System.out.println("Exiting iteration\n");
			
		}
	
	}
}
