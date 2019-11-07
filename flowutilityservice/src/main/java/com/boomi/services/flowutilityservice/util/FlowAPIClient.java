package com.boomi.services.flowutilityservice.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;
import com.boomi.services.flowutilityservice.gettenants.Tenant;

public class FlowAPIClient {
	private static String host="https://flow.manywho.com";
	private static String baseDrawURI="/api/draw/1/";
	private static String baseAdminURI="/api/admin/1/";
	
	private static HttpsURLConnection getConnection(String uri, String method) throws IOException
	{
		String httpsURL = host+uri;
		URL myUrl = new URL(httpsURL);
		HttpsURLConnection conn = (HttpsURLConnection)myUrl.openConnection();
		conn.setRequestMethod(method);
//		conn.setRequestProperty("Accept", "application/json; charset=utf-8");
		conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		conn.setDoOutput(true);
		return conn;
	}
	
	public static String login(String userName, String password) throws IOException
	{
		HttpsURLConnection conn = getConnection(baseDrawURI+"authentication", "POST");
		conn.setDoInput(true);
//		conn.setRequestProperty("Content-Type", "plain/text");
        JSONObject body = new JSONObject();
        body.put("username", userName);
        body.put("password", password);
//        conn.setRequestProperty ("Authorization", body.toString());
       
        String str = body.toString();
        byte[] input = str.getBytes();
 		conn.setRequestProperty("Content-Length", ""+input.length);
 
        OutputStream os = conn.getOutputStream();
        os.write(input, 0, input.length);  
        String token = getResponse(conn);

        token=token.replace("\"", "");
        return token;
	}	
	
	private static String getResponse(HttpsURLConnection conn) throws IOException
	{
        StringBuffer response= new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())) ;
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
			    response.append(responseLine.trim());
			}
//	    System.out.println(response.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream())) ;
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
			    response.append(responseLine.trim());
			}
			throw new IOException(response.toString());
		}
	    return response.toString();
	}

	public static String doGet(String uri, String token, String tenant) throws IOException
	{
//		System.out.println(uri+ " " + token + " " + tenant);
		HttpsURLConnection conn = getConnection(uri, "GET");
		conn.setDoOutput(false);
        conn.setRequestProperty ("Authorization", token);
		if (tenant!=null)
			conn.setRequestProperty("manywhotenant", tenant);
        return getResponse(conn);
	}
	
	public static String doDelete(String uri, String token, String tenant) throws IOException
	{
//		System.out.println(uri+ " " + token + " " + tenant);
		HttpsURLConnection conn = getConnection(uri, "DELETE");
		conn.setDoOutput(false);
        conn.setRequestProperty ("Authorization", token);
		if (tenant!=null)
			conn.setRequestProperty("manywhotenant", tenant);
        return getResponse(conn);
	}
	
	public static String doPost(String uri, String token, String tenant, String payload) throws IOException
	{
		System.out.println(uri);
		System.out.println("payload:"+payload);
		HttpsURLConnection conn = getConnection(uri, "POST");
        conn.setRequestProperty ("Authorization", token);
		if (tenant!=null)
			conn.setRequestProperty("manywhotenant", tenant);
        String str = payload.toString();
        byte[] input = str.getBytes();
 		conn.setRequestProperty("Content-Length", ""+input.length);
 
        OutputStream os = conn.getOutputStream();
        os.write(input, 0, input.length);  
        String resp = getResponse(conn);
        return resp;
	}
	
	public static List<Tenant> getTenants(String token) throws IOException
	{
	    JSONObject json = new JSONObject(doGet(baseAdminURI+"users/me", token, null));
	    List<Tenant> tenants = new ArrayList<Tenant>();
	    for (Object tenant:json.getJSONArray("tenants"))
	    {
	    	tenants.add(new Tenant(((JSONObject)tenant).getString("id"), ((JSONObject)tenant).getString("developerName"), UUID.randomUUID().toString()));
	    }
	    return tenants;	    
	}	
	
	public static JSONArray getServices(String token, String tenant) throws IOException
	{
		JSONArray array = new JSONArray(doGet(baseDrawURI+"element/service", token, tenant));
	    return array;
	}	
	
	public static JSONArray getTypes(String token, String tenant) throws IOException
	{
		JSONArray array = new JSONArray(doGet(baseDrawURI+"element/type", token, tenant));
		return array;
	}	
	
	public static JSONArray getValues(String token, String tenant) throws IOException
	{
		JSONArray array = new JSONArray(doGet(baseDrawURI+"element/value", token, tenant));
		return array;
	}	
	
	public static JSONArray getPages(String token, String tenant) throws IOException
	{
		JSONArray array = new JSONArray(doGet(baseDrawURI+"element/page", token, tenant));
		return array;
	}	
	
	public static JSONArray getFlows(String token, String tenant) throws IOException
	{
		JSONArray array = new JSONArray(doGet(baseDrawURI+"flow", token, tenant));
		return array;
	}	

	public static JSONArray createType(String token, String tenant) throws IOException
	{
		JSONArray array = new JSONArray(doGet(baseDrawURI+"element/type", token, tenant));
		return array;
	}	

	public static JSONArray createValue(String token, String tenant) throws IOException
	{
		JSONArray array = new JSONArray(doGet(baseDrawURI+"element/value", token, tenant));
		return array;
	}	

	public static JSONArray createPage(String token, String tenant) throws IOException
	{
		JSONArray array = new JSONArray(doGet(baseDrawURI+"element/page", token, tenant));
		return array;
	}	

	public static JSONArray createFlow(String token, String tenant) throws IOException
	{
		JSONArray array = new JSONArray(doGet(baseDrawURI+"element/flow", token, tenant));
		return array;
	}	
	
	public static String copyPage(String token, String tenant, String pageId) throws IOException
	{
		JSONArray pages = getPages(token, tenant);
		JSONObject newPage = Util.copyPage(pages, pageId);
		doPost(baseDrawURI+"element/page", token, tenant, newPage.toString());
		return newPage.getString("developerName");
	}

	public static String copyType(String token, String tenant, String typeId) throws IOException
	{
		JSONArray types = getTypes(token, tenant);
		JSONObject newtype = Util.copyType(types, typeId);
		doPost(baseDrawURI+"element/type", token, tenant, newtype.toString());
		return newtype.getString("developerName");
	}

	public static String copyValue(String token, String tenant, String valueId) throws IOException
	{
		JSONArray values = getValues(token, tenant);
		JSONObject newvalue = Util.copyValue(values, valueId);
		doPost(baseDrawURI+"element/value", token, tenant, newvalue.toString());
		return newvalue.getString("developerName");
	}
}
