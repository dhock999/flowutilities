package com.boomi.services.flowutilityservice.copyvalue;
import java.io.IOException;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Copy Value", summary = "Copy the specified value", uri="/flowutilities/copyvalue")
public class CopyValue {
	public static class Inputs{
		@Action.Input(name="Token", contentType=ContentType.String)
		private String token;
		@Action.Input(name="Tenant ID", contentType=ContentType.String)
		private String tenantId;
		@Action.Input(name="Value ID", contentType=ContentType.String)
		private String valueId;
		
		public String getToken() {
			return token;
		}		

		public String getTenantId() {
			return tenantId;
		}	
		
		public String getValueId() {
			return valueId;
		}		
	}
	
	public static class Outputs {
		@Action.Output(name="New Value Name", contentType=ContentType.String)
		private String valueName;
		
		public Outputs(String valueName)
		{
			this.valueName=valueName;
		}

		public String getValueName() throws IOException {
			return valueName;
		}
	}
}
