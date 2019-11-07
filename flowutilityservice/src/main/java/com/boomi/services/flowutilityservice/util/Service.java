package com.boomi.services.flowutilityservice.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.boomi.services.flowutilityservice.getservices.ServiceDefinition;
import com.boomi.services.flowutilityservice.gettypefields.Field;

public class Service {
	
	//Note we only support simple lists and/or singleton objects. Nested objects or actions with more than just a single list are not supported
	public enum ServicePattern //design pattern options
	{
		CRUD, SINGLETONIN_SINGLETONOUT, SINGLETONIN_LISTOUT, LISTIN, LISTOUT, SINGLETONOUT,  SINGLETONIN, FILES, NOTSUPPORTED
	}

	//*******************************************************************************
	//Called by FlowDefinition
	//*******************************************************************************
	public static ServicePattern getServicePattern(JSONObject service, String actionName)
	{
		ServicePattern pattern=ServicePattern.NOTSUPPORTED;
		if (service.getBoolean("providesFiles"))
		{
			pattern = ServicePattern.FILES;
		}
		else if (actionName==null || actionName.length()==0)
		{
			pattern = ServicePattern.CRUD;
		} else {
			JSONObject action = findAction(service, actionName);
			if (action!=null)
			{
				JSONArray serviceInputs=null;
				JSONArray serviceOutputs=null;
				if (action.has("serviceInputs") && !action.isNull("serviceInputs") && action.getJSONArray("serviceInputs").length()>0)
					serviceInputs = action.getJSONArray("serviceInputs");
				if (action.has("serviceOutputs") && !action.isNull("serviceOutputs") && action.getJSONArray("serviceOutputs").length()>0)
					serviceOutputs = action.getJSONArray("serviceOutputs");
				
				boolean hasInputList = hasList(serviceInputs);
				boolean hasInputSingletons = hasSingletons(serviceInputs);
				boolean hasOutputList = hasList(serviceOutputs);
				boolean hasOutputSingletons = hasSingletons(serviceOutputs);
				
				if (hasInputSingletons && hasOutputSingletons)
					pattern=ServicePattern.SINGLETONIN_SINGLETONOUT;	
				else if (hasInputSingletons && hasOutputList)
					pattern=ServicePattern.SINGLETONIN_LISTOUT;
				else if (hasInputList)
					pattern=ServicePattern.LISTIN;
				else if (hasOutputList)
					pattern=ServicePattern.LISTOUT;
				else if (hasInputSingletons)
					pattern=ServicePattern.SINGLETONIN;
				else if (hasOutputSingletons)
					pattern=ServicePattern.SINGLETONOUT;
			}
//		} else if (service.getBoolean("providesFiles")) {
//			pattern=DesignPattern.FILES;
		}	
		return pattern;
	}
	
	//*******************************************************************************
	//Called by GetServiceCommand for UI
	//*******************************************************************************
	public static List<ServiceDefinition> getFlowServices (JSONArray services, JSONArray types)
	{
		List<ServiceDefinition> flowServices = new ArrayList<ServiceDefinition>();
	    for (Object serviceObj:services)
	    {
	    	JSONObject service = (JSONObject)serviceObj;
	    	JSONArray actions = null;
	    	if (service.has("actions") && !service.isNull("actions"))
	    		actions=service.getJSONArray("actions");
	    	String serviceName = service.getString("developerName");
	    	String serviceId = service.getString("id");
	    	
	    	if (actions !=null && actions.length()>0)
	    	{
		    	for (Object actionObj:service.getJSONArray("actions"))
		    	{
		    		
		    		JSONObject action = (JSONObject)actionObj;
		    		String actionName = action.getString("developerName");
		    		String serviceActionName = serviceName + "(Message) - " + actionName;
		    		ServicePattern designPattern = getServicePattern(service, actionName);
			    	String description = getPatternDescription(designPattern);
		    	
			    	flowServices.add(new ServiceDefinition(serviceId
			    			, serviceName
			    			, UUID.randomUUID().toString()
			    			, actionName, serviceActionName, designPattern.name(), "", "", description));
		    	}
	    	} 
	    	if (service.getBoolean("providesDatabase")){
	    		//Parse all types
	    		for (Object typeObj:types)
	    		{
	    			JSONObject type = (JSONObject)typeObj;
	    			if (!type.isNull("serviceElementId") && type.getString("serviceElementId").contentEquals(serviceId))
	    			{
			    		String typeName = type.getString("developerName");
			    		String serviceTypeName = serviceName + "(Database) - " + typeName;
			    		ServicePattern designPattern = getServicePattern(service, "");
				    	String description = getPatternDescription(designPattern);
				    	flowServices.add(new ServiceDefinition(serviceId
				    			, service.getString("developerName")
				    			, UUID.randomUUID().toString()
				    			, "", serviceTypeName, designPattern.name(), typeName, type.getString("id"), description));
	    			}
	    		}
	    	}
	    	if (service.getBoolean("providesFiles")){
	    		//Parse all types
	    		String typeName = service.getString("developerName");
	    		String serviceTypeName = serviceName + "(File Upload) - " + typeName;
	    		ServicePattern designPattern = ServicePattern.FILES;
		    	String description = getPatternDescription(designPattern);
		    	flowServices.add(new ServiceDefinition(serviceId
		    			, service.getString("developerName")
		    			, UUID.randomUUID().toString()
		    			, "", serviceTypeName, designPattern.name(), "", "", description));
	    	}
	    }		
	    return flowServices;
	}
	
