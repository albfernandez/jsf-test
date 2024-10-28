/**
 * 
 */
package org.jboss.test.faces.staging;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ReadListener;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;


/**
 * @author asmirnov
 * 
 */
abstract class StagingHttpRequest implements HttpServletRequest {

	private static final Logger log = ServerLogger.CONNECTION.getLogger();

	public static final String LOCALHOST = "localhost";
	public static final String HTTP = "http";
	public static final String LOCALHOST_IP = "127.0.0.1";
	public static final String UTF8 = "UTF-8";

	private String requestBody = null;

	private String contentType;

	private Map<String, Object> attributes = new HashMap<String, Object>();

	private Map<String, String> headers = new HashMap<String, String>();

	private Collection<Locale> locales = Arrays.asList(Locale.US,
			Locale.GERMANY);

	private String characterEncoding = UTF8;

	/**
	 * @return the requestBody
	 */
	String getRequestBody() {
		return requestBody;
	}

	/**
	 * @param requestBody
	 *            the requestBody to set
	 */
	void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.http.HttpServletRequest#getAuthType()
	 */
	public String getAuthType() {
		// TODO configure test auth.
		log.info("unimplemented request method getAuthType");
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.http.HttpServletRequest#getContextPath()
	 */
	public String getContextPath() {
		return StagingServletContext.CONTEXT_PATH;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jakarta.servlet.http.HttpServletRequest#getDateHeader(java.lang.String)
	 */
	public long getDateHeader(String name) {
		String value = headers.get(name);
		if(null != value){
			try {
				return DateFormat.getDateInstance(DateFormat.FULL, getLocale()).parse(value).getTime();
			} catch (ParseException e) {
				throw new IllegalArgumentException(e.getMessage());
			}
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.http.HttpServletRequest#getHeader(java.lang.String)
	 */
	public String getHeader(String name) {
		return headers.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.http.HttpServletRequest#getHeaderNames()
	 */
	@SuppressWarnings("unchecked")
	public Enumeration getHeaderNames() {
		return Collections.enumeration(headers.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.http.HttpServletRequest#getHeaders(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Enumeration getHeaders(String name) {
		Set<String> values;
		String value = headers.get(name);
		if (null != value) {
			values = Collections.singleton(value);

		} else {
			values = Collections.emptySet();
		}
		return Collections.enumeration(values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.http.HttpServletRequest#getIntHeader(java.lang.String)
	 */
	public int getIntHeader(String name) {
		String value = headers.get(name);
		if(null != value){
			return Integer.parseInt(value);
		}
		return -1;
	}

	void addHeader(String name, String value) {
		headers.put(name, value);
	}

	void addHeaders(Map<String, String> headers) {
		this.headers.putAll(headers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.http.HttpServletRequest#getPathTranslated()
	 */
	public String getPathTranslated() {
		// we have only 'virtual' server.
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.http.HttpServletRequest#getRemoteUser()
	 */
	public String getRemoteUser() {
		// TODO configure test auth.
		log.info("unimplemented request method getRemoteUser");
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.http.HttpServletRequest#getRequestURL()
	 */
	public StringBuffer getRequestURL() {
		StringBuffer requestURL = new StringBuffer(HTTP + "://" + LOCALHOST
				+ getRequestURI());
		return requestURL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.http.HttpServletRequest#getRequestedSessionId()
	 */
	public String getRequestedSessionId() {
		return StagingHttpSession.SESSION_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.http.HttpServletRequest#getUserPrincipal()
	 */
	public Principal getUserPrincipal() {
		// TODO implement test auth.
		log.info("unimplemented request method getUserPrincipal");
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jakarta.servlet.http.HttpServletRequest#isRequestedSessionIdFromCookie()
	 */
	public boolean isRequestedSessionIdFromCookie() {
		// test do not supports cookie
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.http.HttpServletRequest#isRequestedSessionIdFromURL()
	 */
	public boolean isRequestedSessionIdFromURL() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.http.HttpServletRequest#isRequestedSessionIdFromUrl()
	 */
	public boolean isRequestedSessionIdFromUrl() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.http.HttpServletRequest#isRequestedSessionIdValid()
	 */
	public boolean isRequestedSessionIdValid() {
		// TODO - check session.
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.http.HttpServletRequest#isUserInRole(java.lang.String)
	 */
	public boolean isUserInRole(String role) {
		// TODO implement test auth.
		log.info("unimplemented request method isUserInRole");
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#getAttributeNames()
	 */
	@SuppressWarnings("unchecked")
	public Enumeration getAttributeNames() {
		return Collections.enumeration(attributes.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#getCharacterEncoding()
	 */
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#getContentLength()
	 */
	public int getContentLength() {
		String body = getRequestBody();
		return null == body ? -1 : body.length();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#getContentType()
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType
	 *            the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#getInputStream()
	 */
	public ServletInputStream getInputStream() throws IOException {
		String body = getRequestBody();
		if(null != body){
			final ByteArrayInputStream input = new ByteArrayInputStream(body.getBytes(getCharacterEncoding()));
			return new ServletInputStream(){

				@Override
				public int read() throws IOException {
					// TODO Auto-generated method stub
					return input.read();
				}

				@Override
				public boolean isFinished() {
					//MZ
					return input.available() == 0;
				}

				@Override
				public boolean isReady() {
					//MZ
					return true;
				}

				@Override
				public void setReadListener(ReadListener readListener) {
					//MZ
					throw new NotImplementedException();
				}
				
			};
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#getLocalAddr()
	 */
	public String getLocalAddr() {
		return LOCALHOST_IP;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#getLocalName()
	 */
	public String getLocalName() {
		return LOCALHOST;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#getLocalPort()
	 */
	public int getLocalPort() {
		return 80;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#getLocale()
	 */
	public Locale getLocale() {
		return Locale.US;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#getLocales()
	 */
	@SuppressWarnings("unchecked")
	public Enumeration getLocales() {
		return Collections.enumeration(locales);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#getProtocol()
	 */
	public String getProtocol() {
		return "HTTP/1.1";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#getReader()
	 */
	public BufferedReader getReader() throws IOException {
		String body = getRequestBody();
		if(null != body){
			return new BufferedReader(new StringReader(body));
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#getRealPath(java.lang.String)
	 */
	public String getRealPath(String path) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#getRemoteAddr()
	 */
	public String getRemoteAddr() {
		return LOCALHOST_IP;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#getRemoteHost()
	 */
	public String getRemoteHost() {
		return LOCALHOST;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#getRemotePort()
	 */
	public int getRemotePort() {
		return 1223340;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#getRequestDispatcher(java.lang.String)
	 */
	public RequestDispatcher getRequestDispatcher(String path) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
		// return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#getScheme()
	 */
	public String getScheme() {
		return HTTP;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#getServerName()
	 */
	public String getServerName() {
		return LOCALHOST;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#getServerPort()
	 */
	public int getServerPort() {
		return 80;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#isSecure()
	 */
	public boolean isSecure() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name) {
		// TODO - inform listeners
		Object removed = attributes.remove(name);
		if (null != removed) {
			attributeRemoved(name, removed);
		}
	}

	protected abstract void attributeRemoved(String name, Object removed);

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#setAttribute(java.lang.String,
	 * java.lang.Object)
	 */
	public void setAttribute(String name, Object o) {
		if (null == o) {
			removeAttribute(name);
		} else {
			Object oldValue = attributes.put(name, o);
			if (null != oldValue) {
				attributeReplaced(name, o);
			} else {
				attributeAdded(name, o);
			}
		}

	}

	protected abstract void attributeAdded(String name, Object o);

	protected abstract void attributeReplaced(String name, Object o);

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletRequest#setCharacterEncoding(java.lang.String)
	 */
	public void setCharacterEncoding(String env)
			throws UnsupportedEncodingException {
		if (!Charset.isSupported(env)) {
			throw new UnsupportedEncodingException("Unknown charset "+env);
		}
		this.characterEncoding = env;

	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletRequest#getAsyncContext()
	 */
	public AsyncContext getAsyncContext() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletRequest#getDispatcherType()
	 */
	public DispatcherType getDispatcherType() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletRequest#getParameter(java.lang.String)
	 */
	public String getParameter(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletRequest#getParameterMap()
	 */
	public Map<String, String[]> getParameterMap() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletRequest#getParameterNames()
	 */
	public Enumeration<String> getParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletRequest#getParameterValues(java.lang.String)
	 */
	public String[] getParameterValues(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletRequest#getServletContext()
	 */
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletRequest#isAsyncStarted()
	 */
	public boolean isAsyncStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletRequest#isAsyncSupported()
	 */
	public boolean isAsyncSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletRequest#startAsync()
	 */
	public AsyncContext startAsync() throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletRequest#startAsync(jakarta.servlet.ServletRequest, jakarta.servlet.ServletResponse)
	 */
	public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1)
			throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.http.HttpServletRequest#authenticate(jakarta.servlet.http.HttpServletResponse)
	 */
	public boolean authenticate(HttpServletResponse arg0) throws IOException,
			ServletException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.http.HttpServletRequest#getPart(java.lang.String)
	 */
	public Part getPart(String arg0) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.http.HttpServletRequest#getParts()
	 */
	public Collection<Part> getParts() throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.http.HttpServletRequest#getSession(boolean)
	 */
	public HttpSession getSession(boolean arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.http.HttpServletRequest#login(java.lang.String, java.lang.String)
	 */
	public void login(String arg0, String arg1) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.http.HttpServletRequest#logout()
	 */
	public void logout() throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
