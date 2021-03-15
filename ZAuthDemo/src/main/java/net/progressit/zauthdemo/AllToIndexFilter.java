//$Id$
package net.progressit.zauthdemo;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableMap;

public class AllToIndexFilter implements Filter {
	Logger logger = Logger.getLogger(AllToIndexFilter.class.getName());
	
	//BLACKLIST first
	private String[] blackStartsWith = new String[] { 
				"/sockjs" //WEBPACK debug 
			};
	private String[] passthroughStartsWith = new String[] {
				"/rest/", //REST API 
				"/index.jsp", //INDEX which is landing page of sessionz app. From there it will fork mobile/ and <web>
				"/js", //WEB js
				"/css", //WEB css
				"/img", //WEB img
				"/fonts", //WEB fonts
				"/_", //APPENGINE admin
				"/oauthcallback" //OAUTH callbacks
			};
	private Map<String, String> redirectStartsWithMapping = ImmutableMap.of("/mobile","/mobile/index.jsp");

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain fchain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		
		String redirectTo = null;
		String cpath = req.getRequestURI();
		if(matchesStartsWithArray(cpath, blackStartsWith)) {
			logger.log(Level.INFO, "bad path: [" + cpath + "] We will drop it.");
			sendMovedPermanentlyResponse(req, response);
		    return;
		}else if(matchesStartsWithArray(cpath, passthroughStartsWith)) {   ///REST Api, resources, oauth
			logger.log(Level.INFO, "special path: [" + cpath + "] We will just let it continue.");
			fchain.doFilter(request, response);
		}else if( (redirectTo = checkAndGetRedirect(cpath, redirectStartsWithMapping)) != null) {   ///REST Api
			logger.log(Level.INFO, "redirect path: [" + cpath + "] We will redirect.");
			req.getRequestDispatcher(redirectTo).forward(request, response);
		}else {
			logger.log(Level.INFO, "normal path: [" + cpath + "] We will forward to index.");
			req.getRequestDispatcher("/index.jsp").forward(request, response);
		}
	}
	
	private boolean matchesStartsWithArray(String strToCheck, String[] startsWithArray) {
		if(!strToCheck.startsWith("/")) strToCheck = "/"+strToCheck; //Ensure starts with '/'
		
		for(String s:startsWithArray) {
			if(strToCheck.startsWith(s)) {
				return true;
			}
		}
		return false;
	}
	private String checkAndGetRedirect(String strToCheck, Map<String, String> startsWithMap) {
		if(!strToCheck.startsWith("/")) strToCheck = "/"+strToCheck; //Ensure starts with '/'
		
		Set<String> keysToMatch = startsWithMap.keySet();
		for(String keyToMatch:keysToMatch) {
			if(strToCheck.startsWith(keyToMatch)) {
				return startsWithMap.get(keyToMatch);
			}
		}
		return null;
	}
	
	private void sendMovedPermanentlyResponse(HttpServletRequest req, ServletResponse response) {
		String uri = req.getScheme() + "://" +   // "http" + "://
				req.getServerName() +       // "myhost"
	             ":" +                           // ":"
	             req.getServerPort() +       // "8080"
	             req.getRequestURI();
		HttpServletResponse resp = (HttpServletResponse) response;
	    resp.reset();
	    resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
	    resp.setHeader("Location", uri);		
	}
	
	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig fconf) throws ServletException {
	}

}
