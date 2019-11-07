package com.boomi.services.flowutilityservice.util;

import static org.junit.Assert.assertTrue;
import java.util.List;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import com.boomi.services.flowutilityservice.gettenants.Tenant;

public class FlowAPIClientTest {
	String userName = "dave.hock@dell.com";
	String password = "Gopack!23";
	
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
}
