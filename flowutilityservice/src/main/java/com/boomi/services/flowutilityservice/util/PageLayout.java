package com.boomi.services.flowutilityservice.util;

import java.util.List;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;
import com.boomi.services.flowutilityservice.gettypefields.Field;
import com.boomi.services.flowutilityservice.util.Service.ServicePattern;

public class PageLayout {

	private JSONArray newChartOperations;
	private JSONObject newChartMacro;
	private JSONArray newChartValues;
	private JSONObject newChartType;
	private Tenant tenant;
	private String templateFileName;
	private String serviceId;
	
	public PageLayout(Tenant tenant, String serviceId)
	{
		this.tenant=tenant;
		this.serviceId = serviceId;
		this.newChartValues=new JSONArray();
	}
	
	public enum OutputPageOption //page generation options
	{
		CHARTLIST, LIST, READONLYFORM, FORM, HTML, FILEUPLOAD, PIECHART
	}
	
	public static OutputPageOption validateOption(ServicePattern pattern, OutputPageOption option)
	{
		switch (pattern)
		{
		case SINGLETONIN_LISTOUT:
		case LISTOUT:
			if (option!=OutputPageOption.CHARTLIST && option!=OutputPageOption.LIST)
				option=OutputPageOption.LIST;
			break;
		case SINGLETONIN_SINGLETONOUT:
		case SINGLETONOUT:
			if (option==OutputPageOption.CHARTLIST || option==OutputPageOption.LIST)
				option=OutputPageOption.HTML;
			break;
		default:
			break;
		}
		return option;
	}
	public JSONObject createValueFromType(JSONObject type, boolean isList, String valueName)
	{
		String valueId=UUID.randomUUID().toString();
		
		String name = type.getString("developerName");
		if (valueName != null)
			name=valueName;
		String contentType;
		
		if (!isList) {
			name += " Value";
			contentType = "ContentObject";
		} else {
			name += " List";
			contentType = "ContentList";
		}
		
		name = Util.findUniqueName(name, tenant.getValues());		
		
		JSONObject value = new JSONObject();

	    value.put("access", "PRIVATE");
	    value.put("contentType", contentType);
	    value.put("developerName", name);
	    value.put("developerSummary", "");
	    value.put("elementType", "VARIABLE");
	    value.put("id", valueId);
	    value.put("isFixed", false);
	    value.put("isVersionless", false);
	    value.put("typeElementDeveloperName", type.getString("developerName"));
	    value.put("typeElementId", type.getString("id"));
	    value.put("updateByName", false);
	    return value;
	}
	
	public JSONObject createValueFromType(JSONObject type, boolean isList)
	{
	    return createValueFromType(type, isList, null);
	}
	
