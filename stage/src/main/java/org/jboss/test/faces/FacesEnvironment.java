/*
 * $Id$
 *
 * License Agreement.
 *
 * Rich Faces - Natural Ajax for Java Server Faces (JSF)
 *
 * Copyright (C) 2007 Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package org.jboss.test.faces;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.LogManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.test.faces.staging.HttpConnection;
import org.jboss.test.faces.staging.HttpMethod;

import jakarta.faces.FacesException;
import jakarta.faces.FactoryFinder;
import jakarta.faces.application.Application;
import jakarta.faces.application.ApplicationFactory;
import jakarta.faces.application.StateManager;
import jakarta.faces.application.ViewHandler;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.FacesContextFactory;
import jakarta.faces.lifecycle.Lifecycle;
import jakarta.faces.lifecycle.LifecycleFactory;
import jakarta.faces.render.ResponseStateManager;
import jakarta.faces.webapp.FacesServlet;
import jakarta.servlet.Filter;

/**
 * <p class="changed_added_4_0">
 * </p>
 * 
 * @author asmirnov@exadel.com
 * 
 */
public class FacesEnvironment {

    public static final String WEB_XML = "/WEB-INF/web.xml";
    public static final String FACES_CONFIG_XML = "/WEB-INF/faces-config.xml";
    //MZ
    public static final String BEANS_CONFIG_XML = "/WEB-INF/beans.xml";

    public class FacesRequest {

        /**
         * Current virtual connection. This field populated by the {@link #setupWebContent()} method only.
         */
        private HttpConnection connection;

        /**
         * Current {@link FacesContext} instance. This field populated by the {@link #setupWebContent()} method only.
         */
        private FacesContext facesContext;

        private String viewId;

        public FacesRequest start() {
            if (connection.isStarted() || connection.isFinished()) {
                throw new IllegalStateException();
            }
            connection.start();
            FacesContextFactory facesContextFactory = (FacesContextFactory) FactoryFinder
                .getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
            facesContext = facesContextFactory.getFacesContext(facesServer.getContext(), connection.getRequest(),
                connection.getResponse(), lifecycle);
            
            //MZ
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!! viewId: " + viewId);
            
            if (null != viewId) {
            	
            	//MZ
            	UIViewRoot oldUIViewRoot = facesContext.getViewRoot();
            	System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!! oldUIViewRoot: " + oldUIViewRoot);
            	
            	
            	System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!! application: " + application);
            	System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!! viewHandler: " + application.getViewHandler());
            	UIViewRoot uiViewRoot = application.getViewHandler().createView(facesContext, viewId);
            	System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!! uiViewRoot: " + uiViewRoot);
            	
                facesContext.setViewRoot(uiViewRoot);
            }
            return this;
        }

        public FacesRequest finish() {
            if (!connection.isStarted() || connection.isFinished()) {
                throw new IllegalStateException();
            }
            connection.finish();
            return this;
		}
        
        public byte[] execute() {
            if (connection.isStarted() || connection.isFinished()) {
                throw new IllegalStateException();
            }
            connection.execute();
            return connection.getResponseBody();
        }

        public FacesRequest withViewId(String viewId) {
            if (connection.isStarted() || connection.isFinished()) {
                throw new IllegalStateException();
            }
            this.viewId = viewId;
            return this;
        }

        public FacesRequest withParameter(String name, String value) {
            this.connection.addRequestParameter(name, value);
            return this;
        }

        public String getResponseAsString() {
        	return connection.getContentAsString();
		}
        
        public void release() {
            if (null != facesContext) {
                facesContext.release();
                facesContext = null;
            }
            if (null != connection) {
                if (!connection.isFinished()) {
                    connection.finish();
                }
                connection = null;
            }
            requests.remove(this);
        }

        /**
         * <p class="changed_added_4_0">
         * </p>
         * 
         * @return the connection
         */
        public HttpConnection getConnection() {
            return this.connection;
        }

