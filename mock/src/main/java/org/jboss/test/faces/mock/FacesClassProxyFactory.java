package org.jboss.test.faces.mock;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.easymock.ConstructorArgs;
import org.easymock.internal.ClassExtensionHelper;
import org.easymock.internal.ClassInstantiatorFactory;
import org.easymock.internal.ClassProxyFactory.MockMethodInterceptor;
import org.easymock.internal.IProxyFactory;
import org.easymock.internal.ObjectMethodsFilter;

import net.sf.cglib.core.CollectionUtils;
import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.core.VisibilityPredicate;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * Factory generating a mock for a class.
 * <p>
 * Note that this class is stateful
 */
public class FacesClassProxyFactory<T> implements IProxyFactory<T> {

    @SuppressWarnings("unchecked")
    public T createProxy(Class<T> toMock, final InvocationHandler handler) {

        // Dirty trick to fix ObjectMethodsFilter
        // It will replace the equals, hashCode, toString methods it kept that
        // are the ones
        // from Object.class by the correct ones since they might have been
        // overloaded
        // in the mocked class.
        try {
            updateMethod(handler, toMock.getMethod("equals",
                    new Class[] { Object.class }));
            updateMethod(handler, toMock.getMethod("hashCode", new Class[0]));
            updateMethod(handler, toMock.getMethod("toString", new Class[0]));
        } catch (NoSuchMethodException e) {
            // ///CLOVER:OFF
            throw new InternalError(
                    "We strangly failed to retrieve methods that always exist on an object...");
            // ///CLOVER:ON
        }

        MethodInterceptor interceptor = new MockMethodInterceptor(handler);

        // Create the mock
        Enhancer enhancer = new Enhancer() {
            /**
             * Filter all private constructors but do not check that there are
             * some left
             */
            @Override
            protected void filterConstructors(Class sc, List constructors) {
                CollectionUtils.filter(constructors, new VisibilityPredicate(
                        sc, true));
            }
        };
        enhancer.setSuperclass(toMock);
        enhancer.setCallbackType(interceptor.getClass());
        enhancer.setNamingPolicy(new DefaultNamingPolicy() {
            @Override
            public String getClassName(String prefix, String source, Object key, Predicate names) {
                return "jsftest." + super.getClassName(prefix, source, key, names);
            }
        });

        Class mockClass = enhancer.createClass();
        Enhancer.registerCallbacks(mockClass, new Callback[] { interceptor });

        if (ClassExtensionHelper.getCurrentConstructorArgs() != null) {
            // Really instantiate the class
            ConstructorArgs args = ClassExtensionHelper
                    .getCurrentConstructorArgs();
            Constructor cstr;
            try {
                // Get the constructor with the same params
                cstr = mockClass.getDeclaredConstructor(args.getConstructor()
                        .getParameterTypes());
            } catch (NoSuchMethodException e) {
                // Shouldn't happen, constructor is checked when ConstructorArgs is instantiated
                // ///CLOVER:OFF
                throw new RuntimeException(
                        "Fail to find constructor for param types", e);
                // ///CLOVER:ON
            }
            T mock;
            try {
                cstr.setAccessible(true); // So we can call a protected
                // constructor
                mock = (T) cstr.newInstance(args.getInitArgs());
            } catch (InstantiationException e) {
                // ///CLOVER:OFF
                throw new RuntimeException(
                        "Failed to instantiate mock calling constructor", e);
                // ///CLOVER:ON
            } catch (IllegalAccessException e) {
                // ///CLOVER:OFF
                throw new RuntimeException(
                        "Failed to instantiate mock calling constructor", e);
                // ///CLOVER:ON
            } catch (InvocationTargetException e) {
                throw new RuntimeException(
                        "Failed to instantiate mock calling constructor: Exception in constructor",
                        e.getTargetException());
            }
            return mock;
        } else {
            // Do not call any constructor

            Factory mock;
            try {
                mock = (Factory) ClassInstantiatorFactory.getInstantiator()
                        .newInstance(mockClass);
            } catch (InstantiationException e) {
                // ///CLOVER:OFF
                throw new RuntimeException("Fail to instantiate mock for "
                        + toMock + " on " + ClassInstantiatorFactory.getJVM()
                        + " JVM");
                // ///CLOVER:ON
            }

            // This call is required. CGlib has some "magic code" making sure a
            // callback is used by only one instance of a given class. So only
            // the
            // instance created right after registering the callback will get
            // it.
            // However, this is done in the constructor which I'm bypassing to
            // allow class instantiation without calling a constructor.
            // Fortunately, the "magic code" is also called in getCallback which
            // is
            // why I'm calling it here mock.getCallback(0);
            mock.getCallback(0);

            return (T) mock;
        }
    }

    private void updateMethod(InvocationHandler objectMethodsFilter,
            Method correctMethod) {
        Field methodField = retrieveField(ObjectMethodsFilter.class,
                correctMethod.getName() + "Method");
        updateField(objectMethodsFilter, correctMethod, methodField);
    }

    private Field retrieveField(Class<?> clazz, String field) {
        try {
            return clazz.getDeclaredField(field);
        } catch (NoSuchFieldException e) {
            // ///CLOVER:OFF
            throw new InternalError(
                    "There must be some refactoring because the " + field
                            + " field was there...");
            // ///CLOVER:ON
        }
    }

    private void updateField(Object instance, Object value, Field field) {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            // ///CLOVER:OFF
            throw new InternalError(
                    "Should be accessible since we set it ourselves");
            // ///CLOVER:ON
        }
        field.setAccessible(accessible);
    }
}
