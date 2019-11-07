package com.boomi.services.flowutilityservice.util;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.boomi.services.flowutilityservice.gettypefields.Field;
import com.boomi.services.flowutilityservice.util.PageLayout.OutputPageOption;
import com.boomi.services.flowutilityservice.util.Service.ServicePattern;

public class FlowDefinition {
	private Tenant tenant;
	private JSONObject service;
	
	private JSONObject newFlow;
	private JSONArray newMap;	
	private String newTableComponentId;
	
	private String serviceId;
	private String actionName;
	private String typeId;
	private PageLayout.OutputPageOption option;
	private String optionIn;
	private String xAxis;
	private String existingFlowId;
	
	private Endpoint inputEndpoint;
	private Endpoint outputEndpoint;
	private List<Field> selectedFields;
	private String chartType;
	private List<Field> allFields;
	private PageLayout pageLayout;
	private JSONArray mapElements;
	private JSONObject rowIdValue;
	private int startShapeIndex;
	private String information="";
	private String fileUploadSchema = "";
			
	private class Endpoint
	{
		private JSONObject type;
		private JSONObject value;
		private JSONObject page;
		private boolean serviceType=false;
		
		public JSONObject getType() {
			return type;
		}
		public void setType(JSONObject type) {
			this.type = type;
		}
		public JSONObject getValue() {
			return value;
		}
		public void setValue(JSONObject value) {
			this.value = value;
		}
		public JSONObject getPage() {
			return page;
		}
		private void setPage(JSONObject page) {
			this.page = page;
		}
		private String getInformation()
		{
			return information;
		}
		public boolean isServiceType() {
			return serviceType;
		}
		public void setServiceType(boolean serviceType) {
			this.serviceType = serviceType;
		}
	}
	
	//typeId for database services
	//serviceId/actionName for message services
	public FlowDefinition(String token, String tenantId, String serviceId, String actionName, String typeId, String flowId, String optionIn, String xAxis, String chartType, List<Field> selectedFields, String fileUploadSchema) throws Exception
	{
		this.tenant = new Tenant(token, tenantId);
		this.serviceId = serviceId;
		this.actionName = actionName;
		this.typeId = typeId;
		this.existingFlowId = flowId;
		this.optionIn = optionIn;
		this.xAxis = xAxis;
		this.selectedFields = selectedFields;
		this.chartType = chartType;
		this.pageLayout = new PageLayout(tenant, serviceId);
		this.fileUploadSchema = fileUploadSchema;
		
		newTableComponentId = UUID.randomUUID().toString();
		Tenant tenant = new Tenant(token, tenantId);

		service = Util.findObjectByID(tenant.getServices(), serviceId);		
		if (service==null)
			throw new Exception("Service not found: " + serviceId);
	}
	
