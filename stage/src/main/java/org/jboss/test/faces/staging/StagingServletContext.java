/**
 * 
 */
package org.jboss.test.faces.staging;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.FilterRegistration.Dynamic;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextAttributeEvent;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.descriptor.JspConfigDescriptor;


/**
 * @author asmirnov
 *
 */
abstract class StagingServletContext implements ServletContext {
	
	private static final Logger log = ServerLogger.SERVER.getLogger();

	public static final String CONTEXT_PATH = "";

	private static final String APPLICATION_NAME = "stub";
	private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();
	private final Map<String,String> initParameters = new HashMap<String, String>();

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getAttributeNames()
	 */
	@SuppressWarnings("unchecked")
	public Enumeration getAttributeNames() {
		return Collections.enumeration(attributes.keySet());
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getContext(java.lang.String)
	 */
	public ServletContext getContext(String uripath) {
		// stub server has only one context.
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getContextPath()
	 */
	public String getContextPath() {
		// Test always run in the root context.
		return CONTEXT_PATH;
	}

	public void addInitParameters(Map<String,String>parameters) {
		initParameters.putAll(parameters);
	}
	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getInitParameter(java.lang.String)
	 */
	public String getInitParameter(String name) {
		return initParameters.get(name);
	}

	
	/**
	 * Put new init parameter to the Map.
	 * @param name
	 * @param value
	 * @return 
	 */
	public boolean setInitParameter(String name, String value) {
		return null != initParameters.put(name, value);
	}
	
	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getInitParameterNames()
	 */
	@SuppressWarnings("unchecked")
	public Enumeration getInitParameterNames() {
		return Collections.enumeration(initParameters.keySet());
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getMajorVersion()
	 */
	public int getMajorVersion() {
		return 2;
	}


	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getMinorVersion()
	 */
	public int getMinorVersion() {
		return 5;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getNamedDispatcher(java.lang.String)
	 */
	public RequestDispatcher getNamedDispatcher(String name) {
		// TODO create stub dispatcher.
		log.info("unimplemented response method getNamedDispatcher");
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getRealPath(java.lang.String)
	 */
	public String getRealPath(String path) {
		// we always use 'virtual' configuration.
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getRequestDispatcher(java.lang.String)
	 */
	public RequestDispatcher getRequestDispatcher(String path) {
		// TODO implement stub dispatcher.
		log.info("unimplemented response method getRequestDispatcher");
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getResource(java.lang.String)
	 */
	public URL getResource(String path) throws MalformedURLException {
		URL url = null;
		ServerResource resource = getServerResource(path);
		if(null != resource){
			url = resource.getURL();
		}
		return url;
	}

	/**
	 * @param path
	 * @return
	 */
	protected abstract ServerResource getServerResource(String path);

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getResourceAsStream(java.lang.String)
	 */
	public InputStream getResourceAsStream(String path) {
		ServerResource resource = getServerResource(path);
		if(null != resource){
			try {
				return resource.getAsStream();
			} catch (IOException e) {
				return null;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getResourcePaths(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Set getResourcePaths(String path) {
		HashSet result=null;
		ServerResource resource = getServerResource(path);
		if(null == resource && !path.endsWith("/")){
			path+="/";
			resource = getServerResource(path);
		}
		if(null != resource){
			Set<String> paths = resource.getPaths();
			if(null != paths && paths.size()>0){
				result = new HashSet(paths.size());
				for (String resourcePath : paths) {
					result.add(path+resourcePath);
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getServerInfo()
	 */
	public String getServerInfo() {
		return "Stub test server";
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getServlet(java.lang.String)
	 */
	public Servlet getServlet(String name) throws ServletException {
		// always return null.
		log.info("unimplemented response method getServlet");
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getServletContextName()
	 */
	public String getServletContextName() {
		// Stub server has no declared name.
		return APPLICATION_NAME;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getServletNames()
	 */
	@SuppressWarnings("unchecked")
	public Enumeration getServletNames() {
		log.info("unimplemented response method getServletNames");
		return Collections.enumeration(Collections.EMPTY_LIST);
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getServlets()
	 */
	@SuppressWarnings("unchecked")
	public Enumeration getServlets() {
		log.info("unimplemented response method getServlets");
		return Collections.enumeration(Collections.EMPTY_LIST);
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#log(java.lang.String)
	 */
	public void log(String msg) {
		log.finest(msg);

	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#log(java.lang.Exception, java.lang.String)
	 */
	public void log(Exception exception, String msg) {
		log.log(Level.FINEST, msg, exception);

	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#log(java.lang.String, java.lang.Throwable)
	 */
	public void log(String message, Throwable throwable) {
		log.log(Level.FINEST, message, throwable);
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name) {
		// TODO - inform listeners
		Object removed = attributes.remove(name);
		if(null != removed){
			valueUnbound(new ServletContextAttributeEvent(this,name,removed));
		}
	}


	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String name, Object object) {
		// TODO - inform listeners
		if (null == object) {
			removeAttribute(name);
		} else {
			Object oldValue = attributes.put(name, object);
			ServletContextAttributeEvent event = new ServletContextAttributeEvent(this,name,object);
			if(null != oldValue){
				valueReplaced(event);
			} else {
				valueBound(event);
			}
		}
	}

	protected abstract void valueBound(ServletContextAttributeEvent event);

	protected abstract void valueReplaced(ServletContextAttributeEvent event);

	protected abstract void valueUnbound(
			ServletContextAttributeEvent servletContextAttributeEvent);

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#addFilter(java.lang.String, java.lang.Class)
	 */
	public Dynamic addFilter(String arg0, Class<? extends Filter> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#addFilter(java.lang.String, jakarta.servlet.Filter)
	 */
	public Dynamic addFilter(String arg0, Filter arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#addFilter(java.lang.String, java.lang.String)
	 */
	public Dynamic addFilter(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#addListener(java.lang.Class)
	 */
	public void addListener(Class<? extends EventListener> arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#addListener(java.lang.String)
	 */
	public void addListener(String arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#addListener(java.util.EventListener)
	 */
	public <T extends EventListener> void addListener(T arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#addServlet(java.lang.String, java.lang.Class)
	 */
	public jakarta.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			Class<? extends Servlet> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#addServlet(java.lang.String, jakarta.servlet.Servlet)
	 */
	public jakarta.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			Servlet arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#addServlet(java.lang.String, java.lang.String)
	 */
	public jakarta.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#createFilter(java.lang.Class)
	 */
	public <T extends Filter> T createFilter(Class<T> arg0)
			throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#createListener(java.lang.Class)
	 */
	public <T extends EventListener> T createListener(Class<T> arg0)
			throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#createServlet(java.lang.Class)
	 */
	public <T extends Servlet> T createServlet(Class<T> arg0)
			throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#declareRoles(java.lang.String[])
	 */
	public void declareRoles(String... arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getClassLoader()
	 */
	public ClassLoader getClassLoader() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getDefaultSessionTrackingModes()
	 */
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getEffectiveMajorVersion()
	 */
	public int getEffectiveMajorVersion() {
		return 3;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getEffectiveMinorVersion()
	 */
	public int getEffectiveMinorVersion() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getEffectiveSessionTrackingModes()
	 */
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getFilterRegistration(java.lang.String)
	 */
	public FilterRegistration getFilterRegistration(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getFilterRegistrations()
	 */
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getJspConfigDescriptor()
	 */
	public JspConfigDescriptor getJspConfigDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getMimeType(java.lang.String)
	 */
	public String getMimeType(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getServletRegistration(java.lang.String)
	 */
	public ServletRegistration getServletRegistration(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getServletRegistrations()
	 */
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#getSessionCookieConfig()
	 */
	public SessionCookieConfig getSessionCookieConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.servlet.ServletContext#setSessionTrackingModes(java.util.Set)
	 */
	public void setSessionTrackingModes(Set<SessionTrackingMode> arg0) {
		// TODO Auto-generated method stub
		
	}

}
