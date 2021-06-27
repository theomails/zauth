package net.progressit.zauth;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.progressit.zauth.ZAuth.AuthRequestPrompt;
import net.progressit.zauth.ZAuth.AuthRequestResponseType;
import net.progressit.zauth.ZAuth.AuthResponseField;
import net.progressit.zauth.ZAuth.TokenRequestGrantType;
import net.progressit.zauth.ZAuth.TokenResponseField;

@Deprecated
public class ZAuthTest {

	@Test
	public void testAuthRequestGet() {
		// Server-client samples.
		// 1. Auth request - Make a URL and redirect there.
		ZAuth.AuthRequestGet authGet = ZAuth.AuthRequestGet.builder()//
				.responseType(AuthRequestResponseType.code)//
				.clientId("1000.GMB0YULZHJK411284S8I5GZ4CHUEX0")//
				.scopesCsv("AaaServer.profile.Read")//
				.redirectUri("https://www.zylker.com/oauthredirect")//
				.prompt(AuthRequestPrompt.consent)//
				.build();
		// Add access_type=offline to get refresh token
		// Add both access_type=offline and prompt=consent to get another refresh token
		// (2nd time onwards)
		String expected = "https://accounts.zoho.com/oauth/v2/auth?"
				+ "client_id=1000.GMB0YULZHJK411284S8I5GZ4CHUEX0&response_type=code&scope=AaaServer.profile.Read"
				+ "&redirect_uri=https%3A%2F%2Fwww.zylker.com%2Foauthredirect&prompt=consent";
		System.out.println("testAuthRequestGet");
		System.out.println( expected );
		System.out.println( authGet.buildUrl() );
		assertEquals("URL Should match", expected, authGet.buildUrl().toString());
	}

	@Test
	public void testAuthResp() {
		// 2. Auth response - Zoho will redirect back to our app. Catch the URL and
		// parse it.
		ZAuth.AuthResponse authResp = new ZAuth.AuthResponse("https://www.zylker.com/oauthredirect?"
				+ "code=1000.9c3a2a6a5362125efc9f7666224313b6.d44f4b5b63e71fc682cdf20c771efead" + "&location=us");
		assertEquals("code Should match","1000.9c3a2a6a5362125efc9f7666224313b6.d44f4b5b63e71fc682cdf20c771efead",authResp.get(AuthResponseField.code));
		assertEquals("location Should match","us",authResp.get(AuthResponseField.location));
	}

	@Test
	public void testTokenPost() {
		// 3. Token request (get refresh token) - Make a POST call.
		ZAuth.TokenRequestPost tokenPost = ZAuth.TokenRequestPost.builder()//
				.clientId("1000.GMB0YULZHJK411284S8I5GZ4CHUEX0")//
				.grantType(TokenRequestGrantType.authorization_code)
				.clientSecret("122c324d3496d5d777ceeebc129470715fbb856b7")
				.redirectUri("https://www.zylker.com/oauthredirect")
				.code("1000.86a03ca5dbfccb7445b1889b8215efb0.cad9e1ae4989a1196fe05aa729fcb4e1").build();
		String expected = "https://accounts.zoho.com/oauth/v2/token?"
				+ "client_id=1000.GMB0YULZHJK411284S8I5GZ4CHUEX0&grant_type=authorization_code&client_secret=122c324d3496d5d777ceeebc129470715fbb856b7"
				+ "&redirect_uri=https%3A%2F%2Fwww.zylker.com%2Foauthredirect&code=1000.86a03ca5dbfccb7445b1889b8215efb0.cad9e1ae4989a1196fe05aa729fcb4e1";
		System.out.println("testTokenPost");
		System.out.println( expected );
		System.out.println( tokenPost.buildUrl() );
		assertEquals("URL Should match", expected, tokenPost.buildUrl().toString());
	}

