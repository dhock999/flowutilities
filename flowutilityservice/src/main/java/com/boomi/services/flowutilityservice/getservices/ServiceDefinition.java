package com.boomi.services.flowutilityservice.getservices;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

@Type.Element(name = "Service")
public class ServiceDefinition implements Type{
	@Type.Identifier
	private String guid;
	@Type.Property(name = "Id", contentType = ContentType.String)
	private String id;
	@Type.Property(name = "Service Name", contentType = ContentType.String)
	private String name;
	@Type.Property(name = "Description", contentType = ContentType.String)
	private String description;
	
	public ServiceDefinition(String id, String name, String guid, String description)
	{
		this.name=name;
		this.id=id;
		this.guid=guid;
		this.description=description;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
