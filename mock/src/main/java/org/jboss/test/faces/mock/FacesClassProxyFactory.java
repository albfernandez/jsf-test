package org.jboss.test.faces.mock;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

import org.easymock.ConstructorArgs;
import org.easymock.internal.ClassInstantiatorFactory;
import org.easymock.internal.ClassMockingData;
import org.easymock.internal.ClassProxyFactory.MockMethodInterceptor;
import org.easymock.internal.IProxyFactory;
import org.easymock.internal.classinfoprovider.ClassInfoProvider;
import org.easymock.internal.classinfoprovider.DefaultClassInfoProvider;
import org.easymock.internal.classinfoprovider.JdkClassInfoProvider;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.TypeCache;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.SyntheticState;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * Factory generating a mock for a class.
 * <p>
 * Note that this class is stateful
 */
public class FacesClassProxyFactory implements IProxyFactory {

	/* MZ 
	 * Attribution note:
	 * 
	 * This class was refactored with code from:
	 * https://github.com/easymock/easymock/blob/easymock-5.4.0/core/src/main/java/org/easymock/internal/ClassProxyFactory.java
	*/
	
	private static final String CALLBACK_FIELD = "$callback";
    private static final ClassInfoProvider[] defaultClassInfoProviders = { new DefaultClassInfoProvider() , new JdkClassInfoProvider() };
    private static final ClassInfoProvider[] jdkClassInfoProviders = { defaultClassInfoProviders[1], defaultClassInfoProviders[0] };
    
    private final TypeCache<Class<?>> typeCache = new TypeCache.WithInlineExpunction<>();
    
    private static final AtomicInteger id = new AtomicInteger(0);

    private static final ThreadLocal<ClassMockingData> currentData = new ThreadLocal<>();
	

    @Override
    public <T> T createProxy(final Class<T> toMock, InvocationHandler handler, Method[] mockedMethods, ConstructorArgs args) {
        Throwable kept = null;
        // We pick the provider we think will work
        // But we still loop around all the providers just in case we are wrong when picking but that one will eventually work
        ClassInfoProvider[] providers = isJdkClassOrWithoutPackage(toMock) ? jdkClassInfoProviders : defaultClassInfoProviders;
        for (ClassInfoProvider provider : providers) {
            try {
                return doCreateProxy(toMock, handler, provider, mockedMethods, args);
            } catch (Error | RuntimeException e) {
                kept = e;
            }
        }
        if (kept instanceof Error) {
            throw (Error) kept;
        }
        throw (RuntimeException) kept;
    }
    
    
	@SuppressWarnings("unchecked")
	private <T> T doCreateProxy(Class<T> toMock, InvocationHandler handler, ClassInfoProvider provider, Method[] mockedMethods, ConstructorArgs args) {
		
        ElementMatcher.Junction<MethodDescription> junction = ElementMatchers.any();

        ClassLoader classLoader = provider.classLoader(toMock);
        Class<?> mockClass = typeCache.findOrInsert(classLoader, toMock,  () -> {

                try (DynamicType.Unloaded<T> unloaded = new ByteBuddy()
                    .subclass(toMock)
                    .name("jsftest." + provider.classPackage(toMock) + toMock.getSimpleName() + "$$$EasyMock$" + id.incrementAndGet())
                    .defineField(CALLBACK_FIELD, ClassMockingData.class, SyntheticState.SYNTHETIC, Visibility.PUBLIC)
                    .method(junction)
                    .intercept(MethodDelegation.to(MockMethodInterceptor.class))
                    .make()) {
                    return unloaded
                        .load(classLoader, classLoadingStrategy())
                        .getLoaded();
                }
            });

        T mock;

        ClassMockingData classMockingData = new ClassMockingData(handler, mockedMethods);

        if (args != null) {
            // Really instantiate the class
            Constructor<?> cstr;
            try {
                // Get the constructor with the same params
                cstr = mockClass.getDeclaredConstructor(args.getConstructor().getParameterTypes());
            } catch (NoSuchMethodException e) {
                // Shouldn't happen, constructor is checked when ConstructorArgs is instantiated
                // ///CLOVER:OFF
                throw new RuntimeException("Fail to find constructor for param types", e);
                // ///CLOVER:ON
            }
            try {
                cstr.setAccessible(true); // So we can call a protected
                // Call the constructor. The handler needs to know the mockedMethods but the callback field is not set yet
                // So we put them in thread-local for this really special case
                currentData.set(classMockingData);
                try {
                    mock = (T) cstr.newInstance(args.getInitArgs());
                } finally {
                    currentData.remove();
                }
            } catch (InstantiationException | IllegalAccessException e) {
                // ///CLOVER:OFF
                throw new RuntimeException("Failed to instantiate mock calling constructor", e);
                // ///CLOVER:ON
            } catch (InvocationTargetException e) {
                throw new RuntimeException(
                        "Failed to instantiate mock calling constructor: Exception in constructor",
                        e.getTargetException());
            }
        } else {
            // Do not call any constructor
            try {
                mock = (T) ClassInstantiatorFactory.getInstantiator().newInstance(mockClass);
            } catch (InstantiationException e) {
                // ///CLOVER:OFF
                throw new RuntimeException("Fail to instantiate mock for " + toMock + " on "
                        + ClassInstantiatorFactory.getJVM() + " JVM");
                // ///CLOVER:ON
            }
        }

        MethodHandle callbackField = getCallbackSetter(mock);
        try {
            callbackField.invoke(mock, classMockingData);
        } catch (Error | RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        return mock;
    }
        
        /* MZ
        
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
       

        MethodInterceptor interceptor = new MockMethodInterceptor();

        // Create the mock
        Enhancer enhancer = new Enhancer() {
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
            
            */
        

	/*MZ
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
	*/
	
	/*MZ
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
	*/

	@Override
	public InvocationHandler getInvocationHandler(Object mock) {
		return Proxy.getInvocationHandler(mock);
	}
	
	
	private static <T> boolean isJdkClassOrWithoutPackage(Class<T> toMock) {
        // null class loader means we are from the bootstrap class loader, the mocks will go in another package in class loader
        // we need to verify for null since some dynamic classes have no package
        // and I still verify for .java, which isn't perfect but a start, for classes hacked to another class loader like PowerMock does
        if (toMock.getPackage() == null || toMock.getClassLoader() == null) {
            return true;
        }
        String name = toMock.getName();
        // Here, we just try to guess it's coming from the JDK. Some might not be. "javax" in particular
        // But since we will try both provider, it will work in the end. It's just a matter of which one we try first
        return name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("com.sun.") || name.startsWith("jdk.");
    }
	
	
	private ClassLoadingStrategy<ClassLoader> classLoadingStrategy() {
        if (ClassInjector.UsingUnsafe.isAvailable()) {
            return new ClassLoadingStrategy.ForUnsafeInjection();
        }
        // I don't think this helps much. It was an attempt to help OSGi, but it doesn't work.
        // Right now, everything is using Unsafe to we never get there
        return ClassLoadingStrategy.UsingLookup.of(MethodHandles.lookup());
    }
	
	/*
	private static MethodHandle getCallbackGetter(Object mock) {
        try {
            return MethodHandles.lookup().findGetter(mock.getClass(), CALLBACK_FIELD, ClassMockingData.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    */

    private static MethodHandle getCallbackSetter(Object mock) {
        try {
            return MethodHandles.lookup().findSetter(mock.getClass(), CALLBACK_FIELD, ClassMockingData.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
	
}
