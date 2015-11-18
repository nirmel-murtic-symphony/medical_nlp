package edu.pitt.medical_nlp.utility;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import edu.pitt.medical_nlp.Config;

public class MetaType {

	static HashMap<String, String> cached = new HashMap<>();

	public static String requestWeb(String term) {
		if (!Config.ADD_SYNONYM) {
			return term;
		}
		try {
			term = term.replace(" ", "+");
			if (cached.containsKey(term)) {
				return cached.get(term);
			}
			URL url = new URL(Config.URL_MESH);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			String urlParameters = "term=" + term + "&exact=Find+Exact+Term&field=all";

			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			// int responseCode = con.getResponseCode();
			// System.out.println("\nSending 'POST' request to URL : " + url);
			// System.out.println("Post parameters : " + urlParameters);
			// System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			// System.out.println(response.toString());

			Pattern patt = Pattern
					.compile("<TR><TH align=left>Entry Term<\\/TH><TD colspan=1>([a-zA-Z ,]+)<\\/TD><\\/TR>");
			Matcher matcher = patt.matcher(response);
			while (matcher.find()) {
				String group = matcher.group(1);
				System.out.println(group);
			}

			patt = Pattern.compile("<TR><TH align=left>MeSH Heading<\\/TH><TD colspan=1>([a-zA-Z ,]+)<\\/TD><\\/TR>");
			matcher = patt.matcher(response);
			while (matcher.find()) {
				String group = matcher.group(1);
				System.out.println(group);
				cached.put(term, group);
				return group;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		cached.put(term, "");
		return "";
	}

	// public void initLocal() {
	//
	// try {
	// File fXmlFile = new File(Config.PATH_MESH);
	// DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	// DocumentBuilder dBuilder;
	// dBuilder = dbFactory.newDocumentBuilder();
	// Document doc = dBuilder.parse(fXmlFile);
	//
	// // optional, but recommended
	// // read this -
	// //
	// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
	// doc.getDocumentElement().normalize();
	//
	// NodeList desc_list = doc.getElementsByTagName("DescriptorRecord");
	//
	// for (int i = 0; i < desc_list.getLength(); i++) {
	// Node desc = desc_list.item(i);
	// @SuppressWarnings("unused")
	// NodeList concept_list = ((Element) desc).getElementsByTagName("Concept");
	// throw new NotImplementedException();
	//
	// }
	//
	// } catch (ParserConfigurationException e) {
	// e.printStackTrace();
	// } catch (SAXException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
}
