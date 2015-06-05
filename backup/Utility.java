import java.net.URL;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Utility 
{
	public static String getCRC(String url)
	{
		byte bytes[] = url.getBytes();
		 
		Checksum checksum = new CRC32();
		
		// update the current checksum with the specified array of bytes
		checksum.update(bytes, 0, bytes.length);
		 
		// get the current checksum value
		long checksumValue = checksum.getValue();
		 
		System.out.println("CRC32 checksum for input string is: " + checksumValue);
		String s =String.valueOf(checksumValue);
		return s;
	}
	public static URL verifyUrl(String url) 
	   {
	      // Only allow HTTP URLs.
	      if (!url.toLowerCase().startsWith("http://"))
	        return null;
	      
	      URL verifiedUrl = null;
	      try {
	        verifiedUrl = new URL(url);
	      } catch (Exception e) {
	        return null;
	      }
	      return verifiedUrl;
	   }
}
