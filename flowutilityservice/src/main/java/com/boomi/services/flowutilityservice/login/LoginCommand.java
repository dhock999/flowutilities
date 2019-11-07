package com.boomi.services.flowutilityservice.login;

import java.io.IOException;
import com.boomi.services.flowutilityservice.ApplicationConfiguration;
import com.boomi.services.flowutilityservice.util.FlowAPIClient;
import com.boomi.services.flowutilityservice.login.Login.Inputs;
import com.boomi.services.flowutilityservice.login.Login.Outputs;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;

public class LoginCommand implements ActionCommand<ApplicationConfiguration, Login, Inputs, Outputs>{

	@Override
	public ActionResponse<Outputs> execute(ApplicationConfiguration configuration, ServiceRequest request,
			Inputs input) {
		String token = "";
		try {
			token = FlowAPIClient.login(input.getUsername(),input.getPassword());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ActionResponse<>(new Outputs(token));
	}
}
