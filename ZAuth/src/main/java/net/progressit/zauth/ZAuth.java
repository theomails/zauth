package net.progressit.zauth;

import java.util.Map;

import com.google.gson.Gson;

import lombok.Builder;
import lombok.Data;
import okhttp3.HttpUrl;

public class ZAuth {
	//AUTH
	public enum AuthRequestResponseType{ code }
	public enum AuthRequestAccessType { offline, online }
	public enum AuthRequestPrompt{ offline, consent }
	@Data
	@Builder
	public static class AuthRequestGet{
		private static final String BASE_URL = "https://accounts.zoho.com/oauth/v2/auth";
		private final String clientId;
		private final AuthRequestResponseType responseType;
		private final String scopesCsv;
		private final String redirectUri;
		private AuthRequestAccessType accessType;
		private AuthRequestPrompt prompt;
		public HttpUrl buildUrl() {
			HttpUrl baseUrl = HttpUrl.parse(BASE_URL);
			HttpUrl.Builder urlBuilder = baseUrl.newBuilder();
			urlBuilder.addEncodedQueryParameter("client_id", clientId);
			urlBuilder.addEncodedQueryParameter("response_type", responseType.name());
			urlBuilder.addEncodedQueryParameter("scope", scopesCsv);
			urlBuilder.addEncodedQueryParameter("redirect_uri", redirectUri);
			if(accessType!=null) {
				urlBuilder.addEncodedQueryParameter("access_type", accessType.name());
			}
			if(prompt!=null) {
				urlBuilder.addEncodedQueryParameter("prompt", prompt.name());
			}
			HttpUrl url = urlBuilder.build();
			return url;
		}
	}
	public enum AuthResponseField{ code, location, access_token, expires_in }
	public static class AuthResponse{
		private final HttpUrl url;
		public AuthResponse(String redirectedUrl) {
			this.url = HttpUrl.parse(redirectedUrl);
		}
		public String get(AuthResponseField field) {
			return url.queryParameter(field.name());
		}
	}
	
	//ACCESS
	public enum TokenRequestGrantType{ authorization_code, refresh_token }
	@Data
	@Builder
	public static class TokenRequestPost{
		private static final String BASE_URL = "https://accounts.zoho.com/oauth/v2/token";
		private final String clientId;
		private final TokenRequestGrantType grantType;
		private final String clientSecret;
		private final String redirectUri;
		private String refreshToken;
		private String code;
		public HttpUrl buildUrl() {
			HttpUrl baseUrl = HttpUrl.parse(BASE_URL);
			HttpUrl.Builder urlBuilder = baseUrl.newBuilder();
			urlBuilder.addEncodedQueryParameter("client_id", clientId);
			urlBuilder.addEncodedQueryParameter("grant_type", grantType.name());
			urlBuilder.addEncodedQueryParameter("client_secret", clientSecret);
			urlBuilder.addEncodedQueryParameter("redirect_uri", redirectUri);
			if(refreshToken!=null) {
				urlBuilder.addEncodedQueryParameter("refresh_token", refreshToken);
			}
			if(code!=null) {
				urlBuilder.addEncodedQueryParameter("code", code);
			}
			HttpUrl url = urlBuilder.build();
			return url;
		}
	}
	public enum TokenResponseField{ access_token, refresh_token, api_domain, token_type, expires_in }
	public static class TokenResponse{
		private final Map<String, Object> tokenResponse;
		@SuppressWarnings("unchecked")
		public TokenResponse(String responseJsonString) {
			this.tokenResponse = (Map<String, Object>) new Gson().fromJson(responseJsonString, Map.class);
		}
		public String get(TokenResponseField field) {
			return (String) tokenResponse.get(field.name());
		}
		public Double getDouble(TokenResponseField field) {
			return (Double) tokenResponse.get(field.name());
		}
	}
	
	//AUTH REFRESH
	public enum AuthRefreshRequestResponseType{ token }
	public enum AuthRefreshRequestAccessType { offline, online }
	public enum AuthRefreshRequestPrompt{ offline, consent }
	@Data
	@Builder
	public static class AuthRefreshRequestGet{
		private static final String BASE_URL = "https://accounts.zoho.com/oauth/v2/auth/refresh";
		private final String clientId;
		private final AuthRefreshRequestResponseType responseType;
		private final String scopesCsv;
		private final String redirectUri;
		private String state;
		private AuthRefreshRequestAccessType accessType;
		private AuthRefreshRequestPrompt prompt;
		public HttpUrl buildUrl() {
			HttpUrl baseUrl = HttpUrl.parse(BASE_URL);
			HttpUrl.Builder urlBuilder = baseUrl.newBuilder();
			urlBuilder.addEncodedQueryParameter("client_id", clientId);
			urlBuilder.addEncodedQueryParameter("response_type", responseType.name());
			urlBuilder.addEncodedQueryParameter("scope", scopesCsv);
			urlBuilder.addEncodedQueryParameter("redirect_uri", redirectUri);
			if(accessType!=null) {
				urlBuilder.addEncodedQueryParameter("access_type", accessType.name());
			}
			if(prompt!=null) {
				urlBuilder.addEncodedQueryParameter("prompt", prompt.name());
			}
			HttpUrl url = urlBuilder.build();
			return url;
		}
	}
	public enum AuthRefreshResponseErrors{ client_not_granted, prompt_required  }
	public enum AuthRefreshResponseField{ location, access_token, expires_in }
	public static class AuthRefreshResponse{
		private final HttpUrl url;
		public AuthRefreshResponse(String redirectedUrl) {
			this.url = HttpUrl.parse(redirectedUrl);
		}
		public String get(AuthResponseField field) {
			return url.queryParameter(field.name());
		}
	}
	
	//TOKEN REVOKE
	//AUTH REFRESH
	@Data
	@Builder
	public static class TokenRevokeRequestGet{
		private static final String BASE_URL = "https://accounts.zoho.com/oauth/v2/token/revoke";
		private final String token;
		public HttpUrl buildUrl() {
			HttpUrl baseUrl = HttpUrl.parse(BASE_URL);
			HttpUrl.Builder urlBuilder = baseUrl.newBuilder();
			urlBuilder.addEncodedQueryParameter("token", token);
			HttpUrl url = urlBuilder.build();
			return url;
		}
	}
}
