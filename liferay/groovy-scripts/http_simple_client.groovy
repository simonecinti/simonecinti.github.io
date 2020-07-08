import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.net.HttpURLConnection;
import java.net.URL;

//----------------------------------------------------------
// Simple HTTP Client - Groovy script
//----------------------------------------------------------
//
// Input:

 String urlString = "http://localhost:8080";
 String requestMethod = "GET";  //POST,PUT,DELETE...
 String payload = null;         // insert here the request payload if needed
 Map paramsMap = new LinkedHashMap();
 paramsMap.put("Content-Type", "application/json; utf-8");
 paramsMap.put("Accept", "application/json");

// ----------------------------------------------------------
// Tested with Liferay 7.x CE and DXP
// author: Simone Cinti - 28/07/2020
//-----------------------------------------------------------



try {
	StringBuilder response = _httpClient(urlString, requestMethod, payload, paramsMap);
	out.println(">> response follows this line...\n");
	out.println(response.toString());
} catch (Exception ex) {
	ex.printStackTrace(out);
}
	
StringBuilder _httpClient(String urlString, String requestMethod, String payload, Map requestParameters) throws Exception {
	
	URL url = new URL(urlString);
	
	HttpURLConnection con = (HttpURLConnection)url.openConnection();
	requestMethod = (requestMethod == null) ? "" : requestMethod.trim().toUpperCase();
	con.setRequestMethod(requestMethod);

	for (String paramKey: requestParameters.keySet())  {
		String paramValue = requestParameters.get(paramKey);
		con.setRequestProperty(paramKey, paramValue);
	}

	con.setDoOutput(true);
	
	OutputStream os = null;
	if (payload != null && !"GET".equals(requestMethod)) {
		os = con.getOutputStream();
		byte[] input = payload.getBytes("utf-8");
		out.println(">>writing: " + input.length + " bytes on request payload...");
		os.write(input, 0, input.length);			
	}

	out.println(">>REQUEST METHOD: " + requestMethod + "  URL:" + url);
	int code = con.getResponseCode();
	out.println(">>RESPONSE - HTTP Status Code is: " + code);

	BufferedReader br = null;
	StringBuilder response = null;
	try {
		br =  new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
		String responseLine = null;
		response = new StringBuilder();
		while ((responseLine = br.readLine()) != null) {
			response.append(responseLine.trim());
		}
		if (con != null) {
			try {
				con.close();
			} catch (Exception exc) {
				out.println(">> [!] ERROR while closing the connection: " + exc.getMessage());
			}
		}
	} catch (Exception ex) {
		throw ex;
	}
	return response;
}
