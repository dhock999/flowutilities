package com.boomi.services.flowutilityservice.createflow;

import com.boomi.services.flowutilityservice.ApplicationConfiguration;
import com.boomi.services.flowutilityservice.createflow.CreateFlow.Inputs;
import com.boomi.services.flowutilityservice.createflow.CreateFlow.Outputs;
import com.boomi.services.flowutilityservice.util.FlowDefinition;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;

public class CreateFlowCommand implements ActionCommand<ApplicationConfiguration, CreateFlow, Inputs, Outputs>{

	@Override
	public ActionResponse<Outputs> execute(ApplicationConfiguration configuration, ServiceRequest request,
			Inputs input) {
		String response="";
		try {
			String token=input.getToken();
			String tenant=input.getTenantId();
			FlowDefinition generator = new FlowDefinition(token, tenant, input.getServiceId(), input.getActionName(), input.getTypeId(), input.getFlowId(), input.getOption(), input.getxAxis(), input.getChartType(), input.getFields(), input.getFileUploadSchema());
	//		response = FlowAPIUtil.generateDesignPattern(token, tenant, input.getServiceId(), input.getActionName(), input.getTypeId());
			response = generator.generateDesignPattern();
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			throw new ServiceProblemException(e.getMessage(), 0, "");
		}
		return new ActionResponse<>(new Outputs(response));
	}
}