	@Test
	public void testTokenResp() {
		// 4. Token response - Zoho will return JSON response.
		ZAuth.TokenResponse tokenResp = new ZAuth.TokenResponse("{\r\n"
				+ "    \"access_token\":\"1000.2deaf8d0c268e3c85daa2a013a843b10.703adef2bb337b 8ca36cfc5d7b83cf24\",\r\n"
				+ "    \"refresh_token\":\"1000.18e983526f0ca8575ea9c53b0cd5bb58.1bd83a6f2e22c3a7e1309d96ae439cc1\",\r\n"
				+ "    \"api_domain\":\"https://api.zoho.com\",\r\n" + "    \"token_type\":\"Bearer\",\r\n"
				+ "    \"expires_in\":3600\r\n" + "}");
		assertEquals("access_token Should match","1000.2deaf8d0c268e3c85daa2a013a843b10.703adef2bb337b 8ca36cfc5d7b83cf24",tokenResp.get(TokenResponseField.access_token));
		assertEquals("refresh_token Should match","1000.18e983526f0ca8575ea9c53b0cd5bb58.1bd83a6f2e22c3a7e1309d96ae439cc1",tokenResp.get(TokenResponseField.refresh_token));
		assertEquals("api_domain Should match","https://api.zoho.com",tokenResp.get(TokenResponseField.api_domain));
		assertEquals("token_type Should match","Bearer",tokenResp.get(TokenResponseField.token_type));
		assertEquals("expires_in Should match",(Double) 3600d,tokenResp.getDouble(TokenResponseField.expires_in));
	}

	@Test
	public void testTokenPostRefresh() {
		// 5. Token request (use refresh token) - Make a POST call.
		ZAuth.TokenRequestPost tokenPost2 = ZAuth.TokenRequestPost.builder()//
				.clientId("1000.GMB0YULZHJK411284S8I5GZ4CHUEX0")//
				.grantType(TokenRequestGrantType.refresh_token)
				.clientSecret("122c324d3496d5d777ceeebc129470715fbb856b7")
				.redirectUri("https://www.zylker.com/oauthredirect")
				.refreshToken("1000.18e983526f0ca8575ea9c53b0cd5bb58.1bd83a6f2e22c3a7e1309d96ae439cc1").build();
		String expected = "https://accounts.zoho.com/oauth/v2/token?"
				+ "client_id=1000.GMB0YULZHJK411284S8I5GZ4CHUEX0&grant_type=refresh_token&client_secret=122c324d3496d5d777ceeebc129470715fbb856b7"
				+ "&redirect_uri=https%3A%2F%2Fwww.zylker.com%2Foauthredirect&refresh_token=1000.18e983526f0ca8575ea9c53b0cd5bb58.1bd83a6f2e22c3a7e1309d96ae439cc1";
		System.out.println("testTokenPostRefresh");
		System.out.println( expected );
		System.out.println( tokenPost2.buildUrl() );
		assertEquals("URL Should match", expected, tokenPost2.buildUrl().toString());
	}

	@Test
	public void testTokenRefreshResponse() {
		// 6. Token response - Zoho will return JSON response.
		ZAuth.TokenResponse tokenResp2 = new ZAuth.TokenResponse("{\r\n"
				+ "    \"access_token\":\"1000.2deaf8d0c268e3c85daa2a013a843b10.703adef2bb337b 8ca36cfc5d7b83cf24\",\r\n"
				+ "    \"api_domain\":\"https://api.zoho.com\",\r\n" + "    \"token_type\":\"Bearer\",\r\n"
				+ "    \"expires_in\":3600\r\n" + "}");
		assertEquals("access_token Should match","1000.2deaf8d0c268e3c85daa2a013a843b10.703adef2bb337b 8ca36cfc5d7b83cf24",tokenResp2.get(TokenResponseField.access_token));
		assertEquals("api_domain Should match","https://api.zoho.com",tokenResp2.get(TokenResponseField.api_domain));
		assertEquals("token_type Should match","Bearer",tokenResp2.get(TokenResponseField.token_type));
		assertEquals("expires_in Should match", (Double) 3600d,tokenResp2.getDouble(TokenResponseField.expires_in));
	}
}
