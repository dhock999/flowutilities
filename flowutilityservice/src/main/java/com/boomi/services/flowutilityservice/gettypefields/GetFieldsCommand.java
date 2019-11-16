package com.boomi.services.flowutilityservice.gettypefields;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import com.boomi.services.flowutilityservice.ApplicationConfiguration;
import com.boomi.services.flowutilityservice.util.Tenant;
import com.boomi.services.flowutilityservice.gettypefields.GetFields.Inputs;
import com.boomi.services.flowutilityservice.gettypefields.GetFields.Outputs;
import com.boomi.services.flowutilityservice.util.Service;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;

public class GetFieldsCommand implements ActionCommand<ApplicationConfiguration, GetFields, Inputs, Outputs>{
	Logger logger = Logger.getLogger(this.getClass().getName());
	@Override
	public ActionResponse<Outputs> execute(ApplicationConfiguration configuration, ServiceRequest request,
			Inputs input) {
		List<Field> fields=null;
		try {
			Tenant tenant = new Tenant(input.getToken(), input.getTenantId());
			System.out.print(String.format("Service Id %s Type Id %s Action %s", input.getServiceId(), input.getTypeId(), input.getActionName()));

			fields = Service.getOutputTypeFields(tenant, input.getTypeId(), input.getServiceId(), input.getActionName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ActionResponse<>(new Outputs(fields));
	}
}
