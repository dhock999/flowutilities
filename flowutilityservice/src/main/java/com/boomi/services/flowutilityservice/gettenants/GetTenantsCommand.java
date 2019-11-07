package com.boomi.services.flowutilityservice.gettenants;

import java.io.IOException;
import java.util.List;

import com.boomi.services.flowutilityservice.ApplicationConfiguration;
import com.boomi.services.flowutilityservice.util.FlowAPIClient;
import com.boomi.services.flowutilityservice.gettenants.GetTenants.Inputs;
import com.boomi.services.flowutilityservice.gettenants.GetTenants.Outputs;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;

public class GetTenantsCommand implements ActionCommand<ApplicationConfiguration, GetTenants, Inputs, Outputs>{

	@Override
	public ActionResponse<Outputs> execute(ApplicationConfiguration configuration, ServiceRequest request,
			Inputs input) {
		List<Tenant> tenants=null;
		try {
			tenants = FlowAPIClient.getTenants(input.getToken());
			System.out.println("Tenants" + tenants.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ActionResponse<>(new Outputs(tenants));
	}

}