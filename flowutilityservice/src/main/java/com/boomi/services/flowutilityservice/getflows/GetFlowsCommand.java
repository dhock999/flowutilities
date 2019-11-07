package com.boomi.services.flowutilityservice.getflows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.boomi.services.flowutilityservice.ApplicationConfiguration;
import com.boomi.services.flowutilityservice.util.FlowAPIClient;
import com.boomi.services.flowutilityservice.getflows.GetFlows.Inputs;
import com.boomi.services.flowutilityservice.getflows.GetFlows.Outputs;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;

public class GetFlowsCommand implements ActionCommand<ApplicationConfiguration, GetFlows, Inputs, Outputs>{

	@Override
	public ActionResponse<Outputs> execute(ApplicationConfiguration configuration, ServiceRequest request,
			Inputs input) {
		List<Flow> flows=null;
		try {
			JSONArray array = FlowAPIClient.getFlows(input.getToken(), input.getTenantId());
			flows = new ArrayList<Flow>();
		    for (Object flow:array)
		    {
		    	flows.add(new Flow(((JSONObject)flow).getJSONObject("id").getString("id"), ((JSONObject)flow).getString("developerName"), UUID.randomUUID().toString()));
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ActionResponse<>(new Outputs(flows));
	}
}
