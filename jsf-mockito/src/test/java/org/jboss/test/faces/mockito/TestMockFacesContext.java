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

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jakarta.faces.application.Application;
import jakarta.faces.context.FacesContext;

/**
 * The Class TestMockFacesContext.
 */
public class TestMockFacesContext {

    /** The environment. */
    private MockFacesEnvironment environment;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        environment = new MockFacesEnvironment();
    }

    /**
     * Tear down.
     */
    @After
    public void tearDown() {
        environment.release();
        environment = null;
    }

    /**
     * Test mocking faces context.
     */
    @Test
    public void testMockingFacesContext() {
        FacesContext facesContext = environment.getFacesContext();

        assertSame(facesContext, FacesContext.getCurrentInstance());
        environment.release();
    }

    /**
     * Test stubbing faces context.
     */
    @Test
    public void testStubbingFacesContext() {
        FacesContext facesContext = environment.getFacesContext();
        Application application = mock(Application.class);
        when(facesContext.getApplication()).thenReturn(application);

        assertSame(application, FacesContext.getCurrentInstance().getApplication());
        environment.release();
    }

}
