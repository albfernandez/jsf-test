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



/**
 * <p class="changed_added_4_0">Instance of an Object implemented this interface can be injected into test class to simplify mock objects manipulations.</p>
 * @author asmirnov@exadel.com
 *
 */
public interface MockController {
    
    <T> T createMock(Class<T> clazz);
    
    <T> T createMock(String name, Class<T> clazz);

    <T> T createNiceMock(Class<T> clazz);

    <T> T createNiceMock(String name, Class<T> clazz);
    
    <T> T createStrictMock(Class<T> clazz);
    
    <T> T createStrictMock(String name, Class<T> clazz);
    /**
     * <p class="changed_added_4_0">Repaly all mock objects created by the {@link CdkTestRunner} and ones from parameters</p>
     */
    public void replay(Object ...objects);
    
    /**
     * <p class="changed_added_4_0">Verify all mock objects created by the {@link CdkTestRunner} and ones from parameters</p>
     */
    public void verify(Object ...objects);

    /**
     * <p class="changed_added_4_0">Reset all mock objects created by the {@link CdkTestRunner} and ones from patameters</p>
     */
    public void reset(Object ...objects);
    public void resetToNice(Object ...objects);
    public void resetToStrict(Object ...objects);
    public void resetToDefault(Object ...objects);
    
    /**
     * Release the mock {@link MockFacesEnvironment} environment created by the test runner.
     */
    public void release();
}
