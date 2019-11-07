package com.boomi.services.flowutilityservice.util;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import com.boomi.services.flowutilityservice.getservices.ServiceDefinition;
import com.boomi.services.flowutilityservice.gettenants.Tenant;

public class FlowGenerateTest {	
	
	JSONArray services;
	JSONArray types;
	JSONArray values;
	JSONArray pages;
	JSONArray flows;
	
	
	public void init() throws JSONException, Exception
	{
		services = new JSONArray(Util.readResource("resources/mockServices.json", this.getClass()));
		types = new JSONArray(Util.readResource("resources/mockTypes.json", this.getClass()));
		values = new JSONArray(Util.readResource("resources/mockValues.json", this.getClass()));
		pages = new JSONArray(Util.readResource("resources/mockPages.json", this.getClass()));
		flows = new JSONArray(Util.readResource("resources/mockFlows.json", this.getClass()));
	}
	
    @Test
	public void testGenerateDesignPatter() throws Exception
	{    	
    	String token = FlowAPIClient.login("dave.hock@dell.com", "Gopack!23");
		List<Tenant> tenants=FlowAPIClient.getTenants(token);
		String tenantId="72b3a5ea-5667-466e-820a-362dcc375834";
		Tenant tenantObj=null;
		for (Tenant t : tenants)
		{
			if (t.getId().contentEquals(tenantId))
				tenantObj=t;			
		}
		
		FlowDefinition generator = new FlowDefinition(token, tenantId, "8dc0df53-d2e5-4675-a652-0a9094b4d9a3","","8ff491a8-ce91-41e2-b4aa-1e9775c96037", "", "", null, null, null, "" );
		generator.generateDesignPattern();
//		JSONArray flows = new JSONArray(FlowAPIClient.doGet("/api/draw/1/flow", token, tokenId));
//		JSONObject newFlow = FlowAPIUtil.createFlow(flows, "TEST");		
//		newFlow=new JSONObject(FlowAPIClient.doPost("/api/draw/1/flow", token, tokenId, newFlow.toString()));
//		System.out.println(newFlow.toString(2));
//		JSONArray newFlowMap = FlowAPIUtil.createFlowMap(newFlow, this.getClass());
//		String uri = String.format("/api/draw/1/flow/%s/%s/element/map", newFlow.getJSONObject("id").getString("id"), newFlow.getString("editingToken"));
//		for (Object obj:newFlowMap)
//		{
//			JSONObject map = (JSONObject)obj;
//			if (map.getString("elementType").contentEquals("START"))
//				map.put("id", newFlow.getString("startMapElementId"));
//			else
//				map.put("id", UUID.randomUUID().toString());
//			String res=FlowAPIClient.doPost(uri, token, tokenId, map.toString());
//		}
	}		
}
