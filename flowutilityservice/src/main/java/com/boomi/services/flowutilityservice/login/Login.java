package com.boomi.services.flowutilityservice.login;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name="Login", summary = "Login and return token", uri="/flowutilities/token")
public class Login {
	public static class Inputs{
	    @Action.Input(name = "Username", contentType = ContentType.String)
	    private String username;

	    @Action.Input(name = "Password", contentType = ContentType.Password)
	    private String password;

	    public String getUsername() {
	        return username;
	    }

	    public String getPassword() {
	        return password;
	    }
	}
	
	public static class Outputs {
		@Action.Output(name="Token", contentType=ContentType.String)
		private String token;
		public Outputs(String token)
		{
			this.token=token;
		}

		public String token() {
			return token;
		}
	}
}