	//TODO For for singleton and multi series lists we need to createChartLabelValueType and createChartLabelValueValue 
	//TODO for singleton we also need to populate Action Setters/Ad List and createSimpleChartList
	//TODO for multi series charts we need to createChartLabelValueListFromField for each selected field and generateChartMacro and associate with the charts
	public String generateDesignPattern() throws JSONException, Exception
	{
		Service.ServicePattern designPattern = Service.getServicePattern(service, actionName);
		JSONObject type = null;
		
		JSONObject action = Service.findAction(service, actionName);
		
		String flowName = actionName;
		if (optionIn==null || optionIn.length()==0)
			option = PageLayout.OutputPageOption.LIST;
		else
			option = PageLayout.OutputPageOption.valueOf(optionIn);
		
		option = PageLayout.validateOption(designPattern, option);

		if (designPattern == ServicePattern.CRUD)
		{
			type = Util.findObjectByID(tenant.getTypes(), typeId);
			flowName = type.getString("developerName");
		} else if (designPattern == ServicePattern.FILES)
		{
			type = Util.fileUploadTypeFromXML(fileUploadSchema);
			flowName = type.getString("developerName");
		} else {
			flowName = service.getString("developerName") + "-" + actionName;
		}
		if (this.useExistingFlow())
			newFlow = findFlowByID(tenant.getFlows(), existingFlowId);
		else
			newFlow = createFlow(tenant.getFlows(), flowName);		

		switch (designPattern)
		{
		case CRUD: //aka Database
			if (option==PageLayout.OutputPageOption.CHARTLIST)
			{
				generateCRUDChart(type, option);
				this.createFlowMap_CRUDChart(type, selectedFields);
			} else {
				generateCRUD(type, option);
				this.createFlowMap_CRUD(newFlow, type);
			}
			break;
		case SINGLETONIN_SINGLETONOUT:
			inputEndpoint = generateSingleton(PageLayout.OutputPageOption.FORM, actionName +" In", action.getJSONArray("serviceInputs"), null);
			outputEndpoint = generateSingleton(option, actionName +" Out", action.getJSONArray("serviceOutputs"), this.selectedFields);
			this.createFlowMap_INOUT(newFlow);
			//Read Only, Form, or Presentation
			break;
		case SINGLETONIN_LISTOUT:
			inputEndpoint = generateSingleton(PageLayout.OutputPageOption.FORM, actionName +" In", action.getJSONArray("serviceInputs"), null);
			type = Service.findTypeForService(tenant.getTypes(), serviceId, action.getJSONArray("serviceOutputs"));
			outputEndpoint = generateList(type, option);
			this.createFlowMap_INOUT(newFlow);
			break;
		case LISTIN:
			type = Service.findTypeForService(tenant.getTypes(), serviceId, action.getJSONArray("serviceInputs"));
			inputEndpoint = generateList(type, option);
			this.createFlowMap_IN(newFlow);
			break; 
		case LISTOUT:
			type = Service.findTypeForService(tenant.getTypes(), serviceId, action.getJSONArray("serviceOutputs"));
			outputEndpoint = generateList(type, option);
			this.createFlowMap_OUT(newFlow);
			//Chart Option
			break;
		case SINGLETONOUT: 
			outputEndpoint = generateSingleton(option, actionName +" Out", action.getJSONArray("serviceOutputs"), selectedFields);
			this.createFlowMap_OUT(newFlow);
			//Read Only, Form, or Presentation
			break;
		case SINGLETONIN: 
			inputEndpoint = generateSingleton(PageLayout.OutputPageOption.FORM, actionName +" In", action.getJSONArray("serviceInputs"), null);
			this.createFlowMap_IN(newFlow);
			break;
		case FILES:
			generateFileUpload(option, type);
			this.createFlowMap_FileUploadINOUT(newFlow);
			//TODO
			break;
		case NOTSUPPORTED:
			break;
		}		

//		flowURL = String.format("<a target='_new' href='https://flow.boomi.com/%s'>Click here to view and edit your flow</a><br/>", tenantId);
//		information += flowURL;

		this.saveFlow();
		
		//flow id is generated when we save the flow so build url after save
		String flowURL = String.format("<a target='_new' href='https://flow.boomi.com/%s/play/default/?flow-id=%s'>Click here to run your new flow</a><br/>"
				, tenant.getTenantId(), newFlow.getJSONObject("id").getString("id"));
		information += flowURL;
		return information;
	}
	
