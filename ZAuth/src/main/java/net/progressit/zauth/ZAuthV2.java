package net.progressit.zauth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import lombok.Builder;
import lombok.Data;
import okhttp3.HttpUrl;

public class ZAuthV2 {
	public enum ResponseType { token, code }
	public enum AccessType { Online, Offline }
	public enum Prompt { Consent }
	public enum GrantType { authorization_code, refresh_token }
	
	/**
	 * We support generation of even the Client-only flows in this Java code, because, it is useful to build demos and instructions.
	 * 
	 * @author theomails
	 *
	 */
	@Data
	@Builder
	public static class AuthUrl{
		private String clientId;
		private ResponseType responseType;
		private String scopesCsv;
		private String redirectUri;
		private String state;
		private AccessType accessType;
		private Prompt prompt;
		public HttpUrl makeAuthUrl() {
			return makeAuthUrl(false);
		}
		public HttpUrl makeAuthRefreshUrl() {
			return makeAuthUrl(true);
		}
		private HttpUrl makeAuthUrl(boolean refresh) {
			List<String> missingMandatory = new ArrayList<>();
			if(clientId==null || clientId.trim().length()==0) missingMandatory.add("Client ID");
			if(responseType==null) missingMandatory.add("Response Type");
			if(scopesCsv==null || scopesCsv.trim().length()==0) missingMandatory.add("Scope");
			if(redirectUri==null || redirectUri.trim().length()==0) missingMandatory.add("Redirect URI");
			if(missingMandatory.size()>0) {
				throw new RuntimeException("Some mandatory fields are missing: " + missingMandatory);
			}
			
			//Base
			StringBuilder base = new StringBuilder(100);
			base.append("https://accounts.zoho.com/oauth/v2/auth");
			if(refresh) base.append("/refresh");
			
			HttpUrl baseUrl = HttpUrl.parse(base.toString());
			HttpUrl.Builder urlBuilder = baseUrl.newBuilder();
			urlBuilder.addQueryParameter("client_id", clientId);
			urlBuilder.addQueryParameter("response_type", responseType.name());
			urlBuilder.addQueryParameter("redirect_uri", redirectUri);
			if(scopesCsv!=null) {
				urlBuilder.addQueryParameter("scope", scopesCsv);
			}
			if(accessType!=null) {
				urlBuilder.addQueryParameter("access_type", accessType.name());
			}
			if(state!=null) {
				urlBuilder.addQueryParameter("state", state);
			}
			if(prompt!=null) {
				urlBuilder.addQueryParameter("prompt", prompt.name());
			}
			HttpUrl url = urlBuilder.build();
			return url;
		}
	}
	
	/**
	 * 
	 * @author theomails
	 *
	 */
	@Data
	@Builder
	public static class TokenUrl{
		private String clientId;
		private String clientSecret;
		private GrantType grantType;
		private String code;
		private String refreshToken;
		public String makeTokenUrl() {
			List<String> missingMandatory = new ArrayList<>();
			StringBuilder res = new StringBuilder(200);
			if(clientId==null || clientId.trim().length()==0) missingMandatory.add("Client ID");
			if(clientSecret==null || clientSecret.trim().length()==0) missingMandatory.add("Client Secret");
			if(grantType==null) missingMandatory.add("Grant Type");
			
			if(grantType==GrantType.authorization_code && (code==null||code.trim().isEmpty())) missingMandatory.add("Grant: Code");
			if(grantType==GrantType.refresh_token && (refreshToken==null||refreshToken.trim().isEmpty())) missingMandatory.add("Grant: Refresh Token");
			if(missingMandatory.size()>0) {
				throw new RuntimeException("Some mandatory fields are missing: " + missingMandatory);
			}
			
			//Base
			res.append("https://accounts.zoho.com/oauth/v2/token?");
			
			//Mandatory
			res.append("client_id=").append(clientId);
			res.append("&clientSecret=").append(clientSecret);
			res.append("&grantType=").append(grantType);
			
			//Conditional mandatory
			if(grantType==GrantType.authorization_code) res.append("&code=").append(code);
			else if(grantType==GrantType.refresh_token) res.append("&refreshToken=").append(refreshToken);
			else throw new RuntimeException("Unhandled GrantType: " + grantType);
			
			return res.toString();
		}

	}
	
	public enum AuthResponseField{ code, location, access_token, expires_in }
	public static class AuthResponse implements Serializable{
		private static final long serialVersionUID = 1L;
		private final HttpUrl url;
		public AuthResponse(String redirectedUrl) {
			this.url = HttpUrl.parse(redirectedUrl);
		}
		public String get(AuthResponseField field) {
			return url.queryParameter(field.name());
		}
	}
	
	public enum TokenResponseField{ access_token, refresh_token, api_domain, token_type, expires_in }
	public static class TokenResponse implements Serializable{
		private static final long serialVersionUID = 1L;
		private final Map<String, Object> tokenResponse;
		@SuppressWarnings("unchecked")
		public TokenResponse(String responseJsonString) {
			this.tokenResponse = new Gson().fromJson(responseJsonString, Map.class);
		}
		public String get(TokenResponseField field) {
			return (String) tokenResponse.get(field.name());
		}
		public Double getDouble(TokenResponseField field) {
			return (Double) tokenResponse.get(field.name());
		}
	}
}
