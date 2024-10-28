/**
 * 
 */
package org.jboss.test.faces.staging;

import java.util.EventListener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


/**
 * Listener interface to inspect all calls to {@link HttpServletRequest} , {@link HttpServletResponse}, {@link HttpSession} and {@link ServletContext} objects.
 * @author asmirnov
 *
 */
public interface InvocationListener extends EventListener{

	/**
	 * This metod called after successful invocation on the target object.
	 * @param invocationEvent
	 */
	public void afterInvoke(InvocationEvent invocationEvent);
	
	/**
	 * This method called after any {@link Throwable} thrown during method invocation.
	 * @param invocationErrorEvent
	 */
	public void processException(InvocationErrorEvent invocationErrorEvent);
	
}
