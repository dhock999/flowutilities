package com.boomi.services.flowutilityservice.createflow;
import java.io.IOException;
import java.util.List;

import com.boomi.services.flowutilityservice.gettypefields.Field;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Create Flow", summary = "Create a Flow for the specified service/action", uri="/flowutilities/createflow")
public class CreateFlow {
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
		@Action.Input(name="Option", contentType=ContentType.String)
		private String option;
		@Action.Input(name="Chart XAxis", contentType=ContentType.String)
		private String xAxis;
		@Action.Input(name="Chart Type", contentType=ContentType.String)
		private String chartType;
		@Action.Input(name="Flow ID", contentType=ContentType.String)
		private String flowId;
		@Action.Input(name="Selected Fields", contentType=ContentType.List)
		private List<Field> fields;
		@Action.Input(name="File Upload Schema", contentType=ContentType.String)
		private String fileUploadSchema;

		
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

		public String getOption() {
			return option;
		}

		public String getxAxis() {
			return xAxis;
		}

		public String getFlowId() {
			return flowId;
		}	
		
		public List<Field> getFields() {
			return fields;
		}

		public String getChartType() {
			return chartType;
		}

		public String getFileUploadSchema() {
			return fileUploadSchema;
		}			
	}
	
	public static class Outputs {
		@Action.Output(name="Information", contentType=ContentType.String)
		private String info;
		
		public Outputs(String info)
		{
			this.info=info;
		}

		public String getInfo() throws IOException {
			return info;
		}
	}
}
