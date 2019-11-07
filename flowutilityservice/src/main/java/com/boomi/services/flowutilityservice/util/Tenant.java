package com.boomi.services.flowutilityservice.util;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;

public class Tenant {

	private String token;
	private String tenantId;
	private JSONArray services;
	private JSONArray types;
	private JSONArray values;
	private JSONArray pages;
	private JSONArray macros;
	private JSONArray flows;
	
	//for mock data
	public Tenant()
	{
		
	}
	
	public Tenant(String token, String tenantId) throws JSONException, IOException
	{
		this.token = token;
		this.tenantId = tenantId;
		types = new JSONArray(FlowAPIClient.doGet("/api/draw/1/element/type", token, tenantId));
		values = new JSONArray(FlowAPIClient.doGet("/api/draw/1/element/value", token, tenantId));
		pages = new JSONArray(FlowAPIClient.doGet("/api/draw/1/element/page", token, tenantId));
		services = new JSONArray(FlowAPIClient.doGet("/api/draw/1/element/service", token, tenantId));
		flows = new JSONArray(FlowAPIClient.doGet("/api/draw/1/flow", token, tenantId));
		macros = new JSONArray(FlowAPIClient.doGet("/api/draw/1/flow", token, tenantId));
	}

	public JSONArray getServices() {
		return services;
	}

	public void setServices(JSONArray services) {
		this.services = services;
	}

	public JSONArray getTypes() {
		return types;
	}

	public void setTypes(JSONArray types) {
		this.types = types;
	}

	public JSONArray getValues() {
		return values;
	}

	public void setValues(JSONArray values) {
		this.values = values;
	}

	public JSONArray getPages() {
		return pages;
	}

	public void setPages(JSONArray pages) {
		this.pages = pages;
	}

	public JSONArray getMacros() {
		return macros;
	}

	public void setMacros(JSONArray macros) {
		this.macros = macros;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public JSONArray getFlows() {
		return flows;
	}

	public void setFlows(JSONArray flows) {
		this.flows = flows;
	}
}
