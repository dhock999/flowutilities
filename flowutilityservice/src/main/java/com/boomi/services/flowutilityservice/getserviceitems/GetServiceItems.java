package com.boomi.services.flowutilityservice.getserviceitems;
import java.io.IOException;
import java.util.List;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Get Service Types and Actions", summary = "Return database and actions for a service", uri="/flowutilities/serviceitems")
public class GetServiceItems {
	public static class Inputs{
		@Action.Input(name="Token", contentType=ContentType.String)
		private String token;
		@Action.Input(name="Tenant ID", contentType=ContentType.String)
		private String tenantId;
		@Action.Input(name="Service ID", contentType=ContentType.String)
		private String serviceId;
		
		public String getToken() {
			return token;
		}		

		public String getTenantId() {
			return tenantId;
		}		

		public String getServiceId() {
			return serviceId;
		}		
	}
	
	public static class Outputs {
		@Action.Output(name="Service Items", contentType=ContentType.List)
		private List<ServiceItem> services;
		
		public Outputs(List<ServiceItem> services)
		{
			this.services=services;
		}

		public List<ServiceItem> getServiceItems() throws IOException {
			return services;
		}
	}
}
