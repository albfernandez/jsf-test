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

package org.jboss.test.faces.mock;

import java.util.HashMap;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.easymock.MockType;
import org.jboss.test.faces.mock.factory.FactoryMock;
import org.jboss.test.faces.mock.factory.FactoryMockingService;
import org.jboss.test.faces.writer.RecordingResponseWriter;

import jakarta.el.ELContext;
import jakarta.faces.FactoryFinder;
import jakarta.faces.application.Application;
import jakarta.faces.application.ApplicationFactory;
import jakarta.faces.application.ViewHandler;
import jakarta.faces.context.ExceptionHandlerFactory;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.ExternalContextFactory;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.FacesContextFactory;
import jakarta.faces.context.PartialViewContextFactory;
import jakarta.faces.lifecycle.LifecycleFactory;
import jakarta.faces.render.RenderKit;
import jakarta.faces.render.RenderKitFactory;
import jakarta.faces.render.ResponseStateManager;
import jakarta.faces.view.facelets.TagHandlerDelegateFactory;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p class="changed_added_4_0">
 * </p>
 *
 * @author asmirnov@exadel.com
 *
 */
public class MockFacesEnvironment {

    private static ThreadLocal<MockFacesEnvironment> instance = new ThreadLocal<MockFacesEnvironment>();

    private final IMocksControl mocksControl;

    private FacesContext facesContext;

    private boolean withFactories = false;

    private ExternalContext externalContext;

    private ELContext elContext;

    private ServletContext context;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private Application application;

    private ViewHandler viewHandler;

    private RenderKit renderKit;

    private ResponseStateManager responseStateManager;

    private String name;

	private RecordingResponseWriter responseWriter;

    private static boolean jsf2;

    /** The service. */
    private FactoryMockingService service = FactoryMockingService.getInstance();

    static {
        try {
            Class.forName("jakarta.faces.component.behavior.Behavior", false, FacesContext.class.getClassLoader());
            jsf2 = true;
        } catch (Throwable e) {
            jsf2 = false;
        }
    }

    // Factory methods

    public static MockFacesEnvironment createEnvironment() {
        return new MockFacesEnvironment(new FacesMocksClassControl(MockType.DEFAULT));
    }

    public static MockFacesEnvironment createStrictEnvironment() {
        return new MockFacesEnvironment(new FacesMocksClassControl(MockType.STRICT));
    }

