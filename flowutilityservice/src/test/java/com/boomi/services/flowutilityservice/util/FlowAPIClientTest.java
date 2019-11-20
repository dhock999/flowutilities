package com.boomi.services.flowutilityservice.util;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import com.boomi.services.flowutilityservice.gettenants.Tenant;

public class FlowAPIClientTest {
	String userName = "dave.hock@dell.com";
	String password = "";
	
	@Test
    public void testLogin() throws Exception {
    	String token = FlowAPIClient.login(userName, password);
    	assertTrue(token.length()>0);
    }

	@Test
    public void testGetTenants() throws Exception {
    	String token = FlowAPIClient.login(userName, password);
		List<Tenant> tenants=FlowAPIClient.getTenants(token);
    	assertTrue(tenants.size()>0);    	
    }

	@Test
    public void testGetServices() throws Exception {
    	String token = FlowAPIClient.login(userName, password);
		List<Tenant> tenants=FlowAPIClient.getTenants(token);
		JSONArray services=FlowAPIClient.getServices(token, tenants.get(1).getId());		
    	assertTrue(services.length()>0);    	
    }

	@Test
    public void testGetTypes() throws Exception {
    	String token = FlowAPIClient.login(userName, password);
		List<Tenant> tenants=FlowAPIClient.getTenants(token);
		JSONArray types=FlowAPIClient.getTypes(token, tenants.get(1).getId());		
    	assertTrue(types.length()>0);    	
    }
	
	@Test
    public void testGetPages() throws Exception {
    	String token = FlowAPIClient.login(userName, password);
		List<Tenant> tenants=FlowAPIClient.getTenants(token);
		JSONArray types=FlowAPIClient.getPages(token, tenants.get(1).getId());		
    	assertTrue(types.length()>0);    	
    }
	
    @Test 
    void deleteAllGenerated() throws IOException
    {
    	String token = FlowAPIClient.login(userName, password);
		List<com.boomi.services.flowutilityservice.gettenants.Tenant> tenants=FlowAPIClient.getTenants(token);
		String tenantId="72b3a5ea-5667-466e-820a-362dcc375834";
//		tenantId = "c7a2e361-ad90-4079-8b1c-6c21a9f911a8";
		deleteGeneratedTypes(token, tenantId, "page");
		deleteGeneratedTypes(token, tenantId, "macro");
		deleteGeneratedTypes(token, tenantId, "value");
		deleteGeneratedTypes(token, tenantId, "type");
    }
    
    static void deleteGeneratedTypes(String token, String tenantId, String type) throws JSONException, IOException
    {
    	String baseURI = "/api/draw/1/element/"+type;
		JSONArray objects = new JSONArray(FlowAPIClient.doGet(baseURI, token, tenantId));
		for (Object obj: objects)
		{
			JSONObject jObj = (JSONObject)obj;
			if (!jObj.isNull("developerSummary") && jObj.getString("developerSummary").startsWith("GENERATED"))
			{
				try {
					FlowAPIClient.doDelete(baseURI+"/"+jObj.getString("id"), token, tenantId);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println(jObj.getString("developerName") + " " + e.getMessage());
				}
			}
		}
    }
}
