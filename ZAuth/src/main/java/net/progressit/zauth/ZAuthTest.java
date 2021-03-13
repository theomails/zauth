package net.progressit.zauth;

import net.progressit.zauth.ZAuth.AuthRequestPrompt;
import net.progressit.zauth.ZAuth.AuthRequestResponseType;
import net.progressit.zauth.ZAuth.AuthResponseField;
import net.progressit.zauth.ZAuth.TokenRequestGrantType;
import net.progressit.zauth.ZAuth.TokenResponseField;

public class ZAuthTest {
	public static void main(String[] args) {
		//Server-client samples.
		//1. Auth request - Make a URL and redirect there.
		{
		ZAuth.AuthRequestGet authGet= ZAuth.AuthRequestGet.builder()//
				.responseType(AuthRequestResponseType.code)//
				.clientId("1000.GMB0YULZHJK411284S8I5GZ4CHUEX0")//
				.scopesCsv("AaaServer.profile.Read")//
				.redirectUri("https://www.zylker.com/oauthredirect")//
				.prompt(AuthRequestPrompt.consent)//
				.build();
		//Add access_type=offline to get refresh token
		//Add both access_type=offline and prompt=consent to get another refresh token (2nd time onwards)
		System.out.println( authGet.buildUrl() );
		}
		
		//2. Auth response - Zoho will redirect back to our app. Catch the URL and parse it.
		{
		ZAuth.AuthResponse authResp = new ZAuth.AuthResponse("https://www.zylker.com/oauthredirect?"
				+ "code=1000.9c3a2a6a5362125efc9f7666224313b6.d44f4b5b63e71fc682cdf20c771efead"
				+ "&location=us");
		System.out.println(authResp.get(AuthResponseField.code));
		System.out.println(authResp.get(AuthResponseField.location));
		}
		
		//3. Token request (get refresh token) - Make a POST call.
		{
		ZAuth.TokenRequestPost tokenPost = ZAuth.TokenRequestPost.builder()//
				.clientId("1000.GMB0YULZHJK411284S8I5GZ4CHUEX0")//
				.grantType(TokenRequestGrantType.authorization_code)
				.clientSecret("122c324d3496d5d777ceeebc129470715fbb856b7")
				.redirectUri("https://www.zylker.com/oauthredirect")
				.code("1000.86a03ca5dbfccb7445b1889b8215efb0.cad9e1ae4989a1196fe05aa729fcb4e1")
				.build();
		System.out.println( tokenPost.buildUrl() );
		}
		
		//4. Token response - Zoho will return JSON response.
		{
		ZAuth.TokenResponse tokenResp = new ZAuth.TokenResponse("{\r\n"
				+ "    \"access_token\":\"1000.2deaf8d0c268e3c85daa2a013a843b10.703adef2bb337b 8ca36cfc5d7b83cf24\",\r\n"
				+ "    \"refresh_token\":\"1000.18e983526f0ca8575ea9c53b0cd5bb58.1bd83a6f2e22c3a7e1309d96ae439cc1\",\r\n"
				+ "    \"api_domain\":\"https://api.zoho.com\",\r\n"
				+ "    \"token_type\":\"Bearer\",\r\n"
				+ "    \"expires_in\":3600\r\n"
				+ "}");
		System.out.println(tokenResp.get(TokenResponseField.access_token));
		System.out.println(tokenResp.get(TokenResponseField.refresh_token));
		System.out.println(tokenResp.get(TokenResponseField.api_domain));
		System.out.println(tokenResp.get(TokenResponseField.token_type));
		System.out.println(tokenResp.getDouble(TokenResponseField.expires_in));
		}
		
		//5. Token request (use refresh token) - Make a POST call.
		{
		ZAuth.TokenRequestPost tokenPost2 = ZAuth.TokenRequestPost.builder()//
				.clientId("1000.GMB0YULZHJK411284S8I5GZ4CHUEX0")//
				.grantType(TokenRequestGrantType.refresh_token)
				.clientSecret("122c324d3496d5d777ceeebc129470715fbb856b7")
				.redirectUri("https://www.zylker.com/oauthredirect")
				.refreshToken("1000.18e983526f0ca8575ea9c53b0cd5bb58.1bd83a6f2e22c3a7e1309d96ae439cc1")
				.build();
		System.out.println( tokenPost2.buildUrl() );
		}
		
		//6. Token response - Zoho will return JSON response.
		{
		ZAuth.TokenResponse tokenResp2 = new ZAuth.TokenResponse("{\r\n"
				+ "    \"access_token\":\"1000.2deaf8d0c268e3c85daa2a013a843b10.703adef2bb337b 8ca36cfc5d7b83cf24\",\r\n"
				+ "    \"api_domain\":\"https://api.zoho.com\",\r\n"
				+ "    \"token_type\":\"Bearer\",\r\n"
				+ "    \"expires_in\":3600\r\n"
				+ "}");
		System.out.println(tokenResp2.get(TokenResponseField.access_token));
		System.out.println(tokenResp2.get(TokenResponseField.api_domain));
		System.out.println(tokenResp2.get(TokenResponseField.token_type));
		System.out.println(tokenResp2.getDouble(TokenResponseField.expires_in));
		}
	}
}
