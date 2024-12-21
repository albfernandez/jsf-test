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
package org.jboss.test.faces.mock.factory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import org.easymock.EasyMock;
import org.easymock.internal.ClassInstantiatorFactory;
import org.easymock.internal.ClassMockingData;

/**
 * The implementation of factory mocks - mocks which needs to be created by constructing class instance by class name (e.g.
 * {@see Class#forName(String)).
 * 
 * @param <T>
 *            the generic type
 */
public class FactoryMockImpl<T> implements FactoryMock<T> {
	
	private static final String CALLBACK_FIELD = "$callback";

    /** The message thrown when compatibility issue of used Mockito version are detected */
    //MZ private final static String MOCKITO_COMPATIBILITY_MESSAGE = "Mockito internals has changed and this code not anymore compatible";

    /** The original class. */
    private Class<T> originalClass;

    /** The mock. */
    private T mock;

    /**
     * Instantiates a new factory mock impl.
     * 
     * @param type
     *            the type
     */
    FactoryMockImpl(Class<T> type) {
        this.originalClass = type;
        initialize();
    }

    /**
     * Initialize.
     */
    private void initialize() {
        mock = EasyMock.createMock(originalClass);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.test.faces.mockito.factory.FactoryMock#getMockClass()
     */
    @SuppressWarnings("unchecked")
    public Class<T> getMockClass() {
        return (Class<T>) mock.getClass();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.test.faces.mockito.factory.FactoryMock#getMockName()
     */
    public String getMockClassName() {
        return mock.getClass().getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.test.faces.mockito.factory.FactoryMock#createNewMockInstance()
     */
    @SuppressWarnings("unchecked")
    public T createNewMockInstance() {
        try {
            return (T) ClassInstantiatorFactory.getInstantiator().newInstance(mock.getClass()); //MZ mock.getClass().newInstance();
        //MZ } catch (IllegalAccessException e) {
            //MZ throw new IllegalStateException("Cannot create instance for mock factory of class '" + originalClass + "'",
                //MZ e);
        } catch (InstantiationException e) {
            throw new IllegalStateException("Cannot create instance for mock factory of class '" + originalClass + "'",
                e);
        }
    }

    /**
     * Enhance the mock class instance with mocking ability.
     * 
     * @param mockToEnhance
     *            the mock to enhance
     */
    void enhance(T mockToEnhance) {
    	
    	//MZ MockMethodInterceptor interceptor = ClassExtensionHelper.getInterceptor(mock);
       	
    	//MZ ((Factory) mockToEnhance).setCallbacks(new Callback[] { interceptor, SerializableNoOp.SERIALIZABLE_INSTANCE });
    	
    	try {
    		
    		MethodHandle mockCallbackGetter = MethodHandles.lookup().findGetter(mock.getClass(), CALLBACK_FIELD, ClassMockingData.class);
    		
            MethodHandle mockToEnhanceCallbackSetter = MethodHandles.lookup().findSetter(mockToEnhance.getClass(), CALLBACK_FIELD, ClassMockingData.class);
            
            try {
            	
            	mockToEnhanceCallbackSetter.invoke(mockToEnhance, mockCallbackGetter.invoke(mock));
            
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    	
    }

    /**
     * Gets the declared field from type.
     * 
     * @param type
     *            the type
     * @param fieldName
     *            the field name
     * @return the declared field from type
     */
    /*MZ
    private Field getDeclaredFieldFromType(Class<?> type, String fieldName) {
        try {
            return type.getDeclaredField(fieldName);
        } catch (Exception e) {
            throw new IllegalStateException(MOCKITO_COMPATIBILITY_MESSAGE, e);
        }
    }
    */


    /**
     * Gets the object from declared field.
     * 
     * @param instance
     *            the instance
     * @param field
     *            the field
     * @return the object from declared field
     */
    /*MZ
    private Object getObjectFromDeclaredField(Object instance, Field field) {
        boolean accessible = field.isAccessible();
        Object result = null;
        if (!accessible) {
            field.setAccessible(true);
        }
        try {
            result = field.get(instance);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            if (!accessible) {
                field.setAccessible(false);
            }
        }
        return result;
    }
    */
    
}