    public static MockFacesEnvironment createNiceEnvironment() {
        return new MockFacesEnvironment(new FacesMocksClassControl(MockType.NICE));
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     *
     * @return the instance
     */
    public static MockFacesEnvironment getInstance() {
        return instance.get();
    }

    public MockFacesEnvironment(IMocksControl mocksControl, String name) {
        this(mocksControl);
        this.name = name;
    }

    MockFacesEnvironment(IMocksControl mocksControl) {
        this.mocksControl = mocksControl;
        Class<? extends FacesContext> facesContextClass = FacesContext.class;
        try {
            facesContextClass = (Class<? extends FacesContext>) Class.forName("com.sun.faces.config.InitFacesContext");
        } catch (ClassNotFoundException e) {
        }
        facesContext = createMock(facesContextClass);
        instance.set(this);
    }

    public <T> T createMock(Class<T> mock) {
        return createMock(name, mock);
    }

    public <T> T createMock(String name, Class<T> mock) {
        return FacesMock.createMock(name, mock, mocksControl);
    }

    /*
     * public MockFacesEnvironment _(){ return this; }
     */
    public MockFacesEnvironment withExternalContext() {
        this.externalContext = createMock(ExternalContext.class);
        recordExternalContext();
        return this;
    }

    private void recordExternalContext() {
        EasyMock.expect(facesContext.getExternalContext()).andStubReturn(externalContext);
        EasyMock.expect(externalContext.getContext()).andStubReturn(null);
        EasyMock.expect(externalContext.getApplicationMap()).andStubReturn(new HashMap<String, Object>());
    }

    /*
     * public MockFacesEnvironment _(){ return this; }
     */
    public MockFacesEnvironment withELContext() {
        this.elContext = createMock(ELContext.class);
        recordELContext();
        return this;
    }

    private void recordELContext() {
        EasyMock.expect(facesContext.getELContext()).andStubReturn(elContext);
    }

    public MockFacesEnvironment withServletRequest() {
        if (null == externalContext) {
            withExternalContext();
        }
        this.context = mocksControl.createMock(ServletContext.class);
        this.request = mocksControl.createMock(HttpServletRequest.class);
        this.response = mocksControl.createMock(HttpServletResponse.class);
        recordServletRequest();
        return this;
    }

    private void recordServletRequest() {
        EasyMock.expect(externalContext.getContext()).andStubReturn(context);
        EasyMock.expect(externalContext.getRequest()).andStubReturn(request);
        EasyMock.expect(externalContext.getResponse()).andStubReturn(response);
    }

    public MockFacesEnvironment withFactories() {
        FactoryFinder.releaseFactories();

        setupAndEnhance(ApplicationFactory.class);
        setupAndEnhance(FacesContextFactory.class);
        setupAndEnhance(RenderKitFactory.class);
        setupAndEnhance(LifecycleFactory.class);

        if (jsf2) {
            setupAndEnhance(TagHandlerDelegateFactory.class);
            setupAndEnhance(ExceptionHandlerFactory.class);
            setupAndEnhance(PartialViewContextFactory.class);
            setupAndEnhance(ExternalContextFactory.class);
        }

        withFactories = true;
        return this;
    }

    /**
     * Setup and enhance.
     *
     * @param <T>
     *            the generic type
     * @param type
     *            the type
     * @return the t
     */
    private <T> T setupAndEnhance(Class<T> type) {
        String factoryName = type.getName();
        FactoryMock<T> factoryMock = service.createFactoryMock(type);
        FactoryFinder.setFactory(factoryName, factoryMock.getMockClassName());
        T mock = type.cast(FactoryFinder.getFactory(factoryName));
        service.enhance(factoryMock, mock);
        return mock;
    }

    public MockFacesEnvironment withApplication() {
        this.application = createMock(Application.class);
        this.viewHandler = createMock(ViewHandler.class);
        recordApplication();
        return this;
    }

    private void recordApplication() {
        EasyMock.expect(facesContext.getApplication()).andStubReturn(application);
        EasyMock.expect(application.getViewHandler()).andStubReturn(viewHandler);
    }

    public MockFacesEnvironment withRenderKit() {
        this.renderKit = createMock(RenderKit.class);
        this.responseStateManager = createMock(ResponseStateManager.class);
        recordRenderKit();
        return this;
    }

    private void recordRenderKit() {
        EasyMock.expect(facesContext.getRenderKit()).andStubReturn(renderKit);
        EasyMock.expect(renderKit.getResponseStateManager()).andStubReturn(responseStateManager);
    }

    public MockFacesEnvironment withReSponseWriter() {
        this.responseWriter = new RecordingResponseWriter("UTF-8", "text/html");
        recordResponseWrrier();
        return this;
    }

    private void recordResponseWrrier() {
        EasyMock.expect(facesContext.getResponseWriter()).andStubReturn(responseWriter);
    }

    public MockFacesEnvironment replay() {
        mocksControl.replay();
        MockFacesContext.setCurrentInstance(facesContext);
        return this;
    }

    public MockFacesEnvironment reset() {
        mocksControl.reset();
        recordEnvironment();
        return this;
    }

    private void recordEnvironment() {
        if (null != externalContext) {
            recordExternalContext();
        }
        if (null != elContext) {
            recordELContext();
        }
        if (null != request) {
            recordServletRequest();
        }
        if (null != application) {
            recordApplication();
        }
        if (null != renderKit) {
            recordRenderKit();
        }
        if (null != responseWriter) {
            recordResponseWrrier();
        }
        if (withFactories) {
            FactoryFinder.releaseFactories();
            withFactories();
        }
    }

    public MockFacesEnvironment resetToStrict() {
        mocksControl.resetToStrict();
        recordEnvironment();
        return this;
    }

    public MockFacesEnvironment resetToDefault() {
        mocksControl.resetToDefault();
        recordEnvironment();
        return this;
    }

    public MockFacesEnvironment resetToNice() {
        mocksControl.resetToNice();
        recordEnvironment();
        return this;
    }

    public void verify() {
        mocksControl.verify();
    }

    public void release() {
        MockFacesContext.setCurrentInstance(null);
        instance.remove();
        if (withFactories) {
            FactoryFinder.releaseFactories();
        }
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     *
     * @return the facesContext
     */
    public FacesContext getFacesContext() {
        return this.facesContext;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     *
     * @return the externalContext
     */
    public ExternalContext getExternalContext() {
        return this.externalContext;
    }

    /**
     * <p class="changed_added_4_0"></p>
     * @return the elContext
     */
    public ELContext getElContext() {
        return this.elContext;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     *
     * @return the context
     */
    public ServletContext getContext() {
        return this.context;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     *
     * @return the request
     */
    public HttpServletRequest getRequest() {
        return this.request;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     *
     * @return the response
     */
    public HttpServletResponse getResponse() {
        return this.response;
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

    /**
     * <p class="changed_added_4_0">
     * </p>
     *
     * @return the viewHandler
     */
    public ViewHandler getViewHandler() {
        return this.viewHandler;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     *
     * @return the renderKit
     */
    public RenderKit getRenderKit() {
        return this.renderKit;
    }

    /**
     * <p class="changed_added_4_0">
     * </p>
     *
     * @return the responseStateManager
     */
    public ResponseStateManager getResponseStateManager() {
        return this.responseStateManager;
    }

    /**
     * @return the responseWriter
     */
    public RecordingResponseWriter getResponseWriter() {
    	return this.responseWriter;
    }

	public IMocksControl getControl() {
        return mocksControl;
    }
}