	private void saveFlow() throws JSONException, IOException
	{
		String editingToken;
		if (!this.useExistingFlow())
		{
			this.newFlow = new JSONObject(FlowAPIClient.doPost("/api/draw/1/flow", tenant.getToken(), tenant.getTenantId(), newFlow.toString()));
			information += "<b>Flow:</b> " + newFlow.getString("developerName") + "<br/>";
		} else {
			information += "Elements added to Flow:"+newFlow.getString("developerName") + "<br/>";
		}

		if (!newFlow.isNull("editingToken"))
			editingToken = newFlow.getString("editingToken");
		else
			editingToken = UUID.randomUUID().toString();
		String flowId = newFlow.getJSONObject("id").getString("id");
		if (pageLayout.getNewChartType()!=null)
		{
			FlowAPIClient.doPost("/api/draw/1/element/type", tenant.getToken(), tenant.getTenantId(), pageLayout.getNewChartType().toString());
			createAndShareElement(flowId, "Type", pageLayout.getNewChartType());
		}

		for (Object valueObj : pageLayout.getNewChartValues())
		{
			JSONObject value = (JSONObject) valueObj;
			FlowAPIClient.doPost("/api/draw/1/element/value", tenant.getToken(), tenant.getTenantId(), value.toString());
			createAndShareElement(flowId, "Value", value);
		}

		if (inputEndpoint!=null)
		{
			if (inputEndpoint.getType()!=null && !inputEndpoint.isServiceType())
				createAndShareElement(flowId, "Type", inputEndpoint.getType());
			if (inputEndpoint.getValue()!=null)
				createAndShareElement(flowId, "Value", inputEndpoint.getValue());
			if (inputEndpoint.getPage()!=null)
				createAndShareElement(flowId, "Page", inputEndpoint.getPage());
		}
		
		if (outputEndpoint!=null)
		{
			if (outputEndpoint.getType()!=null && !outputEndpoint.isServiceType())
				createAndShareElement(flowId, "Type", outputEndpoint.getType());
			if (outputEndpoint.getValue()!=null)
				createAndShareElement(flowId, "Value", outputEndpoint.getValue());
			if (outputEndpoint.getPage()!=null)
				createAndShareElement(flowId, "Page", outputEndpoint.getPage());
		}
		if (this.rowIdValue!=null)
			createAndShareElement(flowId, "Value", rowIdValue);
		
		if (pageLayout.getNewChartMacro()!=null)
			createAndShareElement(flowId, "Macro", pageLayout.getNewChartMacro());
		
		if (this.useExistingFlow())
		{
			newMap.remove(startShapeIndex);
		} else {
			//start shape index is generate when we save the flow
			JSONObject startShape = newMap.getJSONObject(this.startShapeIndex);
			startShape.put("id", newFlow.getString("startMapElementId"));	

			JSONObject navigation = new JSONObject();
			navigation.put("developerName", "Home Navigation");
			navigation.put("label", "Created with Flow Utilities");
			navigation.put("elementType", "NAVIGATION");
			navigation.put("id", UUID.randomUUID().toString());
			
			JSONArray navigationItems = new JSONArray();
			navigation.put("navigationItems", navigationItems);
			JSONObject navigationItem = new JSONObject();
			navigationItems.put(navigationItem);
			navigationItem.put("id", UUID.randomUUID().toString());
			navigationItem.put("label", "Start");
			navigationItem.put("locationMapElementId", newFlow.getString("startMapElementId"));
			navigationItem.put("order", 1);
			navigationItem.put("developerName", "Start");
			Util.putDeveloperSummary(navigation);
			String uri = String.format("/api/draw/1/flow/%s/%s/element/navigation", newFlow.getJSONObject("id").getString("id"), editingToken);
			FlowAPIClient.doPost(uri, tenant.getToken(), tenant.getTenantId(), navigation.toString());
		}
		
		String uri = String.format("/api/draw/1/flow/%s/%s/element/map", newFlow.getJSONObject("id").getString("id"), editingToken);
		for (Object obj:newMap)
		{
			JSONObject map = (JSONObject)obj;
			System.out.println("Map:"+map.toString().length());
			information += "<b> Map Element " + map.getString("elementType") + "</b>: " + map.getString("developerName") + "<br/>";
			String res=FlowAPIClient.doPost(uri, tenant.getToken(), tenant.getTenantId(), map.toString());
		}

		uri = String.format("/api/draw/1/flow/snap/%s", newFlow.getJSONObject("id").getString("id"));
		JSONObject snap = new JSONObject(FlowAPIClient.doPost(uri, tenant.getToken(), tenant.getTenantId(), ""));
		uri = String.format("/api/draw/1/flow/activation/%s/%s/true/true", snap.getJSONObject("id").getString("id"), snap.getJSONObject("id").getString("versionId"));
		FlowAPIClient.doPost(uri, tenant.getToken(), tenant.getTenantId(), "");
	}
	
