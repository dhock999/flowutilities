package com.boomi.services.flowutilityservice.gettenants;
import java.io.IOException;
import java.util.List;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Get Tenants", summary = "Return tenants for user", uri="/flowutilities/tenants")
public class GetTenants {
	public static class Inputs{
		@Action.Input(name="Token", contentType=ContentType.String)
		private String token;
		public String getToken() {
			return token;
		}		
	}
	
	public static class Outputs {
		@Action.Output(name="Tenants", contentType=ContentType.List)
		private List<Tenant> tenants;
		
		public Outputs(List<Tenant> tenants)
		{
			this.tenants=tenants;
		}

		public List<Tenant> getTenants() throws IOException {
			System.out.println("tenants" + tenants.size());
			return tenants;
		}
	}
}