	public JSONObject createPageFromType(JSONObject type, JSONObject value, OutputPageOption pageType, String tableComponentId, String xAxis, String chartType, List<Field> selectedFields)
	{		
		String valueId = value.getString("id");
		String containerId = UUID.randomUUID().toString();
		JSONArray pageComponents=null;
		String label="";
		String typeName = type.getString("developerName");
				
		JSONObject page = new JSONObject();
		page.put("id", UUID.randomUUID().toString());
		page.put("elementType" , "PAGE_LAYOUT");
		JSONArray pageContainers = new JSONArray();
		page.put("pageContainers", pageContainers);
		JSONObject pageContainer = new JSONObject();
		pageContainers.put(pageContainer);
//		pageContainer.put("attributes", null);
		pageContainer.put("containerType", "VERTICAL_FLOW");
		pageContainer.put("developerName", "Main container");
		pageContainer.put("id", containerId);
		pageContainer.put("order", 0);
		pageContainer.put("pageContainers", new JSONArray());
//		pageContainer.put("tags", null);
		page.put("stopConditionsOnFirstTrue", false);
		switch (pageType)
		{
		case CHARTLIST:
			typeName = typeName + " Chart Page Layout";
			label = typeName;
			pageComponents = createTableChart(type, value, pageContainer, chartType, xAxis, selectedFields);
			break;
		case PIECHART:
			typeName = typeName + " Chart Page Layout";
			label = typeName;
			pageComponents = createSingletonChart(type, valueId, pageContainer, chartType, selectedFields);
			break;
		case FORM:
			typeName = typeName + " Form Page Layout";
			label = typeName + " - Edit Record";
			pageComponents = createInputFields(type, valueId, containerId, true, selectedFields);
			break;
		case LIST:
			typeName = typeName + " List Page Layout";
			label= typeName + " Record List";
			pageComponents = createTable(type, valueId, containerId, tableComponentId, selectedFields);
			break;
		case HTML:
			typeName = typeName + " Presentation HTML Page Layout";
			label = typeName + " Presentation HTML";
			pageComponents = createHTML(type, value.getString("developerName"), containerId, selectedFields);
			break;
		case READONLYFORM:
			typeName = typeName + " Readonly Form Page Layout";
			label = typeName + " - View Record";
			pageComponents = createInputFields(type, valueId, containerId, false, selectedFields);
			break;
		case FILEUPLOAD:
			typeName = typeName + " File Upload Page Layout";
			label = typeName + " - Upload File";
			pageComponents = createFileUpload(type, valueId, containerId, this.serviceId);
			break;
		default:
			break;		
		}
		typeName = Util.findUniqueName(typeName, tenant.getPages());
		page.put("developerName", typeName);
		pageContainer.put("label", label);
		page.put("pageComponents" , pageComponents);

		return page;
	}
	
	private static JSONArray createFileUpload(JSONObject type, String valueId, String containerId, String serviceId)
	{
		JSONArray pageComponents = new JSONArray();
		JSONObject pageComponent = new JSONObject();
		pageComponents.put(pageComponent);
		
		pageComponent.put("componentType", "file-upload");
		pageComponent.put("developerName", "File Upload");
		pageComponent.put("id", UUID.randomUUID().toString());
		pageComponent.put("isEditable", true);
		pageComponent.put("label", "File Upload");
		pageComponent.put("hintValue", "Click here or drag a file to upload");
		pageComponent.put("pageContainerDeveloperName", "Main container");
		pageComponent.put("pageContainerId", containerId);
		pageComponent.put("content", "");
		pageComponent.put("size", 0);
		pageComponent.put("height", 0);
		
		JSONObject attributes = new JSONObject();
		pageComponent.put("attributes",attributes);
		attributes.put("isAutoUpload", true);
		
		JSONObject fileDataRequest = new JSONObject();
		pageComponent.put("fileDataRequest",fileDataRequest);
		fileDataRequest.put("resourceFile", "");
		fileDataRequest.put("resourcePath", "/file"); //TODO we should dynamically get this from somewhere?
		fileDataRequest.put("serviceElementId", serviceId);		
		
		JSONObject valueElementValueBindingReferenceId = new JSONObject();
		pageComponent.put("valueElementValueBindingReferenceId", valueElementValueBindingReferenceId);
		valueElementValueBindingReferenceId.put("id", valueId);

		return pageComponents;
	}
	
