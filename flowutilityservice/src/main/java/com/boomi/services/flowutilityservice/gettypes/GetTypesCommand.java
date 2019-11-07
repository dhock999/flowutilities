package com.boomi.services.flowutilityservice.gettypes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.boomi.services.flowutilityservice.ApplicationConfiguration;
import com.boomi.services.flowutilityservice.util.FlowAPIClient;
import com.boomi.services.flowutilityservice.gettypes.GetTypes.Inputs;
import com.boomi.services.flowutilityservice.gettypes.GetTypes.Outputs;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;

public class GetTypesCommand implements ActionCommand<ApplicationConfiguration, GetTypes, Inputs, Outputs>{

	@Override
	public ActionResponse<Outputs> execute(ApplicationConfiguration configuration, ServiceRequest request,
			Inputs input) {
		List<Type> types=null;
		try {
			JSONArray array = FlowAPIClient.getTypes(input.getToken(), input.getTenantId());
			types = new ArrayList<Type>();
		    for (Object type:array)
		    {
		    	types.add(new Type(((JSONObject)type).getString("id"), ((JSONObject)type).getString("developerName"), UUID.randomUUID().toString()));
		    	System.out.println(((JSONObject)type).getString("developerName"));
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ActionResponse<>(new Outputs(types));
	}
}
