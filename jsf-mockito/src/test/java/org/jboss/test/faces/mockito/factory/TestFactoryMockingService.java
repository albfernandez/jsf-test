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
package org.jboss.test.faces.mockito.factory;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.MockUtil;

import jakarta.faces.FactoryFinder;
import jakarta.faces.application.Application;
import jakarta.faces.application.ApplicationFactory;

/**
 * The Class TestFactoryMockingService.
 */
public class TestFactoryMockingService {

    /**
     * Test factory.
     */
    @Test
    public void testFactory() {
        FactoryMockingService service = FactoryMockingService.getInstance();
        FactoryMock<ApplicationFactory> mockFactory = service.createFactoryMock(ApplicationFactory.class);
        
        String mockclassname = mockFactory.getMockClassName();

        System.out.println("mockclassname: " + mockclassname);
        
        FactoryFinder.setFactory(FactoryFinder.APPLICATION_FACTORY, mockclassname);

        //TODO spy here??
        ApplicationFactory newMock = Mockito.spy((ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY));
               
        System.out.println("newMockclassname" + newMock.getClass().getName());
        
        System.out.println("newMock getApplication: " + newMock.getApplication());
        
        System.out.println("Tismock" + MockUtil.isMock(newMock));
    	
    	System.out.println("Tisspy" + MockUtil.isSpy(newMock));

    	//TODO currentlz noop
    	service.enhance(mockFactory, newMock);
        
        Application application = mock(Application.class);
    	
    	System.out.println("T2ismock" + MockUtil.isMock(application));
    	
    	System.out.println("T2isspy" + MockUtil.isSpy(application));
        
        when(newMock.getApplication()).thenReturn(application);

        assertSame(newMock.getApplication(), application);
        
        //TODO not working
        //MZ ApplicationFactory newMock2 = (ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
        
        // System.out.println("newMock2classname" + newMock2.getClass().getName());
        
        // System.out.println("newMock2 getApplication: " + newMock2.getApplication());

        //MZ assertSame(newMock2.getApplication(), application);
    }
}
