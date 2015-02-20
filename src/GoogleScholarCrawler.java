import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class GoogleScholarCrawler {

	private static Set<String> dac_url = new HashSet<String>();
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		//get all dac_url in a hashSet
		converDACUrlFileToHashSet();
		
		//Read title file line by line then plug titles to Google Scholar for search
		BufferedReader br = null;
		
		//Get title line from file and crawl, after each crawl write them to file
		String sCurrentLine;
		br = new BufferedReader(new FileReader("DAC_Paper_Titles_Cleaned.txt"));
		
		File file = new File("DAC_nonDAC.txt");
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		File file2 = new File("count.txt");
		// if file doesnt exists, then create it
		if (!file2.exists()) {
			file2.createNewFile();
		}
		FileReader fr2 = new FileReader(file2);
		BufferedReader br2 = new BufferedReader(fr2);
		int skippedCount = Integer.parseInt(br2.readLine());
		br2.close();fr2.close();
		
		int count = 0;
		while ((sCurrentLine = br.readLine()) != null) {

			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			
			FileWriter fw2 = new FileWriter(file2, false);
			BufferedWriter bw2 = new BufferedWriter(fw2);
			
			if(count<skippedCount){
				count++;bw.close();bw2.close();
				System.out.println(count);
				continue;
			}
			int randomTime = (int)(Math.random()*30)*1000+61000;
			Thread.sleep(randomTime);
			DataObject obj = new DataObject();
			//for fixing /u003d
			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			// convert java object to JSON format,
			// and returned as JSON formatted string
			prepairTitle(sCurrentLine, bw, obj);
			String json = gson.toJson(obj);
			//System.out.println(json);
			bw.write(json+"\n");
			//line by line print out in a file
			/*memo = {

					(P_1,[t1,t2,t3,t4...])

					,(P_2,[t1,t2....])......}*/
			count++;
			System.out.println(count);
			bw2.write(count);
			bw2.close();
			bw.close();
		}
	}
	
	@SuppressWarnings("unused")
	private static void prepairTitle(String title, BufferedWriter bw, DataObject obj) throws Exception{
		GoogleScholarCrawler http = new GoogleScholarCrawler();
		//title = "Vehicle Structure Optimization for Crash Pulse";
		String htmlResult = http.sendGet(title);
		
		Document doc = Jsoup.parse(htmlResult);
		Elements elements = doc.select("div[class=gs_r]");
		processRawReturnDivs(elements, bw, obj);
	}
 
	// HTTP GET request
	@SuppressWarnings("unused")
	private String sendGet(String title) throws Exception {
		title = covertUrlToUserFriendly(title);
		String url = "http://scholar.google.com/scholar?q="+title;
		//String url = "http://scholar.google.com";
		//print result
		String htmlResult = getContent(url);
		System.out.println(htmlResult);
		return htmlResult;
	}
	
	
	  public String getContent(String url) throws ClientProtocolException, IOException{
		  //url = "http://www.apache.org/";
		  @SuppressWarnings({ "deprecation", "resource" })
		  HttpClient client = new DefaultHttpClient();
		  HttpGet request = new HttpGet(url);
		  HttpResponse response = client.execute(request);

		  // Get the response
		  BufferedReader rd = new BufferedReader
		    (new InputStreamReader(response.getEntity().getContent()));
		      
			StringBuffer retBuffer = new StringBuffer();
			String inputLine;
			while ((inputLine = rd.readLine()) != null) {
				retBuffer.append(inputLine);
			}
			return retBuffer.toString();
	  }

	public static void processRawReturnDivs(Elements elements, BufferedWriter bw, DataObject obj) throws IOException, InterruptedException{
		for(int i=0;i<elements.size();i++){
			Element currentElement = elements.get(i).select("div[class=gs_ri]").first();
			
			String ret[] = getCitedBy(currentElement);
			String link = currentElement.select("a").first().attr("abs:href");
			Paper p = new Paper(getTitle(currentElement),link,isDAC(link.substring(link.length()-7)));
			obj.setPaper(p);
			if(ret == null){
				//Cited by is not there
				return;
			}
			
			int randomTime = (int)(Math.random()*30)*1000+61000;
			Thread.sleep(randomTime);
			Document doc = Jsoup.connect(ret[1]).data("query", "Java").userAgent("Mozilla").cookie("auth", "token").get();
			Elements cited_by_elements = doc.select("div[class=gs_r]");
			for(int j=0;j<cited_by_elements.size();j++){
				Element current_cited_by_element = cited_by_elements.get(j).select("div[class=gs_ri]").first();;
				String href = current_cited_by_element.select("a").first().attr("abs:href");
				Paper tempP=new Paper(getTitle(current_cited_by_element),href,isDAC(href.substring(href.length()-7)));
				obj.addList(tempP);
			}
		}
	}
	
	public static void converDACUrlFileToHashSet() throws IOException{
		File file = new File("dac_url.json");
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		StringBuffer stringBuffer = new StringBuffer();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			line = line.trim();
			if(line.charAt(0)=='"'){
				if(line.charAt(line.length()-1)==',')
					line = line.substring(1,line.length()-2);
				else
					line = line.substring(1,line.length()-1);
				line=line.substring(line.length()-7);
			}
			//System.out.println(line);
			dac_url.add(line);
		}
	}
	public static boolean isDAC(String url){
		//System.out.println(url);
		return dac_url.contains(url);
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
		
		if(ret[0].contains("Cited by")){
			return ret;
		}else{
			return null;
		}
	}
	
	public static String covertUrlToUserFriendly(String url){
		return url.replaceAll(" ", "+");
	}

}
