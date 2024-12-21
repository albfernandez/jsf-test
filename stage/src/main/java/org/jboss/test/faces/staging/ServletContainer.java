/**
 *
 */
package org.jboss.test.faces.staging;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;


/**
 * This class represens Servlet in the web application.
 * @author asmirnov
 *
 */
public class ServletContainer implements RequestChain {

	private static final Logger log = ServerLogger.SERVER.getLogger();

	private final Servlet servlet;

	private final boolean prefixMapped;

	private final String mapping;

	private final Map<String, String> initParameters;

	private String name = "Default";

	private boolean initialized = false;

	/**
	 * @param mapping Servlet mapping pattern, as defined in the &lt;servlet-mapping&gt; element.
	 * @param servlet servlet instance. Can't be null.
	 * @throws NullPointerException if servlet parameter is null.
	 */
	public ServletContainer(String mapping, Servlet servlet) {
		if(null == servlet){
			throw new NullPointerException();
		}
		if (null == mapping) {
			this.prefixMapped = true;
			this.mapping = "";
		} else if (mapping.startsWith("*")) {
			this.prefixMapped = false;
			this.mapping = mapping.substring(1);
		} else if (mapping.endsWith("*")) {
			this.prefixMapped = true;
			this.mapping = mapping.substring(0, mapping.length() - 1);
		} else {
			throw new IllegalArgumentException("Invalid mapping " + mapping);
		}
		this.servlet = servlet;
		this.initParameters = new HashMap<String, String>();
	}

	/**
	 * Append filter initialization parameter. Name and value are same as
	 * defined in the web.xml
	 *
	 * <code>
	 * &lt;init-param&gt;
	 *    &lt;param-name&gt;foo&lt;/param-name&gt;
	 *    &lt;param-value&gt;bar&lt;/param-value&gt;
	 *   &lt;/init-param&gt;
	 * </code>
	 *
	 * @param name
	 * @param value
	 * @throws IllegalStateException if servlet was already initialized.
	 */
	public void addInitParameter(String name, String value) {
		if (initialized) {
			throw new IllegalStateException(
					"Servlet have already been initialized, init parameters can't be changed");
		}
		initParameters.put(name, value);
	}

	/**
	 * @return the name of servlet
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 * @throws IllegalStateException if servlet was already initialized.
	 */
	public void setName(String name) {
		if (initialized) {
			throw new IllegalStateException(
					"Servlet have already been initialized, name can't be changed");
		}
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.jboss.test.faces.staging.RequestChain#isApplicable(java.lang.String)
	 */
	public boolean isApplicable(String path) {
		if (prefixMapped && path.startsWith(mapping)) {
			return true;
		} else if (!prefixMapped && path.endsWith(mapping)) {
			return true;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.jboss.test.faces.staging.RequestChain#getServletPath(java.lang.String)
	 */
	public String getServletPath(String path) {
		if (!isApplicable(path)) {
			return null;
		}
		if (prefixMapped) {
			return mapping;
		} else {
			return path;
		}
	}

	/* (non-Javadoc)
	 * @see org.jboss.test.faces.staging.RequestChain#getPathInfo(java.lang.String)
	 */
	public String getPathInfo(String path) {
		if (!isApplicable(path)) {
			return null;
		}
		if (prefixMapped) {
		    String pathInfo = path.substring(mapping.length() - 1);
		    if (!pathInfo.startsWith("/")) {
		        pathInfo = "/" + pathInfo;
		    }
		    return pathInfo;
		} else {
			return null;
		}

	}

	/* (non-Javadoc)
	 * @see org.jboss.test.faces.staging.RequestChain#init(org.jboss.test.faces.staging.StagingServletContext)
	 */
	public void init(final ServletContext context)
			throws ServletException {
		if (!initialized) {
			log.finest("Initialize servlet "+getName());
			servlet.init(new ServletConfig() {

				public String getInitParameter(String name) {
					return initParameters.get(name);
				}

				@SuppressWarnings("unchecked")
				public Enumeration getInitParameterNames() {
					return Collections.enumeration(initParameters.keySet());
				}

				public ServletContext getServletContext() {
					return context;
				}

				public String getServletName() {
					return name;
				}

			});
			initialized = true;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.jboss.test.faces.staging.RequestChain#execute(jakarta.servlet.ServletRequest
	 * , jakarta.servlet.ServletResponse)
	 */
	public void execute(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {
		if (!initialized) {
			throw new IllegalStateException(
					"Servlet "+getName()+" have not been initialized, could'n execute request");
		}
		log.finest("Request '"+request+"' executes by the '"+getName()+"' servlet");
		this.servlet.service(request, response);

	}

	/* (non-Javadoc)
	 * @see org.jboss.test.faces.staging.RequestChain#destroy()
	 */
	public void destroy() {
		if (initialized) {
			this.servlet.destroy();
			initialized = false;
		}
	}
}
