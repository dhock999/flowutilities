package com.boomi.services.flowutilityservice.gettypes;

import com.manywho.sdk.api.ContentType;

@com.manywho.sdk.services.types.Type.Element(name = "Type")
public class Type implements com.manywho.sdk.services.types.Type{
	@com.manywho.sdk.services.types.Type.Identifier
	private String guid;
	@com.manywho.sdk.services.types.Type.Property(name = "Id", contentType = ContentType.String)
	private String id;
	@com.manywho.sdk.services.types.Type.Property(name = "Name", contentType = ContentType.String)
	private String name;
	public Type(String id, String name, String guid)
	{
		this.name=name;
		this.id=id;
		this.guid=guid;
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
}
