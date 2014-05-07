package a7;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/** HTTP Client demo program. Can both GET and POST to a URL and read input back from the
 * server. Designed to be used with the demoServlet servlet.
 * Run as demoClient.Main http://localhost:8080/demoServlet/ to read the current message.
 * Run as demoClient.Main http://localhost:8080/demoServlet/ <msg> to change the message.
 */
public class MainClient {
	public static StringBuffer jb1 = new StringBuffer();
	public static void main(String[] args) {
		if (args.length != 1 && args.length != 2) {
			usage();
		}
		new MainClient(args[0], args.length == 2 ? args[1] : null);
	}
	private static void usage() {
		System.err.println("Usage: MyClient <URL> [<message>]");
		System.exit(1);
	}
	
	private URL url;
	
	/** If message is null, query the URL (GET) for the current message.
	 * Otherwise, use a POST request to send the message.
	 */
	public MainClient(String url, String message) {
		try {
			this.url = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();
			if (message == null) {
				System.out.println("ff");
				connection.connect();
				System.out.println("Doing GET " + url);
				BufferedReader r = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				dumpResponse(r);
			} else {
				System.out.println("Doing POST "+ url);
				connection.setDoOutput(true); // send a POST message
				connection.setRequestMethod("POST");
				PrintWriter w = new PrintWriter(connection.getOutputStream());
				w.println(message);
				w.flush();
				BufferedReader r = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				dumpResponse(r);				
			}
		} catch (MalformedURLException e) {
			usage();
		} catch (IOException e) {
			System.err.println("IO exception: " + e.getMessage());
		}
	}
	/** Read back output from the server. Could change to parse JSON... */
	void dumpResponse(BufferedReader r) throws IOException {
		String l = r.readLine();
		if(l == null) {
			jb1 = new StringBuffer();
			return;
		}
		String l2 = l;
		while (l != null) {
			l2 = l;
			l = r.readLine();
		}
		jb1 = new StringBuffer(l2);
	}
	
	int getNumCreated() throws JSONException{
		JSONObject j = new JSONObject(jb1);
		JSONArray j1 = j.getJSONArray("ids");
		return j1.length();
	}
	
	public static StringBuffer getResponse() throws JSONException{
		return jb1;
	}
}