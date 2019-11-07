package com.boomi.services.flowutilityservice.copypage;
import java.io.IOException;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Copy Page", summary = "Copy the specified page", uri="/flowutilities/copypage")
public class CopyPage {
	public static class Inputs{
		@Action.Input(name="Token", contentType=ContentType.String)
		private String token;
		@Action.Input(name="Tenant ID", contentType=ContentType.String)
		private String tenantId;
		@Action.Input(name="Page ID", contentType=ContentType.String)
		private String pageId;
		
		public String getToken() {
			return token;
		}		

		public String getTenantId() {
			return tenantId;
		}	
		
		public String getPageId() {
			return pageId;
		}		
	}
	
	public static class Outputs {
		@Action.Output(name="New Page Name", contentType=ContentType.String)
		private String pageName;
		
		public Outputs(String pageName)
		{
			this.pageName=pageName;
		}

		public String getPageName() throws IOException {
			return pageName;
		}
	}
}
