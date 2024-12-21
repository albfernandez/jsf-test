package org.jboss.test.faces.mock;

import java.lang.reflect.Method;

import org.easymock.ConstructorArgs;
import org.easymock.EasyMockSupport;
import org.easymock.IMocksControl;
import org.easymock.MockType;
import org.easymock.internal.IProxyFactory;
import org.easymock.internal.MocksControl;

public class FacesMocksClassControl extends MocksControl implements IMocksControl {

    private static final long serialVersionUID = -6032144451192179422L;
    
    private EasyMockSupport support = new EasyMockSupport();

    
    public FacesMocksClassControl(MockType type) {
        super(type);
    }

    
    public <T, R> R createMock(String name, Class<T> toMock,
            Method... mockedMethods) {

        if(toMock.isInterface()) {
            throw new IllegalArgumentException("Partial mocking doesn't make sense for interface");
        }

        R  mock = support.partialMockBuilder(toMock).addMockedMethods(mockedMethods).createMock(name); 		//MZ createMock(name, toMock);

        // Set the mocked methods on the interceptor
        //MZ getInterceptor(mock).setMockedMethods(mockedMethods);
        

        return mock;
    }

    public <T, R> R  createMock(Class<T> toMock, Method... mockedMethods) {

        if(toMock.isInterface()) {
            throw new IllegalArgumentException("Partial mocking doesn't make sense for interface");
        }

        R mock = support.partialMockBuilder(toMock).addMockedMethods(mockedMethods).createMock();	//MZ createMock(toMock);

        // Set the mocked methods on the interceptor
        //MZ getInterceptor(mock).setMockedMethods(mockedMethods);

        return mock;
    }

    
    public <T, R> R createMock(Class<T> toMock, ConstructorArgs constructorArgs,
            Method... mockedMethods) {
    	//MZ // Trick to allow the ClassProxyFactory to access constructor args
    	//MZ setCurrentConstructorArgs(constructorArgs);
    	//MZ try {
    		return super.createMock(null, toMock, constructorArgs, mockedMethods);
          //MZ } finally {
          //MZ    setCurrentConstructorArgs(null);
          //MZ }
    }

    public <T, R> R createMock(String name, Class<T> toMock,
            ConstructorArgs constructorArgs, Method... mockedMethods) {
    	//MZ // Trick to allow the ClassProxyFactory to access constructor args
    	//MZ setCurrentConstructorArgs(constructorArgs);
    	//MZ try {
    	return super.createMock(name, toMock, constructorArgs, mockedMethods);
          //MZ } finally {
          //MZ setCurrentConstructorArgs(null);
          //MZ }
    }
	
	
	
	
	protected <T> IProxyFactory createProxyFactory(Class<T> toMock) {
        if (toMock.isInterface()) {
            return super.getProxyFactory(toMock);
        }
        return new FacesClassProxyFactory();
    }
	
	
}
