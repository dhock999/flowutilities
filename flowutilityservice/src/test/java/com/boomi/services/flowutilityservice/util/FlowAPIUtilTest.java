package com.boomi.services.flowutilityservice.util;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import com.boomi.services.flowutilityservice.getserviceitems.ServiceItem;
import com.boomi.services.flowutilityservice.getservices.ServiceDefinition;
import com.boomi.services.flowutilityservice.util.Tenant;
import com.boomi.services.flowutilityservice.gettypefields.Field;
import com.boomi.services.flowutilityservice.util.PageLayout.OutputPageOption;

public class FlowAPIUtilTest {	
	
	JSONArray services;
	JSONArray types;
	JSONArray values;
	JSONArray pages;
	JSONArray flows;
	Tenant tenant;
	PageLayout pageLayout;
	
	
	public void init() throws JSONException, Exception
	{
		tenant=new Tenant();
		services = new JSONArray(Util.readResource("resources/mockServices.json", this.getClass()));
		types = new JSONArray(Util.readResource("resources/mockTypes.json", this.getClass()));
		values = new JSONArray(Util.readResource("resources/mockValues.json", this.getClass()));
		pages = new JSONArray(Util.readResource("resources/mockPages.json", this.getClass()));
		flows = new JSONArray(Util.readResource("resources/mockFlows.json", this.getClass()));
		tenant.setServices(services);
		tenant.setTypes(types);
		tenant.setValues(values);
		tenant.setPages(pages);
		tenant.setFlows(flows);
		pageLayout = new PageLayout(tenant, "");

	}
	
    @Test
    public void testGetDesignPattern() throws Exception {
		init();
		String serviceId="";
		List<ServiceItem> flowServices = Service.getFlowServiceItems(serviceId, services, types);

		for (int i=0; i<flowServices.size(); i++)
		{
			JSONObject service = Util.findObjectByID(services, flowServices.get(i).getId());			
			System.out.println(flowServices.get(i).getServiceActionName() + " " + flowServices.get(i).getDesignPattern());
		}
//    	assertTrue(token.length()>0);
    }
	
    @Test
    public void testGetFlowServices() throws Exception {
    	init();
		String serviceId="";
		List<ServiceItem> flowServices = Service.getFlowServiceItems(serviceId, services, types);

		for (int i=0; i<flowServices.size(); i++)
		{
			JSONObject service = Util.findObjectByID(services, flowServices.get(i).getId());
			
			Service.ServicePattern designPattern = Service.getServicePattern(service, flowServices.get(i).getActionName());
			System.out.println(service.getString("developerName") + " " + flowServices.get(i).getActionName() + " " + flowServices.get(i).getDesignPattern() + " " + flowServices.get(i).getId() + " " + flowServices.get(i).getTypeId());
			
		}
//    	assertTrue(token.length()>0);
    }
    
    
    
    @Test
    public void testGenerateChartMacro() throws JSONException, Exception
    {
    	init();
    	JSONObject type = Util.findObjectByID(types, "2b8903af-a8b0-4ae6-97ae-74c832a5e9cb");
    	JSONObject listValue = pageLayout.createValueFromType(type, true);
    	JSONObject listEntry = pageLayout.createValueFromType(type, false);
    	List<Field> fields=new ArrayList<Field>();
    	for (Object propObj:type.getJSONArray("properties"))
    	{
    		JSONObject property = (JSONObject)propObj;
    		if (property.getString("contentType").contentEquals("ContentNumber"))
    		{
        		Field field = new Field();
        		field.setName(property.getString("developerName"));
        		field.setContentType(property.getString("contentType"));
    			fields.add(field);
    		}
    	}
//    	pageLayout.generateChartMacro(fields, listEntry.getString("developerName"), listValue.getString("developerName"), "id");
    	System.out.println(pageLayout.getNewChartMacro().toString(2));
//    	System.out.println(pageLayout.getNewChartType().toString(2));
    	System.out.println(pageLayout.getNewChartOperations().getJSONObject(0).toString(2));
    	for (Object valueObj:pageLayout.getNewChartValues())
    	{
    		JSONObject chartValue = (JSONObject) valueObj;
    		System.out.println(chartValue.toString(2));
    	}
    }
    
    @Test
    public void testCreateChartPage() throws JSONException, Exception
    {
    	init();
    	String serviceId = "788decf1-1a3a-40ef-935a-f31853a2107c";
    	JSONObject service = Util.findObjectByID(services, serviceId);
    	String actionName = "RealTime";
		JSONObject action = Service.findAction(service, actionName);

       	JSONObject typeOut = Service.findTypeForService(types, serviceId, action.getJSONArray("serviceOutputs") );
    	JSONObject valueOut = pageLayout.createValueFromType(typeOut, true);
    	JSONObject messageComponent=new JSONObject();
    	String xAxisId = findIdByName(typeOut.getJSONArray("properties"),"TimeOfDay");
    	JSONObject page = pageLayout.createPageFromType(typeOut, valueOut, OutputPageOption.CHARTLIST, "XXXX", xAxisId, null, null);
    	System.out.println(page.toString(2));
    }
    
    @Test
    public void testInsertSingletonMessageActions() throws JSONException, Exception
    {
    	init();
    	JSONObject service = Util.findObjectByID(services, "d5f7cd6f-1773-4c4e-88b3-9cd11c5953ac");
    	String actionName = "Recognize Plate from Camera";
		JSONObject action = Service.findAction(service, actionName);

    	JSONObject typeIn = Service.createSingletonTypeFromService(types, "inputType",  action.getJSONArray("serviceInputs"));
       	JSONObject typeOut = Service.createSingletonTypeFromService(types, "outPutType",  action.getJSONArray("serviceInputs"));
       	JSONObject valueIn = pageLayout.createValueFromType(typeIn, false);
    	JSONObject valueOut = pageLayout.createValueFromType(typeOut, false);
    	JSONObject messageComponent=new JSONObject();
    	Service.insertMessageActions(messageComponent, service, typeIn, valueIn, typeOut, valueOut, actionName);
    	System.out.println(messageComponent.toString(2));
    }

    @Test
    public void testInsertListMessageActions() throws JSONException, Exception
    {
    	init();
    	String serviceId = "788decf1-1a3a-40ef-935a-f31853a2107c";
    	JSONObject service = Util.findObjectByID(services, serviceId);
    	String actionName = "RealTime";
		JSONObject action = Service.findAction(service, actionName);

       	JSONObject typeOut = Service.findTypeForService(types, serviceId, action.getJSONArray("serviceOutputs"));
    	JSONObject valueOut = pageLayout.createValueFromType(typeOut, true);
    	JSONObject messageComponent=new JSONObject();
    	Service.insertMessageActions(messageComponent, service, null, null, typeOut, valueOut, actionName);
    	System.out.println(messageComponent.toString(2));
    }

    String findIdByName(JSONArray objects, String name)
	{
    	for (Object obj:objects)
    	{
    		JSONObject object = (JSONObject)obj;
    		if (object.getString("developerName").contentEquals(name))
    			return object.getString("id");
    	}
    	return null;
	}

}