        public FacesRequest submit() throws MalformedURLException, FacesException {
            if (!connection.isFinished()) {
                throw new IllegalStateException();
            }
            // Extract VIEW_STATE value.
            Map<String, String> fields = getHiddenFields(connection.getContentAsString());
            if (!fields.containsKey(ResponseStateManager.VIEW_STATE_PARAM)) {
                throw new FacesException("No view state field in response");
            }
            FacesRequest facesRequest = createFacesRequest(connection.getRequest().getRequestURL().toString())
                .withViewId(viewId);
            facesRequest.connection.setRequestMethod(HttpMethod.POST);
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                facesRequest.withParameter(entry.getKey(), entry.getValue());
            }
            return facesRequest;
        }

    }

    private List<FacesRequest> requests = new CopyOnWriteArrayList<FacesRequest>();

    private ClassLoader contextClassLoader;

    /**
     * Prepared test server instance. Populated by the default {@link #setUp()} method.
     */
    private ApplicationServer facesServer;

    /**
     * JSF {@link Lifecycle} instance. Populated by the default {@link #setUp()} method.
     */
    private Lifecycle lifecycle;

    /**
     * JSF {@link Application} instance. Populated by the default {@link #setUp()} method.
     */
    private Application application;

    private boolean initialized = false;

    private ServletHolder facesServletContainer;

    private FilterHolder filterContainer;

    private String webXmlDefault;

    private File webRoot;

    public FacesEnvironment() {
        this(ApplicationServer.createApplicationServer());
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     */
    public FacesEnvironment(ApplicationServer applicationServer) {
        this.facesServer = applicationServer;
        setupFacesServlet();
        setupFacesListener();
        setupJsfInitParameters();
        setupWebContent();
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @return the facesServer
     */
    public ApplicationServer getServer() {
        return this.facesServer;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @return the lifecycle
     */
    public Lifecycle getLifecycle() {
        return this.lifecycle;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @return the application
     */
    public Application getApplication() {
        return this.application;
    }

    public FacesEnvironment withFilter(String name, Filter filter) {
        checkNotInitialized();
        filterContainer = new FilterHolder(facesServletContainer.getMapping(), filter);
        filterContainer.setName(name);

        return this;
    }

    public FacesEnvironment withRichFaces() {
        checkNotInitialized();
        try {
            Filter ajaxFilter = createInstance("org.ajax4jsf.Filter");
            withFilter("ajax4jsf", ajaxFilter);
            webXmlDefault = "org/jboss/test/faces/ajax-web.xml";
            return this;
        } catch (ClassNotFoundException e) {
            throw new TestException(e);
        }
    }

    public FacesEnvironment withSeam() {
        checkNotInitialized();
        try {
            Filter ajaxFilter = createInstance("org.jboss.seam.servlet.SeamFilter");
            withFilter("ajax4jsf", ajaxFilter);
            EventListener seamListener = createInstance("org.jboss.seam.servlet.SeamListener");
            facesServer.addWebListener(seamListener);
            webXmlDefault = "org/jboss/test/faces/ajax-web.xml";
            return this;
        } catch (ClassNotFoundException e) {
            throw new TestException(e);
        }
    }

    public FacesEnvironment withWebRoot(File root) {
        checkNotInitialized();
        webRoot = root;
        return this;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @param path
     * @param resource
     * @see org.jboss.test.faces.staging.StagingServer#addResource(java.lang.String, java.net.URL)
     */
    public FacesEnvironment withWebRoot(URL root) {
        checkNotInitialized();
        this.facesServer.addResourcesFromDirectory("/", root);
        webRoot = null;
        return this;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @param root
     * @return
     */
    public FacesEnvironment withWebRoot(String root) {
        checkNotInitialized();
        return withWebRoot(FacesEnvironment.class.getClassLoader().getResource(root));
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @param name
     * @param value
     * @see org.jboss.test.faces.staging.StagingServer#addInitParameter(java.lang.String, java.lang.String)
     */
    public FacesEnvironment withInitParameter(String name, String value) {
        checkNotInitialized();
        this.facesServer.addInitParameter(name, value);
        return this;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @param path
     * @param resource
     * @see org.jboss.test.faces.staging.StagingServer#addResource(java.lang.String, java.lang.String)
     */
    public FacesEnvironment withResource(String path, String resource) {
        this.facesServer.addResource(path, resource);
        return this;
    }

    public FacesEnvironment withResource(String path, URL resource) {
        this.facesServer.addResource(path, resource);
        return this;
    }

    public FacesEnvironment withResourcesFromDirectory(String path, URL resource){
    	this.facesServer.addResourcesFromDirectory(path, resource);
    	return this;
    }
    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @param path
     * @param resource
     * @see org.jboss.test.faces.staging.StagingServer#addResource(java.lang.String, java.lang.String)
     */
    public FacesEnvironment withContent(String path, String pageContent) {
        this.facesServer.addContent(path, pageContent);
        return this;
    }

    /**
     * Setup staging server instance with JSF implementation. First, this method creates a local test instance and calls
     * the other template method in the next sequence:
     * <ol>
     * <li>{@link #setupFacesServlet()}</li>
     * <li>{@link #setupFacesListener()}</li>
     * <li>{@link #setupJsfInitParameters()}</li>
     * <li>{@link #setupWebContent()}</li>
     * </ol>
     * After them, test server is initialized as well as fields {@link #lifecycle} and {@link #application} populated.
     * Also, if the resource "logging.properties" is exist in the test class package, The Java {@link LogManager} will
     * be configured with its content.
     * 
     * @throws java.lang.Exception
     */
    public FacesEnvironment start() {
        contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        
        System.out.println("!!! add webXmlDefault: " + webXmlDefault);
        
        facesServer.addResource(WEB_XML, webXmlDefault);
        
        System.out.println("!!! webRoot: " + webRoot);
        
        if (null != webRoot) {
        	
        	System.out.println("!!! add webRoot: " + webRoot);
        	
            facesServer.addResourcesFromDirectory("/", webRoot);
        }

        facesServer.addServlet(facesServletContainer);

        if (filterContainer != null) {
        	
        	System.out.println("!!! add filterContainer: " + filterContainer);
        	
            facesServer.addFilter(filterContainer);
        }

        facesServer.init();
        
        if (facesServer.getClass().getName().contains("Jetty")) {
        	
        	System.out.println("!!! Running with JETTY !!!!!!!!!!!!!!!!! ");
        	
        }
        
        /*MZ todo - why factories null with JettyServer? It initialization not complete? Load on startup missing? */
        //MZ dirty hack
        if (!facesServer.getClass().getName().contains("Jetty")) {
        	
	        ApplicationFactory applicationFactory = (ApplicationFactory) FactoryFinder
	            .getFactory(FactoryFinder.APPLICATION_FACTORY);
	        
	        System.out.println("!!! applicationFactory: " + applicationFactory);
	        
	        //MZ
	        if (applicationFactory != null) {
	        	application = applicationFactory.getApplication();
	        }
	        
	        System.out.println("!!! application: " + application);
	        
	        
	        LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
	            .getFactory(FactoryFinder.LIFECYCLE_FACTORY);
	        
	        System.out.println("!!! lifecycleFactory: " + lifecycleFactory);
	        
	        //MZ
	        if (lifecycleFactory != null) {
	        	lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
	        }
	        
	        System.out.println("!!! lifecycle: " + lifecycle);
                
        }
        
        initialized = true;
        return this;
    }

    /**
     * This hook method called from the {@link #setUp()} should append JSF implementation listener to the test server.
     * Default version applends "com.sun.faces.config.ConfigureListener" or
     * "org.apache.myfaces.webapp.StartupServletContextListener" for the existed SUN RI or MyFaces implementation. This
     * metod also calls appropriate {@link #setupSunFaces()} or {@link #setupMyFaces()} methods.
     */
    protected void setupFacesListener() {
    	
    	System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! setupFacesListener FacesEnvironment");
    	
        EventListener listener = null;
        try {
            // Check Sun RI configuration listener class.
            listener = createInstance("com.sun.faces.config.ConfigureListener");
            setupSunFaces();
            System.out.println("SUNDONE");
        } catch (ClassNotFoundException e) {
            // No JSF RI listener, check MyFaces.
            try {
                listener = createInstance("org.apache.myfaces.webapp.StartupServletContextListener");
                setupMyFaces();
                System.out.println("MYFACESDONE");
            } catch (ClassNotFoundException e1) {
                throw new TestException("No JSF listeners have been found", e1);
            }
        }
        facesServer.addWebListener(listener);
    }

    /**
     * This template method called from {@link #setUp()} to create {@link FacesServlet} instance. The default
     * implementation also tests presense of the "org.ajax4jsf.Filter" class. If this class is avalable, these instance
     * appended to the Faces Servlet call chain. Default mapping to the FacesServlet instance is "*.jsf"
     */
    protected void setupFacesServlet() {
        facesServletContainer = new ServletHolder("*.jsf", new FacesServlet());
        facesServletContainer.setName("Faces Servlet");
        webXmlDefault = "org/jboss/test/faces/web.xml";
    }

    /**
     * This template method called from {@link #setUp()} to append appropriate init parameters to the test server. The
     * default implementation sets state saving method to the "server", default jsf page suffix to the ".xhtml" and
     * project stage to UnitTest
     */
    protected void setupJsfInitParameters() {
        facesServer.addInitParameter(StateManager.STATE_SAVING_METHOD_PARAM_NAME,
            StateManager.STATE_SAVING_METHOD_SERVER);
        facesServer.addInitParameter(ViewHandler.DEFAULT_SUFFIX_PARAM_NAME, ".xhtml");
        // Do not use Jsf 2.0 classes directly because this environment should
        // be applicable for any JSF version.
        facesServer.addInitParameter("jakarta.faces.PROJECT_STAGE", "UnitTest");
    }

    /**
     * This template method called from the {@link #setupFacesListener()} if MyFaces implementation presents. The
     * default implementation does nothing.
     */
    protected void setupMyFaces() {
        //MZ
    	facesServer.addInitParameter("org.apache.myfaces.INITIALIZE_ALWAYS_STANDALONE", "true");
    	facesServer.addInitParameter("org.apache.myfaces.FLASH_SCOPE_DISABLED", "true");		//TODO jakarta.servlet.SessionCookieConfig.getAttribute(String)" because the return value of "jakarta.servlet.ServletContext.getSessionCookieConfig()" is null
    	//facesServer.addInitParameter("org.apache.myfaces.annotation.USE_CDI_FOR_ANNOTATION_SCANNING", "false");
    }

    /**
     * This template method called from the {@link #setupFacesListener()} if Sun JSF reference implementation presents.
     * The default implementation sets the "com.sun.faces.validateXml" "com.sun.faces.verifyObjects" init parameters to
     * the "true"
     */
    protected void setupSunFaces() {
        facesServer.addInitParameter("com.sun.faces.validateXml", "true");
        facesServer.addInitParameter("com.sun.faces.verifyObjects", "true");
    }

    /**
     * This template method called from the {@link #setUp()} to populate virtual server content. The default
     * implementation tries to load web content from directory pointed by the System property "webroot" or same property
     * from the "/webapp.properties" file.
     */
    protected void setupWebContent() {
        String webappDirectory = System.getProperty("webroot");
        webRoot = null;
        if (null == webappDirectory) {
            URL resource = this.getClass().getResource("/webapp.properties");
            if (null != resource && "file".equals(resource.getProtocol())) {
                Properties webProperties = new Properties();
                try {
                    InputStream inputStream = resource.openStream();
                    webProperties.load(inputStream);
                    inputStream.close();
                    webRoot = new File(resource.getPath());
                    webRoot = new File(webRoot.getParentFile(), webProperties.getProperty("webroot")).getAbsoluteFile();
                } catch (IOException e) {
                    throw new TestException(e);
                }
            }
        } else {
            webRoot = new File(webappDirectory);
        }

    }

    /**
     * Setup virtual server connection to run tests inside JSF lifecycle. The default implementation setups virtual
     * request to the "http://localhost/test.jsf" URL and creates {@link FacesContext} instance. Two template methods
     * are called :
     * <ol>
     * <li>{@link #setupConnection()} to prepare request method, parameters, headers and so</li>
     * <li>{@link #setupView()} to create default view.</li>
     * </ol>
     * 
     * @throws Exception
     */
    public FacesRequest createFacesRequest() throws Exception {
        String url = "http://localhost/test.jsf";
        return createFacesRequest(url).withViewId("/test.xhtml");
    }

    /**
     * <p class="changed_added_2_0">
     * </p>
     * 
     * @param url
     * @throws MalformedURLException
     * @throws FacesException
     */
    public FacesRequest createFacesRequest(String url) throws MalformedURLException, FacesException {
        FacesRequest request = new FacesRequest();
        request.connection = getServer().getConnection(new URL(url));
        requests.add(request);
        return request;
    }

    /**
     * JSF and Virtual server instance cleanup.
     * 
     * @throws java.lang.Exception
     */
    public void release() {
        checkInitialized();
        for (FacesRequest request : this.requests) {
            request.release();
        }
        facesServer.destroy();
        Thread.currentThread().setContextClassLoader(contextClassLoader);
        facesServer = null;
        application = null;
        lifecycle = null;
        initialized = false;
    }

    private void checkInitialized() {
        if (!initialized) {
            throw new TestException("JSF test environment has not been initialized");
        }
    }

    private void checkNotInitialized() {
        if (initialized) {
            throw new TestException("JSF test environment has already been initialized");
        }
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     * 
     * @param <T>
     * @param className
     * @return
     * @throws TestException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    private <T> T createInstance(String className) throws TestException, ClassNotFoundException {
        try {
            Class<?> clazz = FacesEnvironment.class.getClassLoader().loadClass(className);
            return (T) clazz.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new TestException(e);
        }
    }

    public static FacesEnvironment createEnvironment() {
        return new FacesEnvironment();
    }

    public static FacesEnvironment createEnvironment(ApplicationServer applicationServer) {
        return new FacesEnvironment(applicationServer);
    }

    
    static final Pattern INPUT_PATTERN =Pattern.compile("<input([^>]+)>", Pattern.MULTILINE|Pattern.DOTALL);
    
    static final Pattern NAME_PATTERN =Pattern.compile("name=[\"']([^\"']*)[\"']");

    static final Pattern VALUE_PATTERN =Pattern.compile("value=[\"']([^\"']*)[\"']");

    public static Collection<String> getInputFields(String content){
        List<String> inputs = new ArrayList<String>();
        Matcher matcher = INPUT_PATTERN.matcher(content);
        while (matcher.find()) {
            inputs.add(matcher.group(1));
        }
        return inputs;
    }
    
    public static Map<String, String> getHiddenFields(String content){
        Collection<String> inputFields = getInputFields(content);
        HashMap<String, String> parameters = new HashMap<String, String>(inputFields.size());
        for (String string : inputFields) {
            if(string.contains("type='hidden'")||string.contains("type=\"hidden\"")){
                Matcher matcher = NAME_PATTERN.matcher(string);
                if(matcher.find()){
                    String name = matcher.group(1);
                    Matcher valueMatcher = VALUE_PATTERN.matcher(string);
                    if(valueMatcher.find()){
                        parameters.put(name, valueMatcher.group(1));
                    } else {
                        parameters.put(name, "");
                    }
                }
            }
        }
        return parameters;
    }

}
