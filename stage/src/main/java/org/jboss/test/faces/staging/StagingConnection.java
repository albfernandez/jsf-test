/**
 * 
 */
package org.jboss.test.faces.staging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jboss.test.faces.TestException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;

/**
 * This class represent single connection ( request ) to the virtual server. These instance should not be created directly, but by the {@link StagingServer#getConnection(URL)}.
 * method only.
 * Since instance have been created, additional request parameters and headers can be set.
 * @author asmirnov
 * 
 */
public class StagingConnection extends HttpConnection {

	private static final Logger log = ServerLogger.SERVER.getLogger();

	private final StagingServer server;

	private final URL url;

	private final ConnectionRequest request;

	private final ConnectionResponse response;

	private final RequestChain servlet;

	private static final Cookie[] COOKIE = new Cookie[] {};

	private List<Cookie> cookies = new ArrayList<Cookie>();

	private final String pathInfo;

	private final String servletPath;

	private boolean finished = false;

	private boolean started = false;

	private final HttpServletRequest requestProxy;

	private final HttpServletResponse responseProxy;

	/**
	 * Create connection instance.
	 * @param localServer virtual server instance.
	 * @param url request URL.
	 */
	StagingConnection(StagingServer localServer, URL url) {
		this.server = localServer;
		this.url = url;
		// TODO - context path support.
		String path = url.getPath();
		servlet = localServer.getServlet(path);
		if (null == servlet) {
			throw new IllegalArgumentException();
		}
		this.pathInfo = servlet.getPathInfo(path);
		this.servletPath = servlet.getServletPath(path);
		this.request = new ConnectionRequest();
		this.response = new ConnectionResponse();
		this.request.setAttribute("jakarta.servlet.include.path_info",
				this.pathInfo);
		this.request.setAttribute("jakarta.servlet.include.servlet_path",
				this.servletPath);
		String queryString = url.getQuery();
		if (null != queryString) {
			parseFormParameters(queryString);
		}
		
		// Create proxy objects.
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if(null == loader){
			loader = this.getClass().getClassLoader();
		}
		requestProxy = (HttpServletRequest) Proxy.newProxyInstance(loader, new Class[]{HttpServletRequest.class}, server.getInvocationHandler(request));
		responseProxy = (HttpServletResponse) Proxy.newProxyInstance(loader, new Class[]{HttpServletResponse.class}, server.getInvocationHandler(response));
	}


	/* (non-Javadoc)
     * @see org.jboss.test.faces.staging.HttpConnection#isFinished()
     */
	@Override
    public boolean isFinished() {
		return finished;
	}

	/* (non-Javadoc)
     * @see org.jboss.test.faces.staging.HttpConnection#isStarted()
     */
	@Override
    public boolean isStarted() {
		return started;
	}

//	private void checkStarted() {
//		if (!isFinished()) {
//			throw new TestException("request have not been started");
//		}
//	}

//	private void checkNotStarted() {
//		if (isStarted()) {
//			throw new TestException("request was started, no parameters changes allowed");
//		}
//	}

	/* (non-Javadoc)
     * @see org.jboss.test.faces.staging.HttpConnection#execute()
     */
	@Override
    public void execute() {
		if (isStarted() || isFinished()) {
			throw new TestException(
					"request have already been executed");
		}
		start();
		try {
			this.servlet.execute(request, response);
		} catch (ServletException e) {
			throw new TestException("Error execute request ",e);
		} catch (IOException e) {
			throw new TestException("IO Error during request execution",e);
		} finally {
			finish();
		}
	}

	/* (non-Javadoc)
     * @see org.jboss.test.faces.staging.HttpConnection#finish()
     */
	@Override
    public void finish() {
		server.requestFinished(request);
		finished = true;
	}

	/* (non-Javadoc)
     * @see org.jboss.test.faces.staging.HttpConnection#start()
     */
	@Override
    public void start() {
		log.fine("start " + getRequestMethod() + " request processing for file "
				+ url.getFile());
		log.fine("request parameters: " + getRequestParameters());
		server.requestStarted(request);
		started = true;
	}

	/**
	 * Get request url.
	 * @return the url
	 */
	public URL getUrl() {
		return url;
	}

	/* (non-Javadoc)
     * @see org.jboss.test.faces.staging.HttpConnection#getContentAsString()
     */
	@Override
    public String getContentAsString() {
//		checkStarted();
		String content = response.getWriterContent();
		if (null == content) {
			byte[] streamContent = response.getStreamContent();
			if (null != streamContent) {
				String encoding = response.getCharacterEncoding();
				if (null != encoding) {
					try {
						content = new String(streamContent, encoding);
					} catch (UnsupportedEncodingException e) {
						throw new TestException(e);
					}
				} else {
					content = new String(streamContent);
				}
			}
		}
		return content;
	}