	//For database service, valueId is a record type for master detail edit form, else it is contentlist of recordtype
	private static JSONArray createTable(JSONObject type, String valueId, String containerId, String tableComponentId, List<Field> fields)
	{
		JSONArray columns = new JSONArray();
		for (Field field:fields)
		{
			JSONObject column = new JSONObject();
//	        column.put("boundTypeElementPropertyId", null);
	        column.put("componentType", "");
	        column.put("isBound", false);
	        column.put("isDisplayValue", true);
	        column.put("isEditable", false);
	        column.put("label", field.getName());
	        column.put("order", 0);
	        column.put("typeElementPropertyDeveloperName", field.getName());
	        column.put("typeElementPropertyId", field.getId());
//	        column.put("typeElementPropertyToDisplayId", null);
			columns.put(column);
		}
	//TODO objectDataRequest null for non database service
		JSONArray pageComponents = new JSONArray();
		JSONObject pageComponent = new JSONObject();
		pageComponents.put(pageComponent);
	    //pageComponent.put("attributes", null);
	    pageComponent.put("columns", columns);
	    pageComponent.put("componentType", "table");
	    pageComponent.put("content", "");
	    pageComponent.put("developerName", type.getString("developerName") + " Table");
	    //pageComponent.put("fileDataRequest", null);
	    pageComponent.put("height", 0);
	    pageComponent.put("helpInfo", "");
	    pageComponent.put("hintValue", "");
	    pageComponent.put("id", tableComponentId);
	    pageComponent.put("imageUri", "");
	    pageComponent.put("isEditable", true);
	    pageComponent.put("isMultiSelect", false);
	    pageComponent.put("isRequired", false);
	    pageComponent.put("isSearchable", false);
	    pageComponent.put("label", "");
	    pageComponent.put("maxSize", 0);
	    pageComponent.put("order", 3);
	    pageComponent.put("pageContainerDeveloperName", "Main container");
	    pageComponent.put("pageContainerId", containerId);
	    pageComponent.put("size", 0);
	    //pageComponent.put("tags", null);
	    pageComponent.put("width", 0);
		if (!type.isNull("bindings")) // database only
		{
			JSONObject objectDataRequest = new JSONObject();
			pageComponents.getJSONObject(0).put("objectDataRequest", objectDataRequest);
//			objectDataRequest.put("command", null);
//			objectDataRequest.put("listFilter", null);
	        objectDataRequest.put("typeElementBindingId", type.getJSONArray("bindings").getJSONObject(0).getString("id"));
	        objectDataRequest.put("typeElementDeveloperName", type.getString("developerName"));
	        objectDataRequest.put("typeElementId", type.getString("id"));
	        
	        JSONObject valueElementValueBindingReferenceId = new JSONObject();
			pageComponents.getJSONObject(0).put("valueElementValueBindingReferenceId", valueElementValueBindingReferenceId);
			valueElementValueBindingReferenceId.put("command", "");
	        valueElementValueBindingReferenceId.put("id", valueId);
	        //valueElementValueBindingReferenceId.put("typeElementPropertyId", null);

		} else { //bind to ContentList
	        JSONObject valueElementDataBindingReferenceId = new JSONObject();
			pageComponents.getJSONObject(0).put("valueElementDataBindingReferenceId", valueElementDataBindingReferenceId);
			valueElementDataBindingReferenceId.put("command", "");
			valueElementDataBindingReferenceId.put("id", valueId);
	        //valueElementValueBindingReferenceId.put("typeElementPropertyId", null);		
		}
		return pageComponents;
	}

	private static JSONArray createInputFields(JSONObject type, String valueId, String containerId, boolean isEditable, List<Field> selectedFields)
	{	
		JSONArray pageComponents = new JSONArray();
		int i=0;
		for (Field field:selectedFields)
		{
			String developerName = field.getName();
			JSONObject pageComponent = new JSONObject();
			if (field.getContentType().contentEquals("ContentBoolean"))
		        pageComponent.put("componentType", "checkbox");
			else
				pageComponent.put("componentType", "input");
	        pageComponent.put("developerName", developerName);
	        pageComponent.put("id", UUID.randomUUID().toString());
	        pageComponent.put("isEditable", isEditable);
	        pageComponent.put("label", developerName);
	        pageComponent.put("maxSize", 255);
	        pageComponent.put("order", i+1);
	        pageComponent.put("pageContainerDeveloperName", "Main container");
	        pageComponent.put("pageContainerId", containerId);
	        pageComponent.put("size", 25);
	        
	        JSONObject valueElementValueBindingReferenceId = new JSONObject();
	        pageComponent.put("valueElementValueBindingReferenceId", valueElementValueBindingReferenceId);
	        valueElementValueBindingReferenceId.put("id", valueId);
	        valueElementValueBindingReferenceId.put("typeElementPropertyId", type.getJSONArray("properties").getJSONObject(i).getString("id"));

	        pageComponent.put("width", 0);
			pageComponents.put(pageComponent);
			i++;
		}
		return pageComponents;
	}
	
