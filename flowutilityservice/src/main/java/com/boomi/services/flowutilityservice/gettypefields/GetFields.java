package com.boomi.services.flowutilityservice.gettypefields;
import java.io.IOException;
import java.util.List;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Get Properties", summary = "Return properties for the output type", uri="/flowutilities/fields")
public class GetFields {
	public static class Inputs{
		@Action.Input(name="Token", contentType=ContentType.String)
		private String token;
		@Action.Input(name="Tenant ID", contentType=ContentType.String)
		private String tenantId;		
		@Action.Input(name="Service ID", contentType=ContentType.String)
		private String serviceId;
		@Action.Input(name="Action Name", contentType=ContentType.String)
		private String actionName;
		@Action.Input(name="Type ID", contentType=ContentType.String)
		private String typeId;

		public String getToken() {
			return token;
		}		

		public String getTenantId() {
			return tenantId;
		}

		public String getServiceId() {
			return serviceId;
		}

		public String getActionName() {
			return actionName;
		}

		public String getTypeId() {
			return typeId;
		}		
	}
	
	public static class Outputs {
		@Action.Output(name="Fields", contentType=ContentType.List)
		private List<Field> fields;
		
		public Outputs(List<Field> fields)
		{
			this.fields=fields;
		}

		public List<Field> getFields() throws IOException {
			return fields;
		}
	}
}
