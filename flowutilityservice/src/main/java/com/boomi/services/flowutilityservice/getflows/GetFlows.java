package com.boomi.services.flowutilityservice.getflows;
import java.io.IOException;
import java.util.List;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Get Flows", summary = "Return flows for tenant", uri="/flowutilities/flows")
public class GetFlows {
	public static class Inputs{
		@Action.Input(name="Token", contentType=ContentType.String)
		private String token;
		@Action.Input(name="Tenant ID", contentType=ContentType.String)
		private String tenantId;
		
		public String getToken() {
			return token;
		}		

		public String getTenantId() {
			return tenantId;
		}		
	}
	
	public static class Outputs {
		@Action.Output(name="Flows", contentType=ContentType.List)
		private List<Flow> flows;
		
		public Outputs(List<Flow> flows)
		{
			this.flows=flows;
		}

		public List<Flow> getFlows() throws IOException {
			return flows;
		}
	}
}
