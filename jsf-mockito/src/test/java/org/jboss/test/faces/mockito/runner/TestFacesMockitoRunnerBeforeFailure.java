package org.jboss.test.faces.mockito.runner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.mockito.junit.MockitoJUnitRunner;

import jakarta.faces.context.FacesContext;

public class TestFacesMockitoRunnerBeforeFailure {

    @Test
    public void test() throws InitializationError {
        try {
            new MockitoJUnitRunner(PrivateTest.class).run(new RunNotifier());
        } catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            assertNull("FacesContext should be cleared after failed test", FacesContext.getCurrentInstance());
            assertTrue("@Before should be invoked", PrivateTest.beforeWasCalled);
            assertFalse("@Test should not be invoked", PrivateTest.testWasCalled);
            assertTrue("@After should be invoked", PrivateTest.afterWasCalled);
            assertTrue("@AfterClass should be invoked", PrivateTest.afterClassWasCalled);
        }
    }

    @Ignore("PrivateTest will be ran from outside")
    @RunWith(FacesMockitoRunner.class)
    public static class PrivateTest {

        private static boolean beforeWasCalled = false;
        private static boolean testWasCalled = false;
        private static boolean afterWasCalled = false;
        private static boolean afterClassWasCalled = false;

        @Before
        public void setUp() {
            beforeWasCalled = true;
            assertNotNull(FacesContext.getCurrentInstance());
            throw new AfterFailureException();
        }

        @Test
        public void privateTest() {
            testWasCalled = true;
        }

        @After
        public void tearDown() {
            afterWasCalled = true;
        }

        @AfterClass
        public static void verify() {
            afterClassWasCalled = true;
        }

    }

    public static class AfterFailureException extends RuntimeException {
    }
}