	private void createAndShareElement(String flowId, String type, JSONObject object) throws JSONException, IOException
	{
	    Util.putDeveloperSummary(object);
	    information += "<b>" + type + "</b>: " + object.getString("developerName") + "<br/>";
	    type = type.toLowerCase();
	    //Create the element
	    FlowAPIClient.doPost(String.format("/api/draw/1/element/%s", type), tenant.getToken(), tenant.getTenantId(), object.toString());
		//Add reference from flow
		FlowAPIClient.doPost(String.format("/api/draw/1/element/flow/%s/%s/%s", flowId, type, object.getString("id")), tenant.getToken(), tenant.getTenantId(),"");
	}
	
	private boolean useExistingFlow() {
		return 	(this.existingFlowId !=null && this.existingFlowId.length()>0);
	}
	
	private void generateFileUpload(OutputPageOption option, JSONObject type)
	{
		inputEndpoint = new Endpoint();
		outputEndpoint = new Endpoint();
		selectedFields = Service.getTypeFields(type);
		inputEndpoint.setType(type);
		inputEndpoint.setValue(pageLayout.createValueFromType(type, false));
		inputEndpoint.setPage(pageLayout.createPageFromType(type, inputEndpoint.getValue(), OutputPageOption.FILEUPLOAD, null, "", "", selectedFields));
		outputEndpoint.setPage(pageLayout.createPageFromType(type, inputEndpoint.getValue(), OutputPageOption.HTML, newTableComponentId, "", "", selectedFields));
	}

	private Endpoint generateSingleton(OutputPageOption option, String valueName, JSONArray properties, List<Field> selectedFields) throws IOException
	{
		Endpoint endPoint = new Endpoint();
		
		endPoint.setType(Service.createSingletonTypeFromService(tenant.getTypes(), valueName, properties));
		if (selectedFields==null)
			selectedFields = Service.getTypeFields(endPoint.getType());
		endPoint.setValue(this.pageLayout.createValueFromType(endPoint.getType(), false));
		endPoint.setPage(this.pageLayout.createPageFromType(endPoint.getType(), endPoint.getValue(), option, null, xAxis, chartType, selectedFields));
		
		return endPoint;
	}
	
	private Endpoint generateList(JSONObject type, PageLayout.OutputPageOption option) throws IOException
	{
		Endpoint endPoint = new Endpoint();
		endPoint.setType(type);
		endPoint.setServiceType(true);
		endPoint.setValue(this.pageLayout.createValueFromType(type, true));
		endPoint.setPage(this.pageLayout.createPageFromType(type, endPoint.getValue(), option, newTableComponentId, xAxis, chartType, selectedFields));

		return endPoint;
	}
	
	//Output only database (for now only for db charts)
	private void generateSimpleDatabase(JSONObject type, OutputPageOption option) throws IOException
	{
		outputEndpoint = new Endpoint();
		outputEndpoint.setPage(this.pageLayout.createPageFromType(type, inputEndpoint.getValue(), option, newTableComponentId, xAxis, chartType, selectedFields));
	}

	private void generateCRUDChart(JSONObject type, OutputPageOption option) throws IOException
	{
		inputEndpoint = new Endpoint();
		outputEndpoint = new Endpoint();
		inputEndpoint.setValue(pageLayout.createValueFromType(type, true));
		outputEndpoint.setPage(pageLayout.createPageFromType(type, inputEndpoint.getValue(), option, newTableComponentId, xAxis, chartType, selectedFields));
	}
	