	public static List<Field> getTypeFields(JSONObject type)
	{
		List<Field> fields = new ArrayList<Field>();
		if (type!=null)
			for (Object propertyObj: type.getJSONArray("properties"))
			{
				JSONObject property = (JSONObject) propertyObj;
				Field field = new Field(property.getString("id"), property.getString("developerName"), UUID.randomUUID().toString());
				field.setContentType(property.getString("contentType"));
				fields.add(field);
				System.out.println(field.getName());
			}
		return fields;
	}
	
	//*******************************************************************************
	//Return Fields to GetFieldsCommand for UI display and chart field selection
	//*******************************************************************************
	public static List<Field> getOutputTypeFields(Tenant tenant, String typeId, String serviceId, String actionName) throws IOException {
//		JSONArray services = FlowAPIClient.getServices(token, tenantId);
		JSONArray types = tenant.getTypes();
		JSONObject service = Util.findObjectByID(tenant.getServices(), serviceId);
		
		JSONObject type=null;
		ServicePattern designPattern = getServicePattern(service, actionName);
		JSONObject action = findAction(service, actionName);

		switch (designPattern)
		{
		case CRUD:
			type = Util.findObjectByID(types, typeId);
			break;
		case SINGLETONIN_SINGLETONOUT:
			type = createSingletonTypeFromService(types, "TYPE", action.getJSONArray("serviceOutputs"));
			break;
		case SINGLETONIN_LISTOUT:
			type = findTypeForService(types, serviceId, action.getJSONArray("serviceOutputs"));
			break;
		case LISTIN:
			type = findTypeForService(types, serviceId, action.getJSONArray("serviceInputs"));
			break; 
		case LISTOUT:
			type = findTypeForService(types, serviceId, action.getJSONArray("serviceOutputs"));
			break;
		case SINGLETONOUT: 
			type = createSingletonTypeFromService(types, "TYPE", action.getJSONArray("serviceOutputs"));
			break;
		case FILES: 
			return new ArrayList<Field>();
//TODO return something			break;
		default:
			break;
		}

		System.out.println(designPattern.name());
		return getTypeFields(type);
	}

	//*******************************************************************************
	//Called by FlowDefinition
	//*******************************************************************************
	public static JSONObject createSingletonTypeFromService(JSONArray types, String typeName, JSONArray fields)
	{
		JSONObject type = new JSONObject();
		
		type.put("developerName", Util.findUniqueName(typeName, types));
		Util.putDeveloperSummary(type);
		type.put("elementType", "TYPE");
		type.put("id", UUID.randomUUID().toString());
		
		JSONArray properties = new JSONArray();
		type.put("properties", properties);
		for (Object fieldObj:fields)
		{
			JSONObject field = (JSONObject)fieldObj;
			JSONObject property = new JSONObject();
			property.put("contentFormat", "");
			property.put("contentType", field.getString("contentType"));
			property.put("developerName", field.getString("developerName"));
			property.put("id", UUID.randomUUID().toString());
//			field.put("typeElementDeveloperName", null);
//			field.put("typeElementId", null);
			properties.put(property);
		}
		return type;
	}

	//*******************************************************************************
	//*******************************************************************************
	//*******************************************************************************
	// privates
	//*******************************************************************************
	//*******************************************************************************
	//*******************************************************************************

	private static String getPatternDescription(ServicePattern pattern)
	{
		String description=null;
		
		switch (pattern)
		{
		case CRUD:
			description="Create/Query/Update/Delete (CRUD) form and list. This pattern includes support for a URI parameter for opening the flow to a specific database record. This is useful for usecases such as embedding URLs into email notifications where the user can click to open the flow.";
			break;
		case FILES:
			description="Create File Upload Flow.";
			break;
		case LISTIN:
			description="Input a list to the service.";
			break;
		case LISTOUT:
			description="Retrieve a list from the service.";
			break;
		case NOTSUPPORTED:
			description="";
			break;
		case SINGLETONIN:
			description="Input simple fields to the service.";
			break;
		case SINGLETONIN_LISTOUT:
			description="Input simple fields and retrieve a list from the service.";
			break;
		case SINGLETONIN_SINGLETONOUT:
			description="Input simple fields and retrieve simple fields from the service.";
			break;
		case SINGLETONOUT:
			description="Retrieve simple fields from the service";
			break;
		default:
			description="";
			break;
		}
		
		return description;
	}

