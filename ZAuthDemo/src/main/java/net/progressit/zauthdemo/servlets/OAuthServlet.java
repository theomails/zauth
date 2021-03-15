package net.progressit.zauthdemo.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.progressit.zauth.ZAuth;
import net.progressit.zauth.ZAuth.TokenRequestAccessType;
import net.progressit.zauth.ZAuth.TokenRequestGrantType;
import net.progressit.zauth.ZAuth.TokenResponseField;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OAuthServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String clientTypeClientId="1000.IG68G0QV4OX34X7P7WUEHQLPSRJ9CU";
	public static final String clientTypeClientSecret="efdc495c9da4d15999cb9bdafe0dab20beb36cf1eb";
	public static final String clientTypeScope="AaaServer.profile.Read";
	public static final String clientTypeRedirectUri="https://zauthdemo.appspot.com";
	//server
	public static final String serverTypeClientId="1000.CZDWPEVKC56LG7DS5YHZTTSD7WS5KC";
	public static final String serverTypeClientSecret = "dfff0ec240bb57eaf2f7e65445384efa2d8ff43cf7";
	public static final String serverTypeScope = "AaaServer.profile.Read";
	public static final String serverTypeRedirectUri = "https://zauthdemo.appspot.com/oauthcallback/serverflow";
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
		String completeURL = getCompleteUrl(request);
		HttpUrl url = HttpUrl.parse(completeURL);
		List<String> pathSegments = url.pathSegments();
		String clientOrServerFlow = pathSegments.get( pathSegments.size()-1 );
		
		if("serverflow".equals(clientOrServerFlow)) {
			String code = url.queryParameter("code");
			HttpSession session = request.getSession(true);
			session.setAttribute("server:code", code);
			ZAuth.TokenResponse tokenResponse = getFirstTokens(code);
			session.setAttribute("server:refresh_token", tokenResponse.get(TokenResponseField.refresh_token));
			session.setAttribute("server:access_token", tokenResponse.get(TokenResponseField.access_token));
			request.setAttribute("timedMessage", "You can find the authorization-grant <b>code</b> in the URL. "
					+ "We are catching it from the URL. We will be using it in the next call only. "
					+ "Also storing it in the http session just for display."
					+ "\nCode: " + code 
					+ "We used the above code to get a refresh token (for permanent use) and also the first access token. "
					+ "We are caching the tokens in the http session."
					+ "\nRefresh Token: " + tokenResponse.get(TokenResponseField.refresh_token)
					+ "\nAccess Token: " + tokenResponse.get(TokenResponseField.access_token));
			try {
				request.getRequestDispatcher("/ShowTimedMessage.jsp").forward(request, response);
			} catch (ServletException | IOException e) {
				throw new RuntimeException(e);
			}
		}else {
			System.err.println( completeURL );
		}
    }
	
	private String getCompleteUrl(HttpServletRequest request) {
		StringBuffer requestURL = request.getRequestURL();
		if (request.getQueryString() != null) {
		    requestURL.append("?").append(request.getQueryString());
		}
		String completeURL = requestURL.toString();
		return completeURL;
	}
	
	private ZAuth.TokenResponse getFirstTokens(String code) {
		ZAuth.TokenRequestPost tokenRequest = ZAuth.TokenRequestPost.builder()//
				.clientId(serverTypeClientId)//
				.clientSecret(serverTypeClientSecret)//
				.code(code)//
				.scopesCsv(serverTypeScope)//
				.redirectUri(serverTypeRedirectUri)//
				.grantType(TokenRequestGrantType.authorization_code)
				//.accessType(TokenRequestAccessType.offline)
				.state("101")
				.build();
		
		HttpUrl tokenRequestUrl = tokenRequest.buildUrl();
		try {
			String tokenResponseStr = callPost(tokenRequestUrl);
			ZAuth.TokenResponse tokenResponse = new ZAuth.TokenResponse(tokenResponseStr);
			return tokenResponse;
		}catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private OkHttpClient client = new OkHttpClient();
	private OkHttpClient getClient() {
		return client;
	}
	public String callPost(HttpUrl url) throws IOException {
		Request request = new Request.Builder().post(new FormBody.Builder().build()).url( url.url() ).build();
		Response response = getClient().newCall(request).execute();
		String sResponse = response.body().string();
		System.out.println(sResponse);
		return sResponse;
	}
}