	private void generateCRUD(JSONObject type, OutputPageOption option) throws IOException
	{
		inputEndpoint = new Endpoint();
		outputEndpoint = new Endpoint();
		
		//create rowId Value
		JSONObject rowIdValue=new JSONObject();
		this.rowIdValue = rowIdValue;
		rowIdValue.put("access", "INPUT_OUTPUT");
		rowIdValue.put("contentType", "ContentString");
		rowIdValue.put("developerName", "rowID");
		rowIdValue.put("elementType", "VARIABLE");
		rowIdValue.put("id", "ea264eb1-97dc-4497-9fef-5232fcf37678");
		
//		tableComponentId = "c34148b5-3b9c-4115-9b01-fb6d406b5536"; //pageObjectBindingId used to bind buttons in steps
//		formPageId = "b59be5d7-b46b-440a-9852-dca909eeec69";
//		listPageId = "8cbf4a4a-92b0-4946-9e43-d89e2085a641";
//		valueId = "b01c13eb-1393-4e98-b810-f63f90155c4d";
	
		inputEndpoint.setValue(pageLayout.createValueFromType(type, false));
		inputEndpoint.setPage(pageLayout.createPageFromType(type, inputEndpoint.getValue(), PageLayout.OutputPageOption.FORM, null, xAxis, chartType, selectedFields));
		outputEndpoint.setPage(pageLayout.createPageFromType(type, inputEndpoint.getValue(), option, newTableComponentId, xAxis, chartType, selectedFields));
	}
	
	private JSONObject createFlow(JSONArray flows, String name) throws Exception
	{
		name = Util.findUniqueName(name + " " + option.name(), flows);
		JSONObject newFlow = new JSONObject();
		JSONObject authorization = new JSONObject();
		newFlow.put("authorization", authorization);
		if (service.getBoolean("providesIdentity"))
		{
	        authorization.put("serviceElementId", serviceId);
	        authorization.put("globalAuthenticationType", "ALL_USERS");
		} else {
			authorization.put("globalAuthenticationType", "PUBLIC");
		}
		authorization.put("streamBehaviourType", "NONE");
		
		newFlow.put("developerName", name);
		newFlow.put("startMapElementId", UUID.randomUUID().toString());
		newFlow.put("stateExpirationLength", 0);	
		newFlow.put("editingToken", UUID.randomUUID().toString());
		
		JSONObject flowId = new JSONObject();
		newFlow.put("id", flowId);
		Util.putDeveloperSummary(newFlow);
//		flowId.put("id", UUID.randomUUID().toString());
		
		return newFlow;
	}
	
	private void createFlowMap_CRUDChart(JSONObject type, List<Field> selectedFields) throws Exception
	{
		String flowTemplateName;

		String keyPropertyId = "";
		if (selectedFields.size()==1)
			flowTemplateName = "resources/FlowTemplateDBChartSimple.json";
		else
			flowTemplateName = "resources/FlowTemplateDBChartMacro.json";
flowTemplateName = "resources/FlowTemplateDBChartMacro.json";
		String flowTemplate = Util.readResource(flowTemplateName, this.getClass());
		//TODO allow the user to choose the key or figure it out automatically?
		keyPropertyId = type.getJSONArray("properties").getJSONObject(0).getString("id");
		flowTemplate=flowTemplate.replace("{{keyPropertyId}}", keyPropertyId);
		flowTemplate=flowTemplate.replace("{{typeId}}", typeId);
		flowTemplate=flowTemplate.replace("{{typeName}}", type.getString("developerName"));
		flowTemplate=flowTemplate.replace("{{valueId}}", inputEndpoint.getValue().getString("id"));
		flowTemplate=flowTemplate.replace("{{typeBindingId}}", type.getJSONArray("bindings").getJSONObject(0).getString("id"));
		flowTemplate=flowTemplate.replace("{{outPageId}}", outputEndpoint.getPage().getString("id"));
		flowTemplate=flowTemplate.replace("{{tableId}}", newTableComponentId);
		
		newMap = new JSONArray(flowTemplate);
		createFlowMap(newMap, newFlow);
	}

