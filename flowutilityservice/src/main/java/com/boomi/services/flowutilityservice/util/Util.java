package com.boomi.services.flowutilityservice.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Util {
	
	public static JSONObject copyPage(JSONArray pages, String pageId) throws IOException
	{
		JSONObject page = findObjectByID(pages, pageId);
		String pageName = findUniqueName(page.getString("developerName"), pages);
		
		page.put("id", UUID.randomUUID().toString());
		page.put("developerName", pageName);
		
		page.put("developerSummary", "COPIED AT: " + (new Date()).toString());
		
		page.remove("whoCreated");
		page.remove("whoModified");
		page.remove("whoOwner");
		page.remove("dateCreated");
		page.remove("dateModified");
		return page;
	}
	
	public static JSONObject copyValue(JSONArray values, String valueId) throws IOException
	{
		JSONObject value = findObjectByID(values, valueId);
		String valueName = findUniqueName(value.getString("developerName"), values);
		
		value.put("id", UUID.randomUUID().toString());
		value.put("developerName", valueName);
		
		value.put("developerSummary", "COPIED AT: " + (new Date()).toString());
		
		value.remove("whoCreated");
		value.remove("whoModified");
		value.remove("whoOwner");
		value.remove("dateCreated");
		value.remove("dateModified");
		return value;
	}
	
	public static JSONObject copyType(JSONArray types, String typeId) throws IOException
	{
		JSONObject type = findObjectByID(types, typeId);
		String typeName = findUniqueName(type.getString("developerName"), types);
		
		type.put("id", UUID.randomUUID().toString());
		type.put("developerName", typeName);
		
		type.put("developerSummary", "COPIED AT: " + (new Date()).toString());
		
		type.remove("whoCreated");
		type.remove("whoModified");
		type.remove("whoOwner");
		type.remove("dateCreated");
		type.remove("dateModified");
		return type;
	}

	public static String findUniqueName(String candidate, JSONArray objects)
	{
		int index=0;
		if (objects!=null)
		{
		    for (int i = 0; i < objects.length(); i++) {
				if (objects.getJSONObject(i).getString("developerName").startsWith(candidate))
				{
					index++;
				}
			}
			if (index>0)
			{
				candidate = candidate + " ("+index+")";
			}
		}
		return candidate;
	}	
	
	public static void putDeveloperSummary(JSONObject obj)
	{
		obj.put("developerSummary", "GENERATED AT: " + (new Date()).toString());
	}
	
	public static JSONObject findObjectByID(JSONArray objects, String id)
	{
		if (id!=null)
		{
			for (int i=0; i<objects.length(); i++)
			{
				JSONObject object = (JSONObject)objects.getJSONObject(i);
				if (!object.isNull("id") && object.getString("id").contentEquals(id))
				{
					return objects.getJSONObject(i);
				}
			}
		}
		return null;
	}
	
	public static JSONObject findObjectByName(JSONArray objects, String developerName)
	{
		if (developerName!=null)
		{
			for (int i=0; i<objects.length(); i++)
			{
				JSONObject object = (JSONObject)objects.getJSONObject(i);
				if (!object.isNull("developerName") && object.getString("developerName").contentEquals(developerName))
				{
					return objects.getJSONObject(i);
				}
			}
		}
		return null;
	}
	
	public static JSONObject fileUploadTypeFromXML(String XML) throws Exception
	{
		JSONObject newType = new JSONObject();
		DocumentBuilderFactory factory =
		DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		ByteArrayInputStream input = new ByteArrayInputStream(
		XML.getBytes("UTF-8"));
		Document doc = builder.parse(input); 
		Element top = doc.getDocumentElement();
//TODO Check if this is a "resourceType": "Bundle", if not it is a GET	            
        NodeList items = top.getElementsByTagName("JSONRootValue");		
    	if (items!=null && items.getLength()>0)
    	{
    		Element root = (Element) items.item(0);
    		NodeList objectRoots = root.getChildNodes();
    		for (int i = 0; objectRoots !=null && i< objectRoots.getLength();i++)
    		{
    			Node objNode = objectRoots.item(i);
    			if (objNode.getNodeType() == Node.ELEMENT_NODE)
    			{
        			Element jsonObject = (Element) objNode;    	
        			//First child is the root of the profile
        			if (jsonObject.getTagName().contentEquals("JSONObject"))
        			{
        				newType.put("developerName", "$File RESPONSE - " + jsonObject.getAttribute("name"));
        				newType.put("id", UUID.randomUUID().toString());
        				newType.put("elementType", "TYPE");
        	    		NodeList objectEntries = jsonObject.getChildNodes();
        	    		JSONArray properties = new JSONArray();
        	    		newType.put("properties", properties);
        	    		for (int j = 0; objectEntries !=null && j < objectEntries.getLength();j++)
        	    		{
        	    			Node entryNode = objectEntries.item(j);
                			if (entryNode.getNodeType() == Node.ELEMENT_NODE)
                			{
            	    			Element objectEntry = (Element) entryNode;
            	    			JSONObject property = new JSONObject();
            	    			properties.put(property);
            	    			property.put("id", UUID.randomUUID().toString());
            	    			property.put("developerName", objectEntry.getAttribute("name"));
            	    			String contentType = objectEntry.getAttribute("name");
            	    			switch (objectEntry.getAttribute("dataType"))
            	    			{
            	    			case "number":
            	    				contentType = "ContentNumber";
            	    				break;    	    			
            	    			case "datetime":
            	    				contentType = "ContentDateTime";
            	    				break;
            	    			case "boolean":
            	    				contentType = "ContentBoolean";
            	    				break;
            	    			default:
            	    				contentType = "ContentString";
            	    			}
            	    			property.put("contentType", contentType);
                			}
        	    		}     				
        				break; 
        			}
    			}
    		}
    	}
    	String requiredMsg = checkAllRequiredElements(newType.getJSONArray("properties"));
    	if (requiredMsg.length() > 0)
    		throw new Exception(requiredMsg);
		return newType;		
	}
	
	static String checkAllRequiredElements(JSONArray properties)
	{
		StringBuilder errMsg = new StringBuilder();
		hasReqiredProperty(properties,"externalId","ContentString", errMsg);		
		hasReqiredProperty(properties,"Date Created","ContentDateTime", errMsg);		
		hasReqiredProperty(properties,"Date Modified","ContentDateTime", errMsg);		
		hasReqiredProperty(properties,"Description","ContentString", errMsg);		
		hasReqiredProperty(properties,"Download Uri","ContentString", errMsg);		
		hasReqiredProperty(properties,"Embed Uri","ContentString", errMsg);		
		hasReqiredProperty(properties,"Icon Uri","ContentString", errMsg);		
		hasReqiredProperty(properties,"Id","ContentString", errMsg);		
		hasReqiredProperty(properties,"Kind","ContentString", errMsg);		
		hasReqiredProperty(properties,"Mime Type","ContentString", errMsg);		
		hasReqiredProperty(properties,"Name","ContentString", errMsg);		
		return errMsg.toString();
	}
	
	static void hasReqiredProperty(JSONArray properties, String name, String type, StringBuilder errMsg)
	{
		for (Object obj : properties)
		{
			JSONObject property = (JSONObject) obj;
			if (property.getString("developerName").contentEquals(name)
					&& property.getString("contentType").contentEquals(type))
				return;
		}
		errMsg.append("Missing: " + name + ", " + type + "<br/>");
		return;
	}
	
    static String inputStreamToString(InputStream is) throws IOException
    {
    	try (Scanner scanner = new Scanner(is, "UTF-8")) {
    		return scanner.useDelimiter("\\A").next();
    	}
    }

	static String readResource(String resourcePath, Class theClass) throws Exception
	{
		String resource = null;
		try {
			InputStream is = theClass.getClassLoader().getResourceAsStream(resourcePath);
			resource = inputStreamToString(is);
			
		} catch (Exception e)
		{
			throw new Exception("Error loading resource: "+resourcePath + " " + e.getMessage());
		}

		return resource;
	}
}
