import java.util.ArrayList;
import java.util.HashSet;


public class Result {
	public String url;
	public ArrayList<String> contexts;
	public String title;
	public String filename;
	
	public Result(String url, ArrayList<String> contexts, String title, String filename) {
		this.url = url;
		this.contexts = contexts;
		this.title = title;
		this.filename = filename;
	}
}
