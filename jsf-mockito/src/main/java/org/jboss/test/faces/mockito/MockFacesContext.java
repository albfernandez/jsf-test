/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.test.faces.mockito;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.el.ELContext;
import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.FacesMessage.Severity;
import jakarta.faces.application.ProjectStage;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.PartialViewContext;
import jakarta.faces.context.ResponseStream;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.event.PhaseId;
import jakarta.faces.lifecycle.Lifecycle;
import jakarta.faces.render.RenderKit;

/**
 * <p>
 * The mocked {@link FacesContext} instance.
 * </p>
 * 
 * <p>
 * All methods are stubbed (provides no functionality) except {@link #setCurrentInstance(FacesContext)} and
 * {@link #release()} which are delegated to real {@link FacesContext} implementation to set and reset current context
 * in mocked environment.
 * </p>
 * 
 * @author <a href="mailto:lfryc@redhat.com">Lukas Fryc</a>
 */
public class MockFacesContext extends FacesContext {

    /**
     * Sets the current context of {@link FacesContext}.
     * 
     * @param context
     *            the current context of {@link FacesContext}.
     */
    public static void setCurrentInstance(FacesContext context) {
        FacesContext.setCurrentInstance(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#release()
     */
    @Override
    public void release() {
        FacesContext.setCurrentInstance(null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#getApplication()
     */
    @Override
    public Application getApplication() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#getClientIdsWithMessages()
     */
    @Override
    public Iterator<String> getClientIdsWithMessages() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#getExternalContext()
     */
    @Override
    public ExternalContext getExternalContext() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#getMaximumSeverity()
     */
    @Override
    public Severity getMaximumSeverity() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#getMessages()
     */
    @Override
    public Iterator<FacesMessage> getMessages() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#getMessages(java.lang.String)
     */
    @Override
    public Iterator<FacesMessage> getMessages(String clientId) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#getRenderKit()
     */
    @Override
    public RenderKit getRenderKit() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#getRenderResponse()
     */
    @Override
    public boolean getRenderResponse() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#getResponseComplete()
     */
    @Override
    public boolean getResponseComplete() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#getResponseStream()
     */
    @Override
    public ResponseStream getResponseStream() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#setResponseStream(jakarta.faces.context.ResponseStream)
     */
    @Override
    public void setResponseStream(ResponseStream responseStream) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#getResponseWriter()
     */
    @Override
    public ResponseWriter getResponseWriter() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#setResponseWriter(jakarta.faces.context.ResponseWriter)
     */
    @Override
    public void setResponseWriter(ResponseWriter responseWriter) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#getViewRoot()
     */
    @Override
    public UIViewRoot getViewRoot() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#setViewRoot(jakarta.faces.component.UIViewRoot)
     */
    @Override
    public void setViewRoot(UIViewRoot root) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#addMessage(java.lang.String, jakarta.faces.application.FacesMessage)
     */
    @Override
    public void addMessage(String clientId, FacesMessage message) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#renderResponse()
     */
    @Override
    public void renderResponse() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#responseComplete()
     */
    @Override
    public void responseComplete() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#getAttributes()
     */
    @Override
    public Map<Object, Object> getAttributes() {
        return Collections.emptyMap();
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#getPartialViewContext()
     */
    @Override
    public PartialViewContext getPartialViewContext() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#getELContext()
     */
    @Override
    public ELContext getELContext() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#getExceptionHandler()
     */
    @Override
    public ExceptionHandler getExceptionHandler() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#setExceptionHandler(jakarta.faces.context.ExceptionHandler)
     */
    @Override
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#getMessageList()
     */
    @Override
    public List<FacesMessage> getMessageList() {
        return Collections.emptyList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#getMessageList(java.lang.String)
     */
    @Override
    public List<FacesMessage> getMessageList(String clientId) {
        return Collections.emptyList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#isValidationFailed()
     */
    @Override
    public boolean isValidationFailed() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#isPostback()
     */
    @Override
    public boolean isPostback() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#validationFailed()
     */
    @Override
    public void validationFailed() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#getCurrentPhaseId()
     */
    @Override
    public PhaseId getCurrentPhaseId() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#setCurrentPhaseId(jakarta.faces.event.PhaseId)
     */
    @Override
    public void setCurrentPhaseId(PhaseId currentPhaseId) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#setProcessingEvents(boolean)
     */
    @Override
    public void setProcessingEvents(boolean processingEvents) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#isProcessingEvents()
     */
    @Override
    public boolean isProcessingEvents() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.faces.context.FacesContext#isProjectStage(jakarta.faces.application.ProjectStage)
     */
    @Override
    public boolean isProjectStage(ProjectStage stage) {
        return false;
    }

	@Override
	public Lifecycle getLifecycle() {
		// TODO Auto-generated method stub
		return null;
	}

}
