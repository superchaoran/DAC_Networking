import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class GoogleScholarCrawler {

	private final String USER_AGENT = "Mozilla/5.0";
	
	public static void main(String[] args) throws Exception {
 
		GoogleScholarCrawler http = new GoogleScholarCrawler();
 
		System.out.println("Testing 1 - Send Http GET request");
		String title = "Vehicle Structure Optimization for Crash Pulse";
		String htmlResult = http.sendGet(title);
		
		Document doc = Jsoup.parse(htmlResult);
		Elements elements = doc.select("div[class=gs_r]");
		processRawReturnDivs(elements);
 
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

	public static void processRawReturnDivs(Elements elements) throws IOException{
		
		for(int i=0;i<elements.size();i++){
			Element currentElement = elements.get(i).select("div[class=gs_ri]").first();
			System.out.println("Query Result "+(i+1));
			System.out.println("Title: "+getTitle(currentElement));
			String ret[] = getCitedBy(currentElement);
			System.out.println(ret[0]);
			System.out.println("Link to Cited by:\n"+ret[1]);
			System.out.println("Cited by: ");
			Document doc = Jsoup.connect(ret[1]).data("query", "Java").userAgent("Mozilla").cookie("auth", "token").get();
			Elements cited_by_elements = doc.select("div[class=gs_r]");
			for(int j=0;j<cited_by_elements.size();j++){
				Element current_cited_by_element = cited_by_elements.get(j).select("div[class=gs_ri]").first();;
				System.out.println((j+1)+"."+getTitle(current_cited_by_element)+" ");
			}
		}
	}
	
	public static String getTitle(Element currentElement){
		Element titleElement = currentElement.select("a").first();
		return titleElement.text();
	}
	
	public static String[] getCitedBy(Element currentElement){
		String ret[] = new String[2];
		Element citedAElement = currentElement.select("div.gs_fl").first().select("a").first();
		ret[0] = citedAElement.text();
		String linkHref = citedAElement.attr("href");
		ret[1] = "http://scholar.google.com"+linkHref;
		return ret;
	}
	
	public static String covertUrlToUserFriendly(String url){
		return url.replaceAll(" ", "+");
	}

}
