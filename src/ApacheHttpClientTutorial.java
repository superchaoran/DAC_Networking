import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;


public class ApacheHttpClientTutorial {
  
  public String getContent(String url) throws ClientProtocolException, IOException{
	  //url = "http://www.apache.org/";
	  HttpClient client = new DefaultHttpClient();
	  HttpGet request = new HttpGet("http://www.vogella.com");
	  HttpResponse response = client.execute(request);

	  // Get the response
	  BufferedReader rd = new BufferedReader
	    (new InputStreamReader(response.getEntity().getContent()));
	      
		StringBuffer retBuffer = new StringBuffer();
		String inputLine;
		while ((inputLine = rd.readLine()) != null) {
			retBuffer.append(inputLine);
		}
		return response.toString();
  }
}