	private void createFlowMap_CRUD(JSONObject newFlow, JSONObject type) throws Exception
	{
		String flowTemplate = Util.readResource("resources/FlowTemplateCRUD.json", this.getClass());
		String keyPropertyId = "";
		
		//TODO allow the user to choose the key or figure it out automatically?
		keyPropertyId = type.getJSONArray("properties").getJSONObject(0).getString("id");
		flowTemplate=flowTemplate.replace("{{keyPropertyId}}", keyPropertyId);
		flowTemplate=flowTemplate.replace("{{typeId}}", typeId);
		flowTemplate=flowTemplate.replace("{{typeName}}", type.getString("developerName"));
		flowTemplate=flowTemplate.replace("{{valueId}}", inputEndpoint.getValue().getString("id"));
		flowTemplate=flowTemplate.replace("{{typeBindingId}}", type.getJSONArray("bindings").getJSONObject(0).getString("id"));
		flowTemplate=flowTemplate.replace("{{inPageId}}", inputEndpoint.getPage().getString("id"));
		flowTemplate=flowTemplate.replace("{{outPageId}}", outputEndpoint.getPage().getString("id"));
		flowTemplate=flowTemplate.replace("{{tableId}}", newTableComponentId);
		
		newMap = new JSONArray(flowTemplate);
		createFlowMap(newMap, newFlow);
	}
	
	private void createFlowMap_IN(JSONObject newFlow) throws Exception
	{
		String flowTemplate = Util.readResource("resources/FlowTemplateIN.json", this.getClass());
		flowTemplate=flowTemplate.replace("{{inPageId}}", inputEndpoint.getPage().getString("id"));
		flowTemplate=flowTemplate.replace("{{inTypeName}}", inputEndpoint.getType().getString("developerName"));
		flowTemplate=flowTemplate.replace("{{actionName}}", this.actionName);
		
		newMap = new JSONArray(flowTemplate);
		createFlowMap(newMap, newFlow);
	}
	
	//No service in the middle, just a inoutform
	private void createFlowMap_FileUploadINOUT(JSONObject newFlow) throws Exception
	{
		String flowTemplateName;
		flowTemplateName = "resources/FlowTemplateINOUTSimple.json";
		
		String flowTemplate = Util.readResource(flowTemplateName, this.getClass());
		flowTemplate=flowTemplate.replace("{{inPageId}}", inputEndpoint.getPage().getString("id"));
		flowTemplate=flowTemplate.replace("{{inTypeName}}", inputEndpoint.getType().getString("developerName"));
//		flowTemplate=flowTemplate.replace("{{actionName}}", this.actionName);
		flowTemplate=flowTemplate.replace("{{outPageId}}", outputEndpoint.getPage().getString("id"));
		flowTemplate=flowTemplate.replace("{{outTypeName}}", inputEndpoint.getType().getString("developerName"));
		
		newMap = new JSONArray(flowTemplate);
		createFlowMap(newMap, newFlow);
	}


	private void createFlowMap_INOUT(JSONObject newFlow) throws Exception
	{
		String flowTemplateName;
		if (pageLayout.getNewChartOperations()!=null && pageLayout.getNewChartOperations().length()>0 )
			flowTemplateName = "resources/FlowTemplateINOUTOperator.json"; 
		else
			flowTemplateName = "resources/FlowTemplateINOUT.json"; //InOut with Message
		
		String flowTemplate = Util.readResource(flowTemplateName, this.getClass());
		flowTemplate=flowTemplate.replace("{{inPageId}}", inputEndpoint.getPage().getString("id"));
		flowTemplate=flowTemplate.replace("{{inTypeName}}", inputEndpoint.getType().getString("developerName"));
		flowTemplate=flowTemplate.replace("{{actionName}}", this.actionName);
		flowTemplate=flowTemplate.replace("{{outPageId}}", outputEndpoint.getPage().getString("id"));
		flowTemplate=flowTemplate.replace("{{outTypeName}}", outputEndpoint.getType().getString("developerName"));
		
		newMap = new JSONArray(flowTemplate);
		createFlowMap(newMap, newFlow);
	}

