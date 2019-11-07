package com.boomi.services.flowutilityservice.copypage;

import java.io.IOException;

import com.boomi.services.flowutilityservice.ApplicationConfiguration;
import com.boomi.services.flowutilityservice.util.FlowAPIClient;
import com.boomi.services.flowutilityservice.copypage.CopyPage.Inputs;
import com.boomi.services.flowutilityservice.copypage.CopyPage.Outputs;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;

public class CopyPageCommand implements ActionCommand<ApplicationConfiguration, CopyPage, Inputs, Outputs>{

	@Override
	public ActionResponse<Outputs> execute(ApplicationConfiguration configuration, ServiceRequest request,
			Inputs input) {
		String pageName="TODO";
		try {
			pageName = FlowAPIClient.copyPage(input.getToken(), input.getTenantId(), input.getPageId());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ActionResponse<>(new Outputs(pageName));
	}
}
