/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
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
package org.jboss.test.faces.jetty;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import org.eclipse.jetty.util.resource.Resource;


/**
 * @author Nick Belaevski
 * 
 */
public class StringContentResource extends Resource {

    private static final long serialVersionUID = -545867200766773535L;

    private byte[] content;
    
    private String path;
    
    public StringContentResource(String contentString, String path) {
        super();
        this.content = contentString.getBytes();
        this.path = path;
    }

    /*MZ
    @Override
    public Resource addPath(String path) throws IOException, MalformedURLException {
        throw new FileNotFoundException(path);
    }

    @Override
    public boolean delete() throws SecurityException {
        return false;
    }
    */

    @Override
    public boolean exists() {
        return true;
    }

    /*MZ
    @Override
    public File getFile() throws IOException {
        return null;
    }
	*/
    
    //MZ
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public String getName() {
        return path;
    }

    /*MZ
    @Override
    public OutputStream getOutputStream() throws IOException, SecurityException {
        throw new FileNotFoundException(path);
    }
	*/

    @Override
    public URI getURI() {
    	
    	
    	 try {
             URL tmpurl = new URL("urn",null,0,"", new URLStreamHandler() {
                 
                 @Override
                 protected URLConnection openConnection(URL u) throws IOException {
                	 
                	 return new URLConnection(u) {
                         
                         @Override
                         public void connect() throws IOException {
                         }
                         
                         @Override
                         public Object getContent() throws IOException {
                             return content;
                         }
                         
                         @Override
                         public InputStream getInputStream() throws IOException {
                             return StringContentResource.this.getInputStream();
                         }
                     };
                     
                 }
             });
             
             return tmpurl.toURI();
             
         } catch (MalformedURLException | URISyntaxException e) {
             //TODO handle
             e.printStackTrace();
             return null;
         }
    	
   
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public Instant lastModified() {
        return super.lastModified();
    }

    @Override
    public long length() {
        return content.length;
    }

    @Override
    public List<Resource> list() {
        return super.list();
    }

    /*MZ
    @Override
    public void release() {
    }

    @Override
    public boolean renameTo(Resource dest) throws SecurityException {
        return false;
    }
	*/
    
	@Override
	public Path getPath() {
		//MZ
		return Path.of(path);
	}

	@Override
	public boolean isReadable() {
		//MZ
		return true;
	}

	@Override
	public String getFileName() {
		//MZ
		return getPath().getFileName().toString();
	}

	@Override
	public Resource resolve(String subUriPath)  {
		//MZ
		return null;
	}

}
