package com.boomi.services.flowutilityservice.copytype;
import java.io.IOException;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Copy Type", summary = "Copy the specified type", uri="/flowutilities/copytype")
public class CopyType {
	public static class Inputs{
		@Action.Input(name="Token", contentType=ContentType.String)
		private String token;
		@Action.Input(name="Tenant ID", contentType=ContentType.String)
		private String tenantId;
		@Action.Input(name="Type ID", contentType=ContentType.String)
		private String typeId;
		
		public String getToken() {
			return token;
		}		

		public String getTenantId() {
			return tenantId;
		}	
		
		public String getTypeId() {
			return typeId;
		}		
	}
	
	public static class Outputs {
		@Action.Output(name="New Type Name", contentType=ContentType.String)
		private String typeName;
		
		public Outputs(String typeName)
		{
			this.typeName=typeName;
		}

		public String getTypeName() throws IOException {
			return typeName;
		}
	}
}
