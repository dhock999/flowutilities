package com.boomi.services.flowutilityservice.gettypes;
import java.io.IOException;
import java.util.List;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Get Types", summary = "Return types for tenant", uri="/flowutilities/types")
public class GetTypes {
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
		@Action.Output(name="Types", contentType=ContentType.List)
		private List<Type> types;
		
		public Outputs(List<Type> types)
		{
			this.types=types;
		}

		public List<Type> getTypes() throws IOException {
			return types;
		}
	}
}