	private static JSONArray createHTML(JSONObject type, String valueName, String containerId, List<Field> selectedFields)
	{
		JSONArray pageComponents = new JSONArray();
		JSONObject pageComponent = new JSONObject();
		pageComponents.put(pageComponent);
		StringBuilder sb = new StringBuilder();
		for (Field field:selectedFields)
		{
			String developerName = field.getName();
			sb.append(String.format("<p>%s: {![%s].[%s]}</p>\r\n", developerName, valueName, developerName));
		}
        pageComponent.put("pageContainerDeveloperName", "Main container");
        pageComponent.put("pageContainerId", containerId);
        pageComponent.put("componentType", "presentation");
        pageComponent.put("id", UUID.randomUUID().toString());
        pageComponent.put("developerName", "Presentation");
        pageComponent.put("content", sb.toString());
        pageComponent.put("pageContainerId", containerId);
		return pageComponents;
	}
	
	private JSONArray createTableChart(JSONObject type, JSONObject listValue, JSONObject pageContainer, String chartType, String xAxisPropertyId, List<Field> selectedFields)
	{
		JSONArray pageComponents = new JSONArray();
		JSONObject labelProperty;
		JSONObject valueProperty;
		JSONObject chartContainer = createChartContainer(pageContainer);
		String chartName = type.getString("developerName") + " Chart";
		JSONArray properties = type.getJSONArray("properties");
		labelProperty = Util.findObjectByID(properties, xAxisPropertyId);
		String xAxisName = labelProperty.getString("developerName");
		String listValueId = listValue.getString("id");
		if (selectedFields.size()==1)
		{
			//simple list chart, pass the xaxis and value properties for list value
			labelProperty = Util.findObjectByID(properties, xAxisPropertyId);
			valueProperty = Util.findObjectByID(properties, selectedFields.get(0).getId());
			pageComponents.put(createChartComponent(chartType, listValueId, chartName, chartContainer, labelProperty, valueProperty));
		} else if (selectedFields.size()>1){
			//generate type, value, listvalue and macro and macro operator operation
			//pass in all list values, id and label/value properties
			JSONObject labelValueType = createChartLabelValueType();
			JSONObject labelValueValue = createValueFromType(labelValueType, false);
			JSONObject labelValueList = createValueFromType(labelValueType, true);
			newChartType = labelValueType;
			newChartValues.put(labelValueValue);
			newChartValues.put(labelValueList);
			JSONObject listEntryValue = createValueFromType(type, false); //the macro needs this as a temp var
			newChartValues.put(listEntryValue);

			labelProperty = labelValueType.getJSONArray("properties").getJSONObject(0);
			valueProperty = labelValueType.getJSONArray("properties").getJSONObject(1);

			JSONArray newValues = generateChartMacro(selectedFields, listEntryValue.getString("developerName"), listValue.getString("developerName"), xAxisName, labelValueType, labelValueValue.getString("developerName"));	

			for (Object valueObj:newValues)
			{
				JSONObject value = (JSONObject)valueObj;
				pageComponents.put(createChartComponent(chartType, value.getString("id"), chartName + " " + value.getString("developerName"), chartContainer, labelProperty, valueProperty));
				this.newChartValues.put(value);
			}
		}
		return pageComponents;
	}
	
