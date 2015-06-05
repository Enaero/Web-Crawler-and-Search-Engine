import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Mylogger {
	public static final Logger logger = Logger.getLogger("Error-while-crawling");
	private static Mylogger instance = null;
	
	public static Mylogger getInstance()

	{
		if(instance == null)
		{
		prepareLogger();
		instance = new Mylogger();
		}
		return instance;
	}
	private static void prepareLogger()
	{
		try
		{
			FileHandler file = new FileHandler("LogFile.log");
			file.setFormatter(new SimpleFormatter());
			logger.addHandler(file);
			logger.setUseParentHandlers(false);
		}
		catch(Exception e){}

	}
	



	/*Logger logger = Logger.getLogger("Errors-While-Crawling");
	FileHandler fh;
	File currDir;
	String path;
	public Mylogger() throws IOException
	{
		currDir  = new File(".");
		path = currDir.getAbsolutePath();
		path =path.substring(0, path.length()-1);
		path=path+"LogFile.log";
		System.out.println("path: "+path);
		fh = new FileHandler(path);
		logger.addHandler(fh);
 		//logger.setUseParentHandlers(false);
		// logger.setLevel(Level.ALL);
		SimpleFormatter formatter = new SimpleFormatter();
		fh.setFormatter(formatter);
		
	}
	
	public void log(String Error) {

		
		try {

			// This block configure the logger with handler and formatter
			

			// the following statement is used to log any messages
	logger.info(Error);		

		} catch (SecurityException e) {
			e.printStackTrace();
		}

		// logger.info("Hi How r u?");

	}*/

}
