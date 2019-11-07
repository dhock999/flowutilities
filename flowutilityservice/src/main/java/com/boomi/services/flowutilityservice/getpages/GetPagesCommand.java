package com.boomi.services.flowutilityservice.getpages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.boomi.services.flowutilityservice.ApplicationConfiguration;
import com.boomi.services.flowutilityservice.util.FlowAPIClient;
import com.boomi.services.flowutilityservice.getpages.GetPages.Inputs;
import com.boomi.services.flowutilityservice.getpages.GetPages.Outputs;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;

public class GetPagesCommand implements ActionCommand<ApplicationConfiguration, GetPages, Inputs, Outputs>{

	@Override
	public ActionResponse<Outputs> execute(ApplicationConfiguration configuration, ServiceRequest request,
			Inputs input) {
		List<Page> pages=null;
		try {
			JSONArray array = FlowAPIClient.getPages(input.getToken(), input.getTenantId());
			pages = new ArrayList<Page>();
		    for (Object page:array)
		    {
		    	pages.add(new Page(((JSONObject)page).getString("id"), ((JSONObject)page).getString("developerName"), UUID.randomUUID().toString()));
		    	System.out.println(((JSONObject)page).getString("developerName"));
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ActionResponse<>(new Outputs(pages));
	}
}