	private JSONArray createSingletonChart(JSONObject type, String valueId, JSONObject pageContainer, String chartType, List<Field> selectedFields)
	{
		JSONArray pageComponents=new JSONArray();
		JSONObject chartContainer = createChartContainer(pageContainer);
		String chartName = type.getString("developerName") + " Chart";
		
		JSONObject operation;
		JSONObject valueElementToApplyId;
		JSONObject valueElementToReferenceId;
		JSONObject labelValueType = createChartLabelValueType();
		JSONObject labelValueValue = createValueFromType(labelValueType, false);
		JSONObject labelValueList = createValueFromType(labelValueType, true);

		this.newChartType = labelValueType;
		newChartValues.put(labelValueValue);
		newChartValues.put(labelValueList);

		//TODO don't hardcode indexes
		JSONObject labelProperty = labelValueType.getJSONArray("properties").getJSONObject(0);
		JSONObject valueProperty = labelValueType.getJSONArray("properties").getJSONObject(1);

		JSONArray operations = new JSONArray();
		int i=0;
		for (Field field:selectedFields)
		{
			//Create new label constant value and set defaultValue to field.getName()
			String constName = Util.findUniqueName("Constant " + field.getName(), tenant.getValues());
			JSONObject labelConstValue = new JSONObject();
			this.newChartValues.put(labelConstValue);
			labelConstValue.put("id", UUID.randomUUID().toString());
			labelConstValue.put("developerName", constName);
			labelConstValue.put("defaultContentValue", field.getName());
			
			labelConstValue.put("access", "PRIVATE");
			labelConstValue.put("contentType", "ContentString");
			labelConstValue.put("elementType", "VARIABLE");
		
			//EMPTY Name/Value Object
			operation = new JSONObject();
			operation.put("id", UUID.randomUUID().toString());
			operation.put("order", ++i);
			
			valueElementToApplyId = new JSONObject();
			operation.put("valueElementToApplyId", valueElementToApplyId);
			valueElementToApplyId.put("id", labelValueValue.getString("id"));
			valueElementToApplyId.put("command", "EMPTY");
			operations.put(operation);
			
			//Set Label = const
			operation = new JSONObject();
			operation.put("id", UUID.randomUUID().toString());
			operation.put("order", ++i);
			
			valueElementToApplyId = new JSONObject();
			operation.put("valueElementToApplyId", valueElementToApplyId);
			valueElementToApplyId.put("id", labelValueValue.getString("id"));
			valueElementToApplyId.put("typeElementPropertyId", labelProperty.getString("id"));   //.Label
			valueElementToApplyId.put("command", "SET_EQUAL");
			
			valueElementToReferenceId = new JSONObject();
			valueElementToReferenceId.put("id", labelConstValue.getString("id"));
			operation.put("valueElementToReferenceId", valueElementToReferenceId);
			valueElementToReferenceId.put("command", "VALUE_OF");
			operations.put(operation);
			
			//Set value = output element
			operation = new JSONObject();
			operation.put("id", UUID.randomUUID().toString());
			operation.put("order", ++i);
			
			valueElementToApplyId = new JSONObject();
			operation.put("valueElementToApplyId", valueElementToApplyId);
			valueElementToApplyId.put("id", labelValueValue.getString("id"));
			valueElementToApplyId.put("typeElementPropertyId", valueProperty.getString("id"));  
			valueElementToApplyId.put("command", "SET_EQUAL");
			
			//Field id might be stale if it is a singleton generated type that is generated once to send to the UI and generated again at flow creation time
			//so lets just find the fresh id by name
			JSONObject property = Util.findObjectByName(type.getJSONArray("properties"), field.getName());
			valueElementToReferenceId = new JSONObject();
			operation.put("valueElementToReferenceId", valueElementToReferenceId);
			valueElementToReferenceId.put("id", valueId);
			valueElementToReferenceId.put("typeElementPropertyId", property.getString("id"));
			valueElementToReferenceId.put("command", "VALUE_OF");
			operations.put(operation);
			
			//Add object to list
			operation = new JSONObject();
			operation.put("id", UUID.randomUUID().toString());
			operation.put("order", ++i);
			
			valueElementToApplyId = new JSONObject();
			valueElementToApplyId.put("id", labelValueList.getString("id"));
			operation.put("valueElementToApplyId", valueElementToApplyId);
			valueElementToApplyId.put("command", "ADD");
			
			valueElementToReferenceId = new JSONObject();
			operation.put("valueElementToReferenceId", valueElementToReferenceId);
			valueElementToReferenceId.put("id", labelValueValue.getString("id"));
			valueElementToReferenceId.put("command", "VALUE_OF");
			operations.put(operation);
		}
		this.newChartOperations = operations;
		
		pageComponents.put(createChartComponent(chartType, labelValueList.getString("id"), chartName, chartContainer, labelProperty, valueProperty));
		//generate type, value, listvalue and operator actions
		//pass in list value id and label/value properties
		return pageComponents;
	}
		