	/* (non-Javadoc)
     * @see org.jboss.test.faces.staging.HttpConnection#getResponseBody()
     */
	@Override
    public byte[] getResponseBody() {
//		checkStarted();
		byte[] content = response.getStreamContent();
		if (null == content) {
			String writerContent = response.getWriterContent();
			if (null != writerContent) {
				try {
					content = writerContent.getBytes(response
							.getCharacterEncoding());
				} catch (UnsupportedEncodingException e) {
					content = writerContent.getBytes();
				}
			} else {
				content = new byte[0];
			}
		}
		return content;
	}

	/* (non-Javadoc)
     * @see org.jboss.test.faces.staging.HttpConnection#getCookies()
     */
	private List<Cookie> getCookies() {
		return cookies;
	}

	/**
	 * request object for the this connection.
	 * @return the request
	 */
    @Override
    public HttpServletRequest getRequest() {
		return requestProxy;
	}

	/**
	 * response object for the this connection.
	 * @return the response
	 */
    @Override
    public HttpServletResponse getResponse() {
		return responseProxy;
	}

	/* (non-Javadoc)
     * @see org.jboss.test.faces.staging.HttpConnection#getResponseCharacterEncoding()
     */
	@Override
    public String getResponseCharacterEncoding() {
//		checkStarted();
		return response.getCharacterEncoding();
	}

	/* (non-Javadoc)
     * @see org.jboss.test.faces.staging.HttpConnection#getResponseContentType()
     */
	@Override
    public String getResponseContentType() {
//		checkStarted();
		return response.getContentType();
	}

	/* (non-Javadoc)
     * @see org.jboss.test.faces.staging.HttpConnection#getResponseStatus()
     */
	@Override
    public int getResponseStatus() {
//		checkStarted();
		return response.getStatus();
	}

	/* (non-Javadoc)
     * @see org.jboss.test.faces.staging.HttpConnection#getErrorMessage()
     */
	@Override
    public String getErrorMessage() {
//		checkStarted();
		return response.getErrorMessage();
	}

	/* (non-Javadoc)
     * @see org.jboss.test.faces.staging.HttpConnection#getResponseHeaders()
     */
	@Override
    public Map<String, String[]> getResponseHeaders() {
//		checkStarted();
		return response.getHeaders();
	}

	/* (non-Javadoc)
     * @see org.jboss.test.faces.staging.HttpConnection#setRequestCharacterEncoding(java.lang.String)
     */
	@Override
    public void setRequestCharacterEncoding(String charset) throws UnsupportedEncodingException {
//		checkNotStarted();
		request.setCharacterEncoding(charset);		
	}

	/* (non-Javadoc)
     * @see org.jboss.test.faces.staging.HttpConnection#setRequestBody(java.lang.String)
     */
	@Override
    public void setRequestBody(String body) {
//		checkNotStarted();
		request.setRequestBody(body);		
	}

	/* (non-Javadoc)
     * @see org.jboss.test.faces.staging.HttpConnection#setRequestContentType(java.lang.String)
     */
	@Override
    public void setRequestContentType(String contentType) {
//		checkNotStarted();
		request.setContentType(contentType);
		
	}

	/**
	 * Append additional HTTP request headers.
	 * @param headers
	 */
    @Override
    public void addRequestHeaders(Map<String, String> headers) {
//		checkNotStarted();
		request.addHeaders(headers);		
	}

	private class ConnectionRequest extends StagingHttpRequest {
	
		public Cookie[] getCookies() {
			return cookies.toArray(COOKIE);
		}
	
		/*
		 * (non-Javadoc)
		 * 
		 * @see jakarta.servlet.http.HttpServletRequest#getMethod()
		 */
		public String getMethod() {
			return getRequestMethod().toString();
		}
	
		/*
		 * (non-Javadoc)
		 * 
		 * @see jakarta.servlet.http.HttpServletRequest#getServletPath()
		 */
		public String getServletPath() {
			return servletPath;
		}
	
		/*
		 * (non-Javadoc)
		 * 
		 * @see jakarta.servlet.http.HttpServletRequest#getPathInfo()
		 */
		public String getPathInfo() {
			return pathInfo;
		}
	
		/*
		 * (non-Javadoc)
		 * 
		 * @see jakarta.servlet.http.HttpServletRequest#getQueryString()
		 */
		public String getQueryString() {
		    return getRequestQueryString();
		}

        /*
		 * (non-Javadoc)
		 * 
		 * @see jakarta.servlet.http.HttpServletRequest#getRequestURI()
		 */
		public String getRequestURI() {
			return url.getPath();
		}
		
