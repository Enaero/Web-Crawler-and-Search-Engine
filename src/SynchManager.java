import java.io.BufferedWriter;
import java.util.*;

// Class of SynchManager
public class SynchManager{
  
    synchronized void crawledList_add(HashSet _crawledList, String url)
    {
      // Add page to the crawled list.
      _crawledList.add(url);
      System.out.println("Added"+"\t"+url);
    }
    synchronized boolean crawledList_contain(HashSet _crawledList, String url)
    {
      return _crawledList.contains(url);
    }
    synchronized String toCrawlList_get(LinkedHashSet _toCrawlList)
    {
      // Get URL at bottom of the list.
      String url = (String) _toCrawlList.iterator().next();
          
      // Remove URL from the To Crawl list.
      _toCrawlList.remove(url);
      
      return(url);
    }
    
    synchronized void toCrawlList_add(LinkedHashSet _toCrawlList,String url)
    {
       _toCrawlList.add(url);
    }
    synchronized void toCrawlList_addAll(LinkedHashSet _toCrawlList, ArrayList _links)
    {
       // Add links to the To Crawl list.
       _toCrawlList.addAll(_links);
    }
    
    synchronized int  incCounter( Integer[] _counter)
    {
      _counter[0] = _counter[0] + 1;
      return _counter[0];
    }
    
     synchronized int getCounter(Integer[] _counter )
    {
      return _counter[0];
    }
    
    synchronized int toCrawlList_size(LinkedHashSet _toCrawlList)
    {
       return  _toCrawlList.size();
    }
    synchronized void writeToAssociation(BufferedWriter out, String url, String filename)
    {
    	try
    	{
    	out.flush();
    	out.write(url+"\t"+filename+"\n");
    	
    	System.out.println("in Association\n");
    	}
    	catch(Exception e){}
    }
/*    synchronized void logActivity(Mylogger log, String msg)
    {
	try{
	 log =new Mylogger();
	log.log(msg);
	}
        catch(Exception e){}
    }*/
  
}//Class SynchManager
