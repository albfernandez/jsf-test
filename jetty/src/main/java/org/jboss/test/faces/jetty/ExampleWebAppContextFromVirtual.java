package org.jboss.test.faces.jetty;


import java.util.logging.Logger;

import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.jboss.test.faces.FacesEnvironment;
import org.jboss.test.faces.TestException;

import jakarta.faces.webapp.FacesServlet;

public class ExampleWebAppContextFromVirtual
{
	private static Logger logger = Logger.getLogger("test");
	
    public static void main(String[] args) throws Exception
    {
        
    	logger.info("logggggggggggggggggggggggg");
    	
    	VirtualDirectoryResource serverRoot = new VirtualDirectoryResource("");
    	
    	serverRoot.addResource("/WEB-INF/web.xml", ResourceFactory.root().newResource("src/main/webapp/WEB-INF/web.xml"));
    	
    	
    	Server server = new Server(9999);

        WebAppContext webAppContext = new WebAppContext();
        
        webAppContext.setContextPath("/");
        webAppContext.setBaseResourceAsString("src/main/webapp");				//serverRoot);
        
        // webAppContext.addEventListener(new StartupServletContextListener());
        
        //ServletHolder facesServlet = new ServletHolder(new FacesServlet());
        //facesServlet.setInitOrder(-1);
        //webAppContext.addServlet(facesServlet, "*.jsf"); 
        
        //EventListener startuplistener = createInstance("org.apache.myfaces.webapp.StartupServletContextListener");
        //webAppContext.addEventListener(startuplistener);
        
        server.setHandler(webAppContext);

        server.start();
        server.join();
    }
    
    
    private static <T> T createInstance(String className) throws TestException, ClassNotFoundException {
        try {
            Class<?> clazz = FacesEnvironment.class.getClassLoader().loadClass(className);
            return (T) clazz.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new TestException(e);
        }
    }
    
}