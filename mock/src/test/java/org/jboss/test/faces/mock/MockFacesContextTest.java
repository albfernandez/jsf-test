package org.jboss.test.faces.mock;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Ignore;
import org.junit.Test;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;

public class MockFacesContextTest {

    @Test
    @Ignore
    public void testCreate() throws Exception {
        FacesContext facesContext = FacesMock.createMock(FacesContext.class);
        expect(facesContext.getMaximumSeverity()).andReturn(FacesMessage.SEVERITY_INFO);
        ExternalContext externalContext = FacesMock.createMock(ExternalContext.class);
        expect(facesContext.getExternalContext()).andReturn(externalContext);
        FacesMock.replay(facesContext,externalContext);
        FacesContext facesContext2 = FacesContext.getCurrentInstance();
        assertSame(facesContext, facesContext2);
        assertEquals(FacesMessage.SEVERITY_INFO, facesContext2.getMaximumSeverity());
        assertSame(externalContext, facesContext2.getExternalContext());
        FacesMock.verify(facesContext,externalContext);
    }
}
