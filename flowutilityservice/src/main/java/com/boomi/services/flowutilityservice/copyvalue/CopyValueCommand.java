package com.boomi.services.flowutilityservice.copyvalue;

import java.io.IOException;

import com.boomi.services.flowutilityservice.ApplicationConfiguration;
import com.boomi.services.flowutilityservice.util.FlowAPIClient;
import com.boomi.services.flowutilityservice.copyvalue.CopyValue.Inputs;
import com.boomi.services.flowutilityservice.copyvalue.CopyValue.Outputs;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;

public class CopyValueCommand implements ActionCommand<ApplicationConfiguration, CopyValue, Inputs, Outputs>{

	@Override
	public ActionResponse<Outputs> execute(ApplicationConfiguration configuration, ServiceRequest request,
			Inputs input) {
		String PageName="TODO";
		try {
			PageName = FlowAPIClient.copyValue(input.getToken(), input.getTenantId(), input.getValueId());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ActionResponse<>(new Outputs(PageName));
	}
}
