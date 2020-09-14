package com.boomi.services.flowutilityservice.getservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.boomi.services.flowutilityservice.ApplicationConfiguration;
import com.boomi.services.flowutilityservice.getpages.Page;
import com.boomi.services.flowutilityservice.util.FlowAPIClient;
import com.boomi.services.flowutilityservice.util.Service;
import com.google.common.collect.Lists;
import com.boomi.services.flowutilityservice.getservices.GetServices.Inputs;
import com.boomi.services.flowutilityservice.getservices.GetServices.Outputs;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;

public class GetServicesCommand implements ActionCommand<ApplicationConfiguration, GetServices, Inputs, Outputs>{

    private Logger logger = Logger.getLogger(this.getClass().getName());
	@Override
	public ActionResponse<Outputs> execute(ApplicationConfiguration configuration, ServiceRequest request,
			Inputs input) {
		List<ServiceDefinition> flowServices=null;
		try {
			JSONArray array = FlowAPIClient.getServices(input.getToken(), input.getTenantId());
			flowServices = Lists.newArrayList();
		    for (Object service:array)
		    {
		    	String developerSummary="";
		    	
		    	if (((JSONObject)service).has("developerSummary"))
		    	{
		    		Object obj =((JSONObject)service).get("developerSummary");
		    		if (obj !=null && obj instanceof String)
		    			developerSummary = (String)obj;
		    		else
			    		logger.info("developerSummary:" + obj);		    			
		    	}
		    	
		    	flowServices.add(new ServiceDefinition(((JSONObject)service).getString("id")
		    			, ((JSONObject)service).getString("developerName")
		    			, UUID.randomUUID().toString(),
		    			developerSummary));
		    	System.out.println(((JSONObject)service).getString("developerName"));
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ActionResponse<>(new Outputs(flowServices));
	}
}
