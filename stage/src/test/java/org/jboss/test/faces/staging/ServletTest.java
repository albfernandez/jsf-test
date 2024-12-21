package org.jboss.test.faces.staging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import jakarta.servlet.ServletContextAttributeEvent;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration.Dynamic;

public class ServletTest {

	@Test
	public void testIsApplicable() {
		StaticServlet staticServlet = new StaticServlet();
		RequestChain servlet = new ServletContainer("/foo/*",staticServlet);
		assertTrue(servlet.isApplicable("/foo/bar.jsf"));
		assertFalse(servlet.isApplicable("/foz/bar.jsf"));
		assertFalse(servlet.isApplicable("bar"));
		servlet = new ServletContainer("*.jsf",staticServlet);
		assertTrue(servlet.isApplicable("/foo/bar.jsf"));
		assertFalse(servlet.isApplicable("bar"));
		try {
			servlet = new ServletContainer(".jsf",staticServlet);
		} catch (IllegalArgumentException e) {
			return;
		}
		assertFalse(true);
	}

	@Test
	public void testGetServletPath() {
		StaticServlet staticServlet = new StaticServlet();
		RequestChain servlet = new ServletContainer("/foo/*",staticServlet);
		assertEquals("/foo/", servlet.getServletPath("/foo/bar.jsf"));
		assertNull(servlet.getServletPath("/foz/bar.jsf"));
		servlet = new ServletContainer("*.jsf",staticServlet);
		assertEquals("/foo/bar.jsf", servlet.getServletPath("/foo/bar.jsf"));
	}

	@Test
	public void testGetPathInfo() {
		StaticServlet staticServlet = new StaticServlet();
		RequestChain servlet = new ServletContainer("/foo/*",staticServlet);
		assertEquals("/bar.jsf", servlet.getPathInfo("/foo/bar.jsf"));
		assertNull(servlet.getPathInfo("/foz/bar.jsf"));
		servlet = new ServletContainer("*.jsf",staticServlet);
		assertNull(servlet.getPathInfo("/foo/bar.jsf"));

	}

	@Test
	public void testInit() throws ServletException {
		StaticServlet staticServlet = new StaticServlet();
		RequestChain servlet = new ServletContainer("/foo/*",staticServlet);
		StagingServletContext context = new StagingServletContext(){

			@Override
			protected ServerResource getServerResource(String path) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			protected void valueBound(ServletContextAttributeEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			protected void valueReplaced(ServletContextAttributeEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			protected void valueUnbound(
					ServletContextAttributeEvent servletContextAttributeEvent) {
				// TODO Auto-generated method stub

			}

			public String getMimeType(String file) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Dynamic addJspFile(String servletName, String jspFile) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getVirtualServerName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getSessionTimeout() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void setSessionTimeout(int sessionTimeout) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public String getRequestCharacterEncoding() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setRequestCharacterEncoding(String encoding) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public String getResponseCharacterEncoding() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setResponseCharacterEncoding(String encoding) {
				// TODO Auto-generated method stub
				
			}

		};
		servlet.init(context);
		assertSame(context,staticServlet.getServletContext());
	}

}
