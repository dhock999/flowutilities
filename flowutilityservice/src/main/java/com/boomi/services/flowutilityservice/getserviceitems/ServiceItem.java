package com.boomi.services.flowutilityservice.getserviceitems;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

@Type.Element(name = "Service Item")
public class ServiceItem implements Type{
	@Type.Identifier
	private String guid;
	@Type.Property(name = "Id", contentType = ContentType.String)
	private String id;
	@Type.Property(name = "Service Name", contentType = ContentType.String)
	private String name;
	@Type.Property(name = "Action Name", contentType = ContentType.String)
	private String actionName;
	@Type.Property(name = "Service - Action Name", contentType = ContentType.String)
	private String serviceActionName;
	@Type.Property(name = "Service Type", contentType = ContentType.String)
	private String serviceType;
	@Type.Property(name = "Design Pattern", contentType = ContentType.String)
	private String designPattern;
	@Type.Property(name = "Type Name", contentType = ContentType.String)
	private String typeName;
	@Type.Property(name = "Type ID", contentType = ContentType.String)
	private String typeId;
	@Type.Property(name = "Description", contentType = ContentType.String)
	private String description;
	
	public ServiceItem(String id, String name, String guid, String actionName
			, String serviceActionName, String serviceType, String designPattern
			, String typeName, String typeId, String description)
	{
		this.name=name;
		this.id=id;
		this.guid=guid;
		this.actionName = actionName;
		this.serviceActionName = serviceActionName;
		this.serviceType = serviceType;
		this.designPattern = designPattern;
		this.typeId = typeId;
		this.typeName = typeName;
		this.description = description;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public String getServiceActionName() {
		return serviceActionName;
	}
	public void setServiceActionName(String serviceActionName) {
		this.serviceActionName = serviceActionName;
	}
	public String getDesignPattern() {
		return designPattern;
	}
	public void setDesignPattern(String designPattern) {
		this.designPattern = designPattern;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}	
}
