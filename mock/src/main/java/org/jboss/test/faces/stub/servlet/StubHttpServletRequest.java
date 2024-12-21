/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.test.faces.stub.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jboss.test.faces.stub.util.IteratorEnumeration;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;

/**
 * @author Gavin King
 * @author Thomas Heute
 */
public class StubHttpServletRequest implements HttpServletRequest
{
   
   private Map<String, String[]> parameters = new HashMap<String, String[]>();
   private Map<String, Object> attributes = new HashMap<String, Object>();
   private HttpSession session;
   private Map<String, String[]> headers = new HashMap<String, String[]>();
   private String principalName;
   private Set<String> principalRoles;
   private Cookie[] cookies;
   private String method;
   private Enumeration locales;
   
   public StubHttpServletRequest(HttpSession session)
   {
      this(session, null, new HashSet<String>());
   }

   public StubHttpServletRequest(HttpSession session, String principalName, Set<String> principalRoles)
   {
      this(session, principalName, principalRoles, new Cookie[] {}, null);
   }

   public StubHttpServletRequest(HttpSession session, String principalName, Set<String> principalRoles, Cookie[] cookies, String method)
   {
      this.session = session;
      this.principalName = principalName;
      this.principalRoles = principalRoles;
      this.cookies = cookies;
      this.method = method;
      // The 1.2 RI NPEs if this header isn't present 
      headers.put("Accept", new String[0]);
      locales = new IteratorEnumeration(new ArrayList().iterator());
   }

   public Map<String, String[]> getParameters()
   {
      return parameters;
   }

   public Map<String, Object> getAttributes()
   {
      return attributes;
   }
   
   public String getAuthType()
   {
      //TODO
      return null;
   }

   public Cookie[] getCookies()
   {
      return cookies;
   }

   public long getDateHeader(String arg0)
   {
      throw new UnsupportedOperationException();
   }

   public String getHeader(String header)
   {
      String[] values = headers.get(header);
      return values==null || values.length==0 ? null : values[0];
   }

   public Enumeration getHeaders(String header)
   {
      return new IteratorEnumeration( Arrays.asList( headers.get(header) ).iterator() );
   }

   public Enumeration getHeaderNames()
   {
      return new IteratorEnumeration( headers.keySet().iterator() );
   }

   public int getIntHeader(String header)
   {
      throw new UnsupportedOperationException();
   }

   public String getMethod()
   {
      return method;
   }

   public String getPathInfo()
   {
      //TODO
      return null;
   }

   public String getPathTranslated()
   {
      //TODO
      return null;
   }

   public String getContextPath()
   {
      return "/project";
   }

   public String getQueryString()
   {
      //TODO
      return null;
   }

   public String getRemoteUser()
   {
      return principalName;
   }

   public boolean isUserInRole(String role)
   {
      return principalRoles.contains(role);
   }

   public Principal getUserPrincipal()
   {
      return principalName==null ? null : 
         new Principal() 
         {
            public String getName()
            {
               return principalName;
            }
         };
   }

   public String getRequestedSessionId()
   {
      //TODO
      return null;
   }

   public String getRequestURI()
   {
      return "http://localhost:8080/myproject/page.seam";
   }

   public StringBuffer getRequestURL()
   {
      return new StringBuffer( getRequestURI() );
   }

   public String getServletPath()
   {
      return "/page.seam";
   }

   public HttpSession getSession(boolean create)
   {
      return session;
   }

   public HttpSession getSession()
   {
      return getSession(true);
   }

   public boolean isRequestedSessionIdValid()
   {
      return true;
   }

   public boolean isRequestedSessionIdFromCookie()
   {
      return true;
   }

   public boolean isRequestedSessionIdFromURL()
   {
      return false;
   }

   public boolean isRequestedSessionIdFromUrl()
   {
      return false;
   }

   public Object getAttribute(String att)
   {
      return attributes.get(att);
   }

   public Enumeration getAttributeNames()
   {
      return new IteratorEnumeration( attributes.keySet().iterator() );
   }

   public String getCharacterEncoding()
   {
      //TODO
      return null;
   }

   public void setCharacterEncoding(String enc)
         throws UnsupportedEncodingException
   {
      //TODO

   }

   public int getContentLength()
   {
      //TODO
      return 0;
   }

   public String getContentType()
   {
      //TODO
      return null;
   }

   public ServletInputStream getInputStream() throws IOException
   {
      //TODO
      return null;
   }

   public String getParameter(String param)
   {
      String[] values = parameters.get(param);
      return values==null || values.length==0 ? null : values[0];
   }

   public Enumeration getParameterNames()
   {
      return new IteratorEnumeration( parameters.keySet().iterator() );
   }

   public String[] getParameterValues(String param)
   {
      return parameters.get(param);
   }

   public Map getParameterMap()
   {
      return parameters;
   }

   public String getProtocol()
   {
      //TODO
      return null;
   }

   public String getScheme()
   {
      //TODO
      return null;
   }

   public String getServerName()
   {
      //TODO
      return null;
   }

   public int getServerPort()
   {
      //TODO
      return 0;
   }

   public BufferedReader getReader() throws IOException
   {
      //TODO
      return null;
   }

   public String getRemoteAddr()
   {
      //TODO
      return null;
   }

   public String getRemoteHost()
   {
      //TODO
      return null;
   }

   public void setAttribute(String att, Object value)
   {
      if (value==null)
      {
         attributes.remove(value);
      }
      else
      {
         attributes.put(att, value);
      }
   }

   public void removeAttribute(String att)
   {
      attributes.remove(att);
   }

   public Locale getLocale()
   {
      //TODO
      return null;
   }

   public Enumeration getLocales()
   {
      return locales;
   }

   public boolean isSecure()
   {
      //TODO
      return false;
   }

   public RequestDispatcher getRequestDispatcher(String path)
   {
      //TODO
      return null;
   }

   public String getRealPath(String path)
   {
      //TODO
      return null;
   }

   public int getRemotePort()
   {
      //TODO
      return 0;
   }

   public String getLocalName()
   {
      //TODO
      return null;
   }

   public String getLocalAddr()
   {
      //TODO
      return null;
   }

   public int getLocalPort()
   {
      //TODO
      return 0;
   }

   public Map<String, String[]> getHeaders()
   {
      return headers;
   }

@Override
public ServletContext getServletContext() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public AsyncContext startAsync() throws IllegalStateException {
	// TODO Auto-generated method stub
	return null;
}

@Override
public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
		throws IllegalStateException {
	// TODO Auto-generated method stub
	return null;
}

@Override
public boolean isAsyncStarted() {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean isAsyncSupported() {
	// TODO Auto-generated method stub
	return false;
}

@Override
public AsyncContext getAsyncContext() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public DispatcherType getDispatcherType() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
	// TODO Auto-generated method stub
	return false;
}

@Override
public void login(String username, String password) throws ServletException {
	// TODO Auto-generated method stub
	
}

@Override
public void logout() throws ServletException {
	// TODO Auto-generated method stub
	
}

@Override
public Collection<Part> getParts() throws IOException, ServletException {
	// TODO Auto-generated method stub
	return null;
}

@Override
public Part getPart(String name) throws IOException, ServletException {
	// TODO Auto-generated method stub
	return null;
}

@Override
public long getContentLengthLong() {
	// TODO Auto-generated method stub
	return 0;
}

@Override
public String changeSessionId() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
	// TODO Auto-generated method stub
	return null;
}

@Override
public String getRequestId() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public String getProtocolRequestId() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public ServletConnection getServletConnection() {
	// TODO Auto-generated method stub
	return null;
}
}
