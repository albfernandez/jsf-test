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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import jakarta.faces.component.UIData;


/**
 * <p class="changed_added_4_0"></p>
 * @author asmirnov@exadel.com
 *
 */
public class TestMockController {
    
    @Test
    public void testCreateMock() throws Exception {
        UIData uiData = FacesMock.createMock(UIData.class);
        assertTrue(uiData instanceof UIData);
        expect(uiData.getClientId()).andReturn("foo");
        expect(uiData.getRowCount()).andReturn(5);
        FacesMock.replay(uiData);
        assertEquals("foo", uiData.getClientId());
        assertEquals(5, uiData.getRowCount());
        FacesMock.verify(uiData);
    }

}
