package com.boomi.services.flowutilityservice.getvalues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.boomi.services.flowutilityservice.ApplicationConfiguration;
import com.boomi.services.flowutilityservice.util.FlowAPIClient;
import com.boomi.services.flowutilityservice.getvalues.GetValues.Inputs;
import com.boomi.services.flowutilityservice.getvalues.GetValues.Outputs;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;

public class GetValuesCommand implements ActionCommand<ApplicationConfiguration, GetValues, Inputs, Outputs>{

	@Override
	public ActionResponse<Outputs> execute(ApplicationConfiguration configuration, ServiceRequest request,
			Inputs input) {
		List<Value> values=null;
		try {
			JSONArray array = FlowAPIClient.getValues(input.getToken(), input.getTenantId());
			values = new ArrayList<Value>();
		    for (Object value:array)
		    {
		    	values.add(new Value(((JSONObject)value).getString("id"), ((JSONObject)value).getString("developerName"), UUID.randomUUID().toString()));
		    	System.out.println(((JSONObject)value).getString("developerName"));
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ActionResponse<>(new Outputs(values));
	}
}
