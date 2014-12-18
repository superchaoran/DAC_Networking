import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;


public class ScopusQuery {
	private final String USER_AGENT = "Mozilla/5.0";
	public static void main(String[] args) throws Exception {
 
		ScopusQuery http = new ScopusQuery();
 
		System.out.println("Testing 1 - Send Http GET request");
		String title = "Vehicle Structure Optimization for Crash Pulse";
		String htmlResult = http.sendGet(title);
		List<String> rawReturnDivs = getRawReturnDivs(htmlResult);
		processRawReturnDivs(rawReturnDivs);
 
	}
 
	// HTTP GET request
	@SuppressWarnings("unused")
	private String sendGet(String title) throws Exception {
		title = covertUrlToUserFriendly(title);
		String url = "http://scholar.google.com/scholar?hl=en&inst=569367360547434339&q="+
					title+"&btnG=&as_sdt=1%2C14&as_sdtp=";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		// optional default is GET
		con.setRequestMethod("GET");
 
		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
 
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		

		//print result
		String htmlResult = response.toString();
		System.out.println(htmlResult);

		return htmlResult;
 
	}
	
	public static List<String> getRawReturnDivs(String htmlResult){
		//Find opening of <div
		String className = "gs_ri";
		return getDivByClassName(htmlResult,className);
	}
	
	public static List<String> getDivByClassName(String htmlResult, String className){
		//Find opening of <div
		String regex = divReg(className);
		List<String> matches = getAllMatches(htmlResult, regex);		
		System.out.println(matches);
		return matches;
	}
	
	
	
	public static void processRawReturnDivs(List<String> rawReturnDivs){
		
		for(int i=0;i<rawReturnDivs.size();i++){
			String currentDiv = rawReturnDivs.get(i);
			currentDiv = currentDiv.replaceAll("<div class=\"gs_ri\">", "");
			//currentDiv = currentDiv.su
			System.out.println("Query Result "+(i+1));
			//System.out.println("Title: "+getTitle(currentDiv));
			System.out.println();
		}
	}
	
	public static String getTitle(String rawReturnDiv){
		String className = "gs_rt";
		String title =getDivByClassName(rawReturnDiv,className).get(0);
		//currentDiv.replaceAll("<div class=\"gs_ri\">", "");
		return title;
	}
	
	public static String divReg(String className){
		String divMatch1 ="(<div class=\"";
		String divMatch2 ="\">)((<.+>.*</.+>)*)(</div>)";
		return divMatch1+className+divMatch2;
	}
	
    public static List<String> getAllMatches(String text, String regex) {
        List<String> matches = new ArrayList<String>();
        Matcher m = Pattern.compile("(?=(" + regex + "))").matcher(text);
        while(m.find()) {
            matches.add(m.group(1));
        }
        return matches;
    }
	
	public static String covertUrlToUserFriendly(String url){
		return url.replaceAll(" ", "+");
	}
}
