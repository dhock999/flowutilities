package com.boomi.services.flowutilityservice.getserviceitems;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import com.boomi.services.flowutilityservice.ApplicationConfiguration;
import com.boomi.services.flowutilityservice.getserviceitems.ServiceItem;
import com.boomi.services.flowutilityservice.util.FlowAPIClient;
import com.boomi.services.flowutilityservice.util.Service;
import com.boomi.services.flowutilityservice.getserviceitems.GetServiceItems.Inputs;
import com.boomi.services.flowutilityservice.getserviceitems.GetServiceItems.Outputs;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;

public class GetServiceItemsCommand implements ActionCommand<ApplicationConfiguration, GetServiceItems, Inputs, Outputs>{
	Logger logger = Logger.getLogger(this.getClass().getName());
	@Override
	public ActionResponse<Outputs> execute(ApplicationConfiguration configuration, ServiceRequest request,
			Inputs input) {
		List<ServiceItem> flowServices=null;
		try {
			JSONArray services = FlowAPIClient.getServices(input.getToken(), input.getTenantId());
			JSONArray types = FlowAPIClient.getTypes(input.getToken(), input.getTenantId());
			flowServices=Service.getFlowServiceItems(input.getServiceId(), services, types);
		} catch (IOException e) {
			logger.severe(e.getMessage());
		}
		return new ActionResponse<>(new Outputs(flowServices));
	}
}
