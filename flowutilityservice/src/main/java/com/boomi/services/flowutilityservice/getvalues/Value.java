package com.boomi.services.flowutilityservice.getvalues;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

@Type.Element(name = "Value")
public class Value implements Type{
	@Type.Identifier
	private String guid;
	@Type.Property(name = "Id", contentType = ContentType.String)
	private String id;
	@Type.Property(name = "Name", contentType = ContentType.String)
	private String name;
	public Value(String id, String name, String guid)
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
