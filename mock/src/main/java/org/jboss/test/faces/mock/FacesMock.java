package org.jboss.test.faces.mock;

import java.lang.reflect.Proxy;

import org.easymock.EasyMockSupport;
import org.easymock.IMocksControl;
import org.easymock.MockType;
//MZ import org.easymock.internal.ClassExtensionHelper;
import org.easymock.internal.MocksControl;
import org.easymock.internal.ObjectMethodsFilter;
/**
 * <p class="changed_added_4_0"></p>
 * @author asmirnov@exadel.com
 *
 */
public class FacesMock {

    private FacesMock() {
        //hidden constructor
    }

    public static org.easymock.IMocksControl createControl(){
        return new FacesMocksClassControl(MockType.DEFAULT);
    }

    public static MockFacesEnvironment createMockEnvironment(){
        return new MockFacesEnvironment(new FacesMocksClassControl(MockType.DEFAULT));
    }

    public static MockFacesEnvironment createNiceEnvironment(){
        return new MockFacesEnvironment(new FacesMocksClassControl(MockType.NICE));
    }

    public static MockFacesEnvironment createStrictEnvironment(){
        return new MockFacesEnvironment(new FacesMocksClassControl(MockType.STRICT));
    }

    @SuppressWarnings("unchecked")
	public static <T> T createMock(String name, Class<T> clazz, IMocksControl control) {
        if (clazz == MockFacesEnvironment.class) {
            return (T) new MockFacesEnvironment(control, name);
        }
        return control.createMock(name, clazz);
    }

    public static <T> T createMock(Class<T> clazz){
        return createMock(null, clazz);
    }

    public static <T> T createMock(String name, Class<T> clazz){
        return createMock(name, clazz, new FacesMocksClassControl(MockType.DEFAULT));
    }

    public static <T> T createNiceMock(Class<T> clazz){
        return createNiceMock(null, clazz);
    }

    public static <T> T createNiceMock(String name, Class<T> clazz) {
        return createMock(name, clazz, new FacesMocksClassControl(MockType.NICE));
    }

    public static <T> T createStrictMock(Class<T> clazz){
        return createStrictMock(null, clazz);
    }

    public static <T> T createStrictMock(String name, Class<T> clazz){
        return createMock(name, clazz, new FacesMocksClassControl(MockType.STRICT));
    }

    
    private static MocksControl getControl(Object mock) {
        ObjectMethodsFilter handler;

        try {
            //MZ if (Enhancer.isEnhanced(mock.getClass())) {
                //MZ handler = (ObjectMethodsFilter) getInterceptor(mock).getHandler();
            //MZ } else 
        	if (EasyMockSupport.isAMock(mock)) {
        		return MocksControl.getControl(mock);
        	} else if (Proxy.isProxyClass(mock.getClass())) {
                handler = (ObjectMethodsFilter) Proxy.getInvocationHandler(mock);
            } else {
                throw new IllegalArgumentException("Not a mock: " + mock.getClass().getName());
            }
            return handler.getDelegate().getControl();
        } catch (final ClassCastException e) {
            throw new IllegalArgumentException("Not a mock: " + mock.getClass().getName());
        }
    }
    
    
    /*MZ
    private static boolean isMockClass(Class<?> clazz){
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> interfaze : interfaces) {
            if("MockObject".equals(interfaze.getSimpleName()) && null !=interfaze.getEnclosingClass()){
                return true;
            }
        }
        return false;
    }
    */

    public static void replay(Object... mocks) {
        for (Object mock : mocks) {
            getControl(mock).replay();
        }
    }

    public static void reset(Object... mocks) {
        for (Object mock : mocks) {
            getControl(mock).reset();
        }
    }

    /**
     * Resets the given mock objects (more exactly: the controls of the mock
     * objects) and turn them to a mock with nice behavior. For details, see
     * the EasyMock documentation.
     *
     * @param mocks
     *            the mock objects
     */
    public static void resetToNice(Object... mocks) {
        for (Object mock : mocks) {
            getControl(mock).resetToNice();
        }
    }

    /**
     * Resets the given mock objects (more exactly: the controls of the mock
     * objects) and turn them to a mock with default behavior. For details, see
     * the EasyMock documentation.
     *
     * @param mocks
     *            the mock objects
     */
    public static void resetToDefault(Object... mocks) {
        for (Object mock : mocks) {
            getControl(mock).resetToDefault();
        }
    }

    /**
     * Resets the given mock objects (more exactly: the controls of the mock
     * objects) and turn them to a mock with strict behavior. For details, see
     * the EasyMock documentation.
     *
     * @param mocks
     *            the mock objects
     */
    public static void resetToStrict(Object... mocks) {
        for (Object mock : mocks) {
            getControl(mock).resetToStrict();
        }
    }

    public static void verify(Object... mocks) {
        for (Object object : mocks) {
            getControl(object).verify();
        }
    }

    /**
     * Switches order checking of the given mock object (more exactly: the
     * control of the mock object) the on and off. For details, see the EasyMock
     * documentation.
     *
     * @param mock
     *            the mock object.
     * @param state
     *            <code>true</code> switches order checking on,
     *            <code>false</code> switches it off.
     */
    public static void checkOrder(Object mock, boolean state) {
        getControl(mock).checkOrder(state);
    }

}
