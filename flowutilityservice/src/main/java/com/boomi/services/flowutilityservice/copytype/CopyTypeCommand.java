package com.boomi.services.flowutilityservice.copytype;

import java.io.IOException;

import com.boomi.services.flowutilityservice.ApplicationConfiguration;
import com.boomi.services.flowutilityservice.util.FlowAPIClient;
import com.boomi.services.flowutilityservice.copytype.CopyType.Inputs;
import com.boomi.services.flowutilityservice.copytype.CopyType.Outputs;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;

public class CopyTypeCommand implements ActionCommand<ApplicationConfiguration, CopyType, Inputs, Outputs>{

	@Override
	public ActionResponse<Outputs> execute(ApplicationConfiguration configuration, ServiceRequest request,
			Inputs input) {
		String typeName="";
		try {
			typeName = FlowAPIClient.copyType(input.getToken(), input.getTenantId(), input.getTypeId());
		} catch (IOException e) {
		}
		return new ActionResponse<>(new Outputs(typeName));
	}
}
