package com.boomi.services.flowutilityservice.getvalues;
import java.io.IOException;
import java.util.List;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Get Values", summary = "Return values for tenant", uri="/flowutilities/values")
public class GetValues {
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
		@Action.Output(name="Values", contentType=ContentType.List)
		private List<Value> values;
		
		public Outputs(List<Value> values)
		{
			this.values=values;
		}

		public List<Value> getValues() throws IOException {
			return values;
		}
	}
}