	//TODO private Called only by TEST
	public static void insertMessageActions(JSONObject messageComponent, JSONObject service, JSONObject typeIn, JSONObject valueIn, JSONObject typeOut, JSONObject valueOut, String actionName)
	{
		JSONObject action = findAction(service, actionName);
		
		JSONArray messageActions = new JSONArray();
		messageComponent.put("messageActions", messageActions);
		
		JSONObject messageAction = new JSONObject();
		messageActions.put(messageAction);

		messageAction.put("uriPart", action.getString("uriPart"));
		messageAction.put("developerName", actionName);
		messageAction.put("id", UUID.randomUUID().toString());
		messageAction.put("serviceElementId", service.getString("id"));
		if (valueIn != null)
		{
			messageAction.put("inputs", getActionProperties(typeIn, valueIn, action.getJSONArray("serviceInputs"), "valueElementToReferenceId"));
		}
		if (valueOut != null)
		{
			messageAction.put("outputs", getActionProperties(typeOut, valueOut, action.getJSONArray("serviceOutputs"), "valueElementToApplyId"));
		}
	}	
	
	private static JSONArray getActionProperties(JSONObject type, JSONObject value, JSONArray serviceParameters, String valueElementName)
	{
		JSONArray actionProperties = new JSONArray();
		if (hasList(serviceParameters))
		{
			JSONObject newProperty = new JSONObject();
			actionProperties.put(newProperty);
			newProperty.put("contentType", value.getString("contentType"));
			newProperty.put("developerName", serviceParameters.getJSONObject(0).getString("developerName"));
			newProperty.put("id", UUID.randomUUID().toString());
			
			JSONObject valueElement = new JSONObject();
			newProperty.put(valueElementName, valueElement);
			valueElement.put("command", "");
			valueElement.put("id", value.getString("id"));
//			valueElement.put("typeElementPropertyId", property.getString("id"));
		} else {
			for (Object objProperty:type.getJSONArray("properties"))
			{
				JSONObject property = (JSONObject)objProperty;
				JSONObject newProperty = new JSONObject();
				actionProperties.put(newProperty);
				newProperty.put("contentType", property.getString("contentType"));
				newProperty.put("developerName", property.getString("developerName"));
				newProperty.put("id", UUID.randomUUID().toString());
				
				JSONObject valueElement = new JSONObject();
				newProperty.put(valueElementName, valueElement);
				valueElement.put("command", "");
				valueElement.put("id", value.getString("id"));
				valueElement.put("typeElementPropertyId", property.getString("id"));
			}		
		}
		return actionProperties;
	}

	//TODO private Called only by TEST
	public static JSONObject findAction(JSONObject service, String actionName)
	{
		if (actionName != null && !service.isNull("actions"))
		{
//			System.out.println(service.toString(2));
			JSONArray actions = service.getJSONArray("actions");
			for (int i=0; i < actions.length(); i++)
			{
				JSONObject action = actions.getJSONObject(i);
				if (action.getString("developerName").contentEquals(actionName))
					return action;
			}		
		}
		return null;
	}
	
	//TODO private Called only by TEST
	public static JSONObject findTypeForService(JSONArray types, String serviceId, JSONArray actionFields)
	{
		//Find the type for the service
		
		for (int i=0; i<types.length(); i++)
		{
			JSONObject type = types.getJSONObject(i);
			if (!type.isNull("serviceElementId") && type.getString("serviceElementId").contentEquals(serviceId))
			{
				return types.getJSONObject(i);
			}
		}
		return null;
	}

	private static boolean hasList(JSONArray serviceProperties)
	{
		if (serviceProperties==null)
			return false;
		return serviceProperties.length()==1 && "ContentList".contentEquals(serviceProperties.getJSONObject(0).getString("contentType"));
	}

	private static boolean hasSingletons(JSONArray serviceProperties)
	{
		if (serviceProperties==null)
			return false;
		for (int i=0; i < serviceProperties.length(); i++)
		{
			String contentType = serviceProperties.getJSONObject(i).getString("contentType");
			if ("ContentList".contentEquals(contentType) || "ContentObject".contentEquals(contentType))
			{
				return false;
			}
		}
		return true;
	}
}
