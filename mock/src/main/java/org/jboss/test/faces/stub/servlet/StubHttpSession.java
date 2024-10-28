/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.test.faces.stub.servlet;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.jboss.test.faces.stub.util.IteratorEnumeration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

/**
 * @author Gavin King
 * @author Thomas Heute
 * @version $Revision$
 */
@SuppressWarnings("deprecation")
public class StubHttpSession implements HttpSession
{
   
   private Map<String, Object> attributes = new HashMap<String, Object>();
   private boolean isInvalid;
   private ServletContext servletContext;
   
   public StubHttpSession() {}
   
   public StubHttpSession(ServletContext servletContext) 
   {
      this.servletContext = servletContext;
   }
   
   public boolean isInvalid()
   {
      return isInvalid;
   }

   public long getCreationTime()
   {
      return 0;
   }

   public String getId()
   {
      return null;
   }

   public long getLastAccessedTime()
   {
      return 0;
   }
   
   private int maxInactiveInterval;

   public void setMaxInactiveInterval(int max)
   {
      maxInactiveInterval = max;
   }

   public int getMaxInactiveInterval()
   {
      return maxInactiveInterval;
   }
   
   /*MZ
   public HttpSessionContex getSessionContext()
   {
      throw new UnsupportedOperationException();
   }
   */

   public Object getAttribute(String att)
   {
      return attributes.get(att);
   }

   public Object getValue(String att)
   {
      return getAttribute(att);
   }

   public Enumeration getAttributeNames()
   {
      return new IteratorEnumeration( attributes.keySet().iterator() );
   }

   public String[] getValueNames()
   {
      return attributes.keySet().toArray( new String[0] );
   }

   public void setAttribute(String att, Object value)
   {
      if (value==null)
      {
         attributes.remove(att);
      }
      else
      {
         attributes.put(att, value);
      }
   }

   public void putValue(String att, Object value)
   {
      setAttribute(att, value);
   }

   public void removeAttribute(String att)
   {
      attributes.remove(att);
   }

   public void removeValue(String att)
   {
      removeAttribute(att);
   }

   public void invalidate()
   {
      attributes.clear();
      isInvalid = true;
   }

   public boolean isNew()
   {
      return false;
   }

   public Map<String, Object> getAttributes()
   {
      return attributes;
   }

   public ServletContext getServletContext()
   {
      return servletContext;
   }

   /*public void clear() {
      attributes.clear();
      isInvalid = false;
   }*/

}