	private void createFlowMap_OUT(JSONObject newFlow) throws Exception
	{
		String flowTemplateName;
		if (pageLayout.getNewChartOperations()!=null && pageLayout.getNewChartOperations().length()>0 )
			flowTemplateName = "resources/FlowTemplateOUTOperator.json";
		else
			flowTemplateName = "resources/FlowTemplateOUT.json";

		String flowTemplate = Util.readResource(flowTemplateName, this.getClass());
		flowTemplate=flowTemplate.replace("{{actionName}}", this.actionName);
		flowTemplate=flowTemplate.replace("{{outPageId}}", outputEndpoint.getPage().getString("id"));
		flowTemplate=flowTemplate.replace("{{outTypeName}}", outputEndpoint.getType().getString("developerName"));
		newMap = new JSONArray(flowTemplate);
		createFlowMap(newMap, newFlow);
	}

	private void createFlowMap(JSONArray newMap, JSONObject newFlow) throws IOException
	{		
		Random random = new Random();
	    int offset = 0;
	    startShapeIndex=0;
	    
		if (this.useExistingFlow())
			offset = random.nextInt(100);
		
	    int index=0;
		for (Object obj:newMap)
		{
			JSONObject mapElement = (JSONObject)obj;
			if (mapElement.getString("elementType").contentEquals("START"))
			{				
				startShapeIndex=index;
			}
			else
			{
				String existingId = mapElement.getString("id");
				String newId = UUID.randomUUID().toString();
				replaceAllOutcomeReferences(newMap, existingId, newId);
				mapElement.put("id", newId);				
				mapElement.put("x", mapElement.getInt("x")+ offset);
				mapElement.put("y", mapElement.getInt("y")+ offset);
			}
			if (mapElement.getString("elementType").contentEquals("message"))
			{
				JSONObject valueIn=null;
				JSONObject typeIn=null;
				JSONObject valueOut=null;
				JSONObject typeOut=null;
				if (inputEndpoint!=null)
				{
					typeIn=inputEndpoint.getType();
					valueIn=inputEndpoint.getValue();
				}
				if (outputEndpoint!=null)
				{
					typeOut=outputEndpoint.getType();
					valueOut=outputEndpoint.getValue();
				}
				Service.insertMessageActions(mapElement, service, typeIn, valueIn, typeOut, valueOut, actionName);
			} else if (mapElement.getString("elementType").contentEquals("operator"))
			{  
				mapElement.put("operations", this.pageLayout.getNewChartOperations());
			}
			index++;
		}		
	}
	
	private static void replaceAllOutcomeReferences(JSONArray map, String existingId, String newId)
	{	
		for (Object obj:map)
		{
			JSONObject mapElement = (JSONObject)obj;
			if (!mapElement.isNull("outcomes"))
			{
				JSONArray outcomes = mapElement.getJSONArray("outcomes");
				for (Object outcomeObj: outcomes)
				{
					JSONObject outcome = (JSONObject)outcomeObj;
					if (outcome.getString("nextMapElementId").contentEquals(existingId))
						outcome.put("nextMapElementId", newId);
				}
			}
			//replace all outcomes. nextMapElementId		
		}
	}
	
	private static JSONObject findFlowByID(JSONArray objects, String id)
	{
		if (id!=null)
		{
			for (int i=0; i<objects.length(); i++)
			{
				JSONObject object = (JSONObject)objects.getJSONObject(i);
				if (!object.isNull("id") && object.getJSONObject("id").getString("id").contentEquals(id))
				{
					return objects.getJSONObject(i);
				}
			}
		}
		return null;
	}	
}
