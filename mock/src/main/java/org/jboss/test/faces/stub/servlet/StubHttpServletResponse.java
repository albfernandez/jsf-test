package org.jboss.test.faces.stub.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class StubHttpServletResponse implements HttpServletResponse
{
   public void addCookie(Cookie cookie)
   {
   }

   public void addDateHeader(String arg0, long arg1)
   {
   }

   public void addHeader(String arg0, String arg1)
   {
   }

   public void addIntHeader(String arg0, int arg1)
   {
   }

   public boolean containsHeader(String arg0)
   {
      return false;
   }

   public String encodeRedirectURL(String arg0)
   {
      return null;
   }

   public String encodeRedirectUrl(String arg0)
   {
      return null;
   }

   public String encodeURL(String url)
   {
      return url;
   }

   @Deprecated
   public String encodeUrl(String url)
   {
      return encodeURL(url);
   }

   public void sendError(int arg0) throws IOException
   {
   }

   public void sendError(int arg0, String arg1) throws IOException
   {
   }

   public void sendRedirect(String arg0) throws IOException
   {
   }

   public void setDateHeader(String arg0, long arg1)
   {
   }

   public void setHeader(String arg0, String arg1)
   {
   }

   public void setIntHeader(String arg0, int arg1)
   {
   }

   public void setStatus(int arg0)
   {
   }

   public void setStatus(int arg0, String arg1)
   {
   }

   public void flushBuffer() throws IOException
   {
   }

   public int getBufferSize()
   {
      return 0;
   }

   public String getCharacterEncoding()
   {
      return null;
   }

   public String getContentType()
   {
      return null;
   }

   public Locale getLocale()
   {
      return null;
   }

   public ServletOutputStream getOutputStream() throws IOException
   {
      return null;
   }

   public PrintWriter getWriter() throws IOException
   {
      return null;
   }

   public boolean isCommitted()
   {
      return false;
   }

   public void reset()
   {
   }

   public void resetBuffer()
   {
   }

   public void setBufferSize(int arg0)
   {
   }

   public void setCharacterEncoding(String arg0)
   {
   }

   public void setContentLength(int arg0)
   {
   }

   public void setContentType(String arg0)
   {
   }

   public void setLocale(Locale arg0)
   {
   }

@Override
public int getStatus() {
	// TODO Auto-generated method stub
	return 0;
}

@Override
public String getHeader(String name) {
	// TODO Auto-generated method stub
	return null;
}

@Override
public Collection<String> getHeaders(String name) {
	// TODO Auto-generated method stub
	return null;
}

@Override
public Collection<String> getHeaderNames() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public void setContentLengthLong(long len) {
	// TODO Auto-generated method stub
	
}

@Override
public void sendRedirect(String location, int sc, boolean clearBuffer) throws IOException {
	// TODO Auto-generated method stub
	
}
}