	//TODO need to work for both xaxis/field and label/value list
	private static JSONObject createChartComponent(String chartType, String listValueId, String chartName, JSONObject chartContainer, JSONObject labelProperty, JSONObject valueProperty)
	{
		String valueName = valueProperty.getString("developerName");
		String valueId = valueProperty.getString("id");
		String labelName = labelProperty.getString("developerName");
		String labelId = labelProperty.getString("id");
		JSONObject pageComponent = new JSONObject();
		
		pageComponent.put("componentType", chartType);
		pageComponent.put("developerName", chartName);
		pageComponent.put("id", UUID.randomUUID().toString());
		pageComponent.put("label", chartName);
		pageComponent.put("pageContainerDeveloperName", chartContainer.getString("developerName"));
		pageComponent.put("pageContainerId", chartContainer.getString("id"));

		JSONObject valueElementDataBindingReferenceId = new JSONObject();
		pageComponent.put("valueElementDataBindingReferenceId", valueElementDataBindingReferenceId);
		valueElementDataBindingReferenceId.put("id", listValueId);
		JSONArray columns = new JSONArray();
		pageComponent.put("columns", columns);
		
		JSONObject column = new JSONObject();
		columns.put(column);
		column.put("isDisplayValue", true);
		column.put("label", labelName);
		column.put("developerName", labelName);
		column.put("order", 0);
		column.put("typeElementPropertyDeveloperName", labelName);
		column.put("typeElementPropertyId", labelId);

		column = new JSONObject();
		columns.put(column);
		column.put("isDisplayValue", true);
		column.put("label", valueName);
		column.put("developerName", valueName);
		column.put("order", 1);
		column.put("typeElementPropertyDeveloperName", valueName);
		column.put("typeElementPropertyId", valueId);
		return pageComponent;
	}
	
	private static JSONObject createChartContainer(JSONObject pageContainer)
	{
		JSONArray subPageContainers = new JSONArray();
		pageContainer.put("pageContainers", subPageContainers);
		JSONObject chartContainer = new JSONObject();
		subPageContainers.put(chartContainer);
		chartContainer.put("id", UUID.randomUUID().toString());
		chartContainer.put("order", 0);
		chartContainer.put("containerType", "CHARTS");
		chartContainer.put("developerName", "Chart Container");
		chartContainer.put("label", "");
		return chartContainer;
	}
	
