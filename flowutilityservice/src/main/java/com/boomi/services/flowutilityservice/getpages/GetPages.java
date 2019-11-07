package com.boomi.services.flowutilityservice.getpages;
import java.io.IOException;
import java.util.List;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Get Pages", summary = "Return pages for tenant", uri="/flowutilities/pages")
public class GetPages {
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
		@Action.Output(name="Pages", contentType=ContentType.List)
		private List<Page> pages;
		
		public Outputs(List<Page> pages)
		{
			this.pages=pages;
		}

		public List<Page> getPages() throws IOException {
			return pages;
		}
	}
}
