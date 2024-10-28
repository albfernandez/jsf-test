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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.util.resource.Resource;
import org.jboss.test.faces.TestException;
import org.jboss.test.faces.staging.DirectoryMap;
import org.jboss.test.faces.staging.ServerResourcePath;

/**
 * @author Nick Belaevski
 * 
 */
public class VirtualDirectoryResource extends Resource {

    /**
     * 
     */
    private static final long serialVersionUID = 695315141388366832L;

    /**
     * 
     */
    private static final String[] EMPTY_STRINGS_ARRAY = new String[0];

    private String path;
    
    private DirectoryMap<Resource, VirtualDirectoryResource> directoryMap = new DirectoryMap<Resource, VirtualDirectoryResource>(this, JettyDirectoryMapAdapter.INSTANCE);
    
    public VirtualDirectoryResource(String path) {
        super();
        this.path = path;
    }

    /*MZ
    @Override
    public boolean delete() throws SecurityException {
        throw new SecurityException(path);
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

    @Override
    public InputStream getInputStream() throws IOException {
        throw new FileNotFoundException(path);
    }
	*/

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
    
    /*MZ
    @Override
    public URL getURL() {
        //necessary to avoid NPE for temp directories
        try {
            return new URL("jetty_virtual_resource", null, -1, path, VirtualResourceURLStreamHandler.INSTANCE);
        } catch (MalformedURLException e) {
            throw new TestException(e.getMessage(), e);
        }
    }
    */

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public Instant lastModified() {
        return super.lastModified();
    }

    @Override
    public long length() {
        return -1L;
    }

    @Override
    public List<Resource> list() {
        return new ArrayList<Resource>(directoryMap.getResources());
    }

    /*MZ
    @Override
    public void release() {
        for (Resource childResource : directoryMap.getResources()) {
            childResource.release();
        }
    }
	*/

    /*MZ
    @Override
    public boolean renameTo(Resource dest) throws SecurityException {
        throw new SecurityException(path);
    }
    */

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("VirtualResource: ");
        sb.append(path);

        File file = getPath().toFile();
		if (file != null) {
		    sb.append(", IsFile: ");
		    sb.append(file.getAbsolutePath());
		} else {
		    URL url;
			try {
				url = getURI().toURL();
				sb.append(", IsURL: ");
			    sb.append(url.toExternalForm());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

        return sb.toString();
    }

    @Override
    public Resource resolve(String path) {
        Resource resource = directoryMap.getResource(new ServerResourcePath("/" + path));
        if (resource == null) {
            resource = new BadResource(path);
        }
        
        return resource;
    }

    public VirtualDirectoryResource createChildDirectory(String name) {
        return directoryMap.addDirectory(name);
    }

    public void addResource(String string, Resource childResource) {
        directoryMap.addResource(new ServerResourcePath(string), childResource);
    }

    public void createChildDirectory(ServerResourcePath serverResourcePath) {
        directoryMap.addDirectory(serverResourcePath);
    }

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
	public URI getURI() {
		//MZ
		try {
			// URL(String protocol, String host, int port, String file, URLStreamHandler handler) 
			
            return new URL("http://", null, -1, path, VirtualResourceURLStreamHandler.INSTANCE).toURI();
        } catch (MalformedURLException e) {
            throw new TestException(e.getMessage(), e);
        } catch (URISyntaxException e) {
        	throw new TestException(e.getMessage(), e);
		}
	}

	@Override
	public String getFileName() {
		//MZ
		return getPath().getFileName().toString();
	}
}