	//Values and Types for charts
	private JSONObject createChartLabelValueType()
	{
		JSONObject type = new JSONObject();
		
		type.put("developerName", Util.findUniqueName("Chart Label/Numeric", this.tenant.getTypes()));
		type.put("elementType", "TYPE");
		type.put("id", UUID.randomUUID().toString());
		
		JSONArray properties = new JSONArray();
		type.put("properties", properties);
		
		JSONObject property = new JSONObject();
		property.put("contentType", "ContentString");
		property.put("developerName", "Label");
		property.put("id", "4fa1d1df-3491-4b46-9719-919917c50e54");
		properties.put(property);
		property = new JSONObject();
		property.put("contentType", "ContentNumber");
		property.put("developerName", "Value");
		property.put("id", "144e3594-3cd8-443d-9ae9-df78a6d33be1");
		properties.put(property);
		return type;
	}
	
//	//Generate the ListValues that the chart macro pushes into
//	private JSONArray createChartLabelValueListFromField(List<Field> fields, JSONObject chartLabelValue)
//	{
//		JSONArray newValues = new JSONArray();
//        for (Field field:fields)
//        {
//    		JSONObject value = this.createValueFromType(chartLabelValue, true);
//    		newValues.put(value);
//        }
//        return newValues;
//	}
	
	//generateOperatorOperations TODO
	
	//Sets lists of label/value with name "Chart List <selected numeric column name>"
	//These values must be generated and associated with each respect chart 
	//TODO public for test
	public JSONArray generateChartMacro(List<Field> fields, String listEntryName, String listValueName, String xAxisPropertyName, JSONObject labelValueType, String labelValueName)
	{
		StringBuilder sb = new StringBuilder();
		JSONArray newValues = new JSONArray();
		sb.append(String.format("var listData = state.getArray(\"{![%s]}\")\r\n", listValueName));
		sb.append(String.format("var tmpData\r\n", listValueName));
		for (int i=0; i< fields.size(); i++)
		{
			String propertyName = fields.get(i).getName();
    		JSONObject value = this.createValueFromType(labelValueType, true, propertyName);
    		newValues.put(value);

			sb.append(String.format("tmpData = [];\r\n"));
			sb.append(String.format("var increment=1;\r\n"));
			sb.append(String.format("if (listData.length>250)\r\n"));
			sb.append(String.format("    increment =  Math.round(listData.length/250);\r\n"));
			sb.append(String.format("for (var x=0; x<listData.length; x+=increment)\r\n")); 
			sb.append(String.format("{\r\n"));
			sb.append(String.format("    state.setObject(\"{![%s]}\", listData[x]);\r\n", listEntryName));
			sb.append(String.format("    state.setStringValue(\"{![%s].[Label]}\", state.getStringValue(\"{![%s].[%s]}\"));\r\n", labelValueName, listEntryName, xAxisPropertyName));
			sb.append(String.format("    state.setNumberValue(\"{![%s].[Value]}\", state.getNumberValue(\"{![%s].[%s]}\"));\r\n", labelValueName, listEntryName, propertyName));
			sb.append(String.format("    tmpData.push(state.getObject(\"{![%s]}\"));\r\n", labelValueName));
			sb.append(String.format("}\r\n"));
			sb.append(String.format("state.setArray(\"{![%s]}\", tmpData);\r\n\r\n", value.getString("developerName"))); //This is what is associated with each chart!
		}
		
		JSONObject macroObject=new JSONObject();
        macroObject.put("code", sb.toString());
        macroObject.put("developerName", Util.findUniqueName("Macro Chart " + listValueName, tenant.getMacros()));
        macroObject.put("elementType", "MACRO");
        macroObject.put("id", UUID.randomUUID().toString());
        this.newChartMacro = macroObject;

        JSONObject operation = new JSONObject();
		operation.put("id", UUID.randomUUID().toString());
        operation.put("macroElementToExecuteDeveloperName", macroObject.getString("developerName"));
        operation.put("macroElementToExecuteId", macroObject.getString("id"));
        newChartOperations = new JSONArray();
        newChartOperations.put(operation);  
        
        return newValues;
	}

	//operator operations created for singleton charts
	public JSONArray getNewChartOperations() {
		return newChartOperations;
	}

	public JSONObject getNewChartMacro() {
		return newChartMacro;
	}

	public JSONArray getNewChartValues() {
		return newChartValues;
	}

	public JSONObject getNewChartType() {
		return newChartType;
	}

	public String getTemplateFileName() {
		return templateFileName;
	}		
}
