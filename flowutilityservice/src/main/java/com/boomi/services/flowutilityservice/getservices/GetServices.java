package com.boomi.services.flowutilityservice.getservices;
import java.io.IOException;
import java.util.List;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Get Services", summary = "Return services for the tenant", uri="/flowutilities/services")
public class GetServices {
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
		@Action.Output(name="Services", contentType=ContentType.List)
		private List<ServiceDefinition> services;
		
		public Outputs(List<ServiceDefinition> services)
		{
			this.services=services;
		}

		public List<ServiceDefinition> getServices() throws IOException {
			return services;
		}
	}
}