		/* (non-Javadoc)
		 * @see org.jboss.test.faces.staging.StagingHttpRequest#getServerName()
		 */
		@Override
		public String getServerName() {
			return url.getHost();
		}
		
	
		@Override
		public int getLocalPort() {
			int port = url.getPort();
			if(port < 0){
				port = super.getLocalPort();
			}
			return port;
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see jakarta.servlet.ServletRequest#getParameter(java.lang.String)
		 */
		public String getParameter(String name) {
			String[] values = getRequestParameters().get(name);
			if (null != values && values.length > 0) {
				return values[0];
			}
			return null;
		}
	
		/*
		 * (non-Javadoc)
		 * 
		 * @see jakarta.servlet.ServletRequest#getParameterMap()
		 */
		@SuppressWarnings("unchecked")
		public Map getParameterMap() {
			return Collections.unmodifiableMap(getRequestParameters());
		}
	
		/*
		 * (non-Javadoc)
		 * 
		 * @see jakarta.servlet.ServletRequest#getParameterNames()
		 */
		@SuppressWarnings("unchecked")
		public Enumeration getParameterNames() {
			return Collections.enumeration(getRequestParameters().keySet());
		}
	
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * jakarta.servlet.ServletRequest#getParameterValues(java.lang.String)
		 */
		public String[] getParameterValues(String name) {
			return getRequestParameters().get(name);
		}
	
		/*
		 * (non-Javadoc)
		 * 
		 * @see jakarta.servlet.http.HttpServletRequest#getSession()
		 */
		public HttpSession getSession() {
			return server.getSession();
		}
	
		/*
		 * (non-Javadoc)
		 * 
		 * @see jakarta.servlet.http.HttpServletRequest#getSession(boolean)
		 */
		public HttpSession getSession(boolean create) {
			return server.getSession(create);
		}
	
		@Override
		public RequestDispatcher getRequestDispatcher(String path) {
			RequestDispatcher dispatcher = null;
			if (!path.startsWith("/")) {
				try {
					URL absoluteUrl = new URL(url, path);
					path = absoluteUrl.getFile();
				} catch (MalformedURLException e) {
					return null;
				}
			}
			final RequestChain dispatchedServlet = server.getServlet(path);
			if (null != dispatchedServlet) {
				dispatcher = new RequestDispatcher() {
	
					public void forward(ServletRequest request,
							ServletResponse response) throws ServletException,
							IOException {
						response.reset();
						dispatchedServlet.execute(request, response);
					}
	
					public void include(ServletRequest request,
							ServletResponse response) throws ServletException,
							IOException {
						dispatchedServlet.execute(request, response);
					}
	
				};
			}
			return dispatcher;
		}
	
		@Override
		protected void attributeAdded(String name, Object o) {
			server.requestAttributeAdded(this, name, o);
	
		}
	
		@Override
		protected void attributeRemoved(String name, Object removed) {
			server.requestAttributeRemoved(this, name, removed);
	
		}
	
		@Override
		protected void attributeReplaced(String name, Object o) {
			server.requestAttributeReplaced(this, name, o);
	
		}

		@Override
		public String changeSessionId() {
			//MZ
			return requestProxy.changeSessionId();
		}

		@Override
		public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
			//MZ
			return requestProxy.upgrade(handlerClass);
		}

		@Override
		public long getContentLengthLong() {
			//MZ
			return requestProxy.getContentLengthLong();
		}

		@Override
		public String getRequestId() {
			//MZ
			return requestProxy.getRequestId();
		}

		@Override
		public String getProtocolRequestId() {
			//MZ
			return requestProxy.getProtocolRequestId();
		}

		@Override
		public ServletConnection getServletConnection() {
			//MZ
			return requestProxy.getServletConnection();
		}
	
	}

	private class ConnectionResponse extends StagingHttpResponse {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * jakarta.servlet.http.HttpServletResponse#addCookie(jakarta.servlet.http
		 * .Cookie )
		 */
		@Override
        public void addCookie(Cookie cookie) {
		    super.addCookie(cookie);
			cookies.add(cookie);
	
		}

		@Override
		public void sendRedirect(String location, int sc, boolean clearBuffer) throws IOException {
			//MZ
			super.sendRedirect(location);
		}

		@Override
		public void setContentLengthLong(long len) {
			//MZ
			super.setContentLength((int)len);
		}
	
	}

	/* (non-Javadoc)
     * @see org.jboss.test.faces.staging.HttpConnection#getResponseContentLength()
     */
	@Override
    public long getResponseContentLength() {
		return response.getContentLength();		
	}


    @Override
    protected String getRequestCharacterEncoding() {
        return request.getCharacterEncoding();
    }

}